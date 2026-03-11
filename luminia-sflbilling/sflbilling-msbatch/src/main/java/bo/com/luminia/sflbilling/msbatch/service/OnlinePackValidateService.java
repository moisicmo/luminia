package bo.com.luminia.sflbilling.msbatch.service;

import bo.com.luminia.sflbilling.msbatch.repository.*;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatBroadcastType;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatInvoiceType;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatSectorDocumentType;
import bo.com.luminia.sflbilling.msbatch.service.dto.InvoiceMassiveDto;
import bo.com.luminia.sflbilling.msbatch.service.dto.InvoiceValidateDto;
import bo.com.luminia.sflbilling.msbatch.service.dto.InvoiceValidateMessageDto;
import bo.com.luminia.sflbilling.msbatch.service.utils.FileCompress;
import bo.com.luminia.sflbilling.msbatch.service.utils.FileHash;
import bo.com.luminia.sflbilling.domain.*;
import bo.com.luminia.sflbilling.domain.enumeration.InvoiceStatusEnum;
import bo.com.luminia.sflbilling.domain.enumeration.InvoiceWrapperEventStatusEnum;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import bo.com.luminia.sflbilling.domain.enumeration.WrapperEventStatusEnum;
import bo.com.luminia.sflbilling.msbatch.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msbatch.repository.*;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.CufdNotFoundException;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.CuisNotFoundException;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.DatetimeNotSync;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.SiatException;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.InvoicePackRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.ResponseMessages;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.SoapUtil;
import bo.gob.impuestos.sfe.electronicinvoice.*;
import bo.gob.impuestos.sfe.invoice.xml.XmlSectorType;
import bo.gob.impuestos.sfe.operation.RespuestaListaEventos;
import bo.gob.impuestos.sfe.operation.ServicioFacturacionOperaciones;
import bo.gob.impuestos.sfe.operation.SolicitudEventoSignificativo;
import bo.gob.impuestos.sfe.synchronization.ObjectFactory;
import bo.gob.impuestos.sfe.synchronization.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnlinePackValidateService extends ConnectivityBase {
    private final PendingToFixRepository pendingToFixRepository;

    private final Environment environment;
    private final SoapUtil soapUtil;

    private final OfflineRepository offlineRepository;
    private final EventRepository eventRepository;
    private final CuisRepository cuisRepository;
    private final CufdRepository cufdRepository;
    private final InvoiceRepository invoiceRepository;
    private final SectorDocumentTypeRepository sectorDocumentTypeRepository;
    private final WrapperEventRepository wrapperEventRepository;
    private final InvoiceWrapperEventRepository invoiceWrapperEventRepository;

    private final NotificationService notificationService;

    public synchronized void processPack() {
        if (this.verifyOffline()) {
            log.error("El sistema se encuentra fuera de Linea");
            return;
        }

        Optional<Event> entity = eventRepository.findFirstByActiveTrueOrderByIdAsc();
        if (!entity.isPresent())
            return;

        Event event = entity.get();
        this.emitOnlinePack(event);
    }

    /**
     * Reduce la cantidad de eventos a sincronizar según los casos.
     */
    public synchronized void reduceEventstSize(){
        log.debug("Entrando a reducir eventos antes de enviar paquetes");

        //Caso 1 identificado: Eventos que no tengan facturas
        eventRepository.disableEventWithoutInvoice();

        log.debug("Eventos reducidos");
    }


    public synchronized void processValidate() {
        if (this.verifyOffline()) {
            log.error("El sistema se encuentra fuera de Linea");
            return;
        }

        Optional<WrapperEvent> entity = wrapperEventRepository.findFirstByStatusOrderByIdAsc(WrapperEventStatusEnum.PENDING);
        if (!entity.isPresent())
            return;

        WrapperEvent wrapperEvent = entity.get();
        this.emitOnlineValidate(wrapperEvent);
    }

    private void emitOnlinePack(Event event) {
        List<Integer> sectorDocumentTypeList = SiatSectorDocumentType.map;
        Company company = event.getBranchOffice().getCompany();
        BranchOffice branchOffice = event.getBranchOffice();
        PointSale pointSale = event.getPointSale();

        Cuis cuis;
        Cufd cufd;

        try {
            cuis = this.obtainCuisActive(company.getId(), branchOffice.getBranchOfficeSiatId(), pointSale.getPointSaleSiatId());
            cufd = this.obtainCufdActive(company.getId(), branchOffice.getBranchOfficeSiatId(), pointSale.getPointSaleSiatId());
        } catch (Exception e) {
            event.setActive(false);
            eventRepository.save(event);
            log.error("Error sincronizando paquetes, cuis/cuf no activos para empresa id=", company.getId());
            notificationService.notifyEventPackError(company, branchOffice, pointSale, event);
            return;
        }


        int check = this.checkConnectivity(company.getToken(), environment, soapUtil);
        log.debug("1. Conexión verificada con el SIAT: {}", check);

        if (check != SiatResponseCodes.SIAT_HAS_CONNECTIVITY)
            throw new SiatException(ResponseMessages.ERROR_NO_SIAT_CONNECTION, ErrorEntities.INVOICE, ErrorKeys.ERR_SIAT_EXCEPTION);

        ZonedDateTime dateTimeFromSiat = ZonedDateTime.now();
        log.debug("2. Fecha y hora actual del servidor: {}", dateTimeFromSiat);
        /*
        ZonedDateTime dateTimeFromSiat = null;
        try {
            dateTimeFromSiat = this.obtainDateTimeFromSiat(company, cuis, branchOffice.getBranchOfficeSiatId(), pointSale.getPointSaleSiatId());
        } catch (DatetimeNotSync datetimeNotSync) {
            throw new DatetimeNotSync();
        }
        log.debug("2. Fecha y hora actual del SIAT: {}", dateTimeFromSiat);
        */

        try {
            Boolean hasInvoicePack = Boolean.TRUE;
            if (null == event.getReceptionCode()) {
                hasInvoicePack = this.populateEndDateInvoicePack(company, branchOffice, pointSale, event);
                if (hasInvoicePack) {
                    this.registerSignificantEventSiat(company, branchOffice, pointSale, cuis, cufd, event);
                    eventRepository.save(event);
                }
            }

            if (hasInvoicePack) {
                for (Integer sectorDocumentType : sectorDocumentTypeList)
                    this.sendPackInvoice(company, branchOffice, pointSale, cuis, cufd, event, dateTimeFromSiat, sectorDocumentType);
            }

            event.setActive(false);
            eventRepository.save(event);

            log.debug("6. Actualización del evento significativo: {}", event.getId());
        } catch (Exception e) {
            log.error("Error en envío de paquetes: event_id={}, company_id={}, companu_name={}", event.getId(), company.getId(), company.getName());
            throw new SiatException(e.getMessage(), ErrorEntities.INVOICE, ErrorKeys.ERR_SIAT_EXCEPTION);
        }
    }

    private void emitOnlineValidate(WrapperEvent wrapperEvent) {
        Company company = wrapperEvent.getBranchOffice().getCompany();
        BranchOffice branchOffice = wrapperEvent.getBranchOffice();
        PointSale pointSale = wrapperEvent.getPointSale();

        Cuis cuis = this.obtainCuisActive(company.getId(), branchOffice.getBranchOfficeSiatId(), pointSale.getPointSaleSiatId());
        Cufd cufd = this.obtainCufdActive(company.getId(), branchOffice.getBranchOfficeSiatId(), pointSale.getPointSaleSiatId());

        int check = this.checkConnectivity(company.getToken(), environment, soapUtil);
        log.debug("1. Conexión verificada con el SIAT: {}", check);
        if (check != SiatResponseCodes.SIAT_HAS_CONNECTIVITY)
            throw new SiatException(ResponseMessages.ERROR_NO_SIAT_CONNECTION, ErrorEntities.INVOICE, ErrorKeys.ERR_SIAT_EXCEPTION);

        InvoicePackRes invoicePackRes = this.sendValidatePackSiat(company, wrapperEvent, cuis, cufd);
        if (invoicePackRes.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT))
            throw new SiatException(invoicePackRes.getMessage());

        InvoiceValidateDto responseValidate = (InvoiceValidateDto) invoicePackRes.getBody();
        log.debug("2. Validación recepción de facturas: {}", responseValidate.getStatusCode());

        if (responseValidate.getStatusCode().equals(SiatResponseCodes.SIAT_WRAPPER_VALIDATED) || responseValidate.getStatusCode().equals(SiatResponseCodes.SIAT_WRAPPER_OBSERVED)) {
            wrapperEvent.setStatus(responseValidate.getStatusCode().equals(SiatResponseCodes.SIAT_WRAPPER_VALIDATED) ? WrapperEventStatusEnum.VALIDATED : WrapperEventStatusEnum.OBSERVED);
            wrapperEventRepository.save(wrapperEvent);

            List<InvoiceWrapperEvent> invoiceWrapperEventList = invoiceWrapperEventRepository.findAllByWrapperEventId(wrapperEvent.getId());
            for (InvoiceWrapperEvent invoiceWrapperEvent : invoiceWrapperEventList) {
                invoiceWrapperEvent.setStatus(InvoiceWrapperEventStatusEnum.VALIDATED);
                invoiceWrapperEventRepository.save(invoiceWrapperEvent);

                Invoice invoiceUpdate = invoiceWrapperEvent.getInvoice();
                invoiceUpdate.setReceptionCode(responseValidate.getReceptionCode());
                invoiceUpdate.setStatus(InvoiceStatusEnum.EMITTED);
                invoiceRepository.save(invoiceUpdate);
            }

            if (responseValidate.getStatusCode().equals(SiatResponseCodes.SIAT_WRAPPER_OBSERVED)) {
                for (InvoiceValidateMessageDto validateMessage : responseValidate.getValidationMessages()) {
                    InvoiceWrapperEvent invoiceWrapperEvent = invoiceWrapperEventList.stream().filter(x -> x.getFileNumber().equals(validateMessage.getFileNumber())).findFirst().get();
                    invoiceWrapperEvent.setStatus(InvoiceWrapperEventStatusEnum.OBSERVED);
                    invoiceWrapperEvent.setResponseMessage(validateMessage.getDescription());
                    invoiceWrapperEventRepository.save(invoiceWrapperEvent);

                    Invoice invoiceUpdate = invoiceWrapperEvent.getInvoice();
                    invoiceUpdate.setReceptionCode(null);
                    invoiceUpdate.setStatus(InvoiceStatusEnum.OBSERVED);
                    invoiceRepository.save(invoiceUpdate);
                }
            }
        }
    }

    private void sendPackInvoice(Company company, BranchOffice branchOffice, PointSale pointSale, Cuis cuis, Cufd cufd, Event event,
                                 ZonedDateTime dateTimeFromSiat, Integer sectorDocumentType) throws Exception {
        Integer numberInvoice = Integer.parseInt(environment.getProperty(ApplicationProperties.INVOICE_NUMBER_PACK));
        List listInvoice = null;
        do {
            listInvoice = invoiceRepository.findPackInvoicePending(company.getId(), branchOffice.getBranchOfficeSiatId(), pointSale.getPointSaleSiatId(),
                sectorDocumentType, event.getCufdEvent(), SiatBroadcastType.BROADCAST_TYPE_OFFLINE, numberInvoice);
            if (listInvoice.size() <= 0)
                return;

            List<InvoiceMassiveDto> listInvoices = ((ArrayList<Object[]>) listInvoice).stream().map(x -> {
                InvoiceMassiveDto invoice = new InvoiceMassiveDto();
                invoice.setId(((BigInteger) x[0]).longValue());
                invoice.setInvoiceXml(x[1].toString());
                return invoice;
            }).collect(Collectors.toList());
            log.debug("4. Cantidad de facturas paquete : {}", listInvoices.size());

            Integer sizeInvoice = listInvoices.size();
            byte[] tarXml = this.compressionTar(listInvoices);
            String hashXml = FileHash.sha256(tarXml);

            InvoicePackRes invoicePackRes;
            try {
                invoicePackRes = this.sendPackInvoiceSiat(company, branchOffice, pointSale, cuis, cufd, event, dateTimeFromSiat, tarXml, hashXml, sizeInvoice, sectorDocumentType);
                if (invoicePackRes.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT)) {
                    event.setActive(false);
                    notificationService.notifyEventNotSyncError(event, invoicePackRes.getMessage());
                    return;
                }
            }catch(SiatException e){
                notificationService.notifyEventNotSyncError(event, e.getDetail());
                registerIfPendingToFix(listInvoices, e.getDetail());
                event.setActive(false);
                return;
            }catch(Exception e){
                notificationService.notifyEventNotSyncError(event, e.getMessage());
                registerIfPendingToFix(listInvoices, e.getMessage());
                event.setActive(false);
                return;
            }

            //if (invoicePackRes.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT))
            //    throw new SiatException(invoicePackRes.getMessage());

            String receptionCode = invoicePackRes.getBody().toString();

            WrapperEvent wrapperEventNew = new WrapperEvent();
            wrapperEventNew.setReceptionCode(receptionCode);
            wrapperEventNew.setStatus(WrapperEventStatusEnum.PENDING);
            wrapperEventNew.setEvent(event);
            wrapperEventNew.setBranchOffice(branchOffice);
            wrapperEventNew.setPointSale(pointSale);
            wrapperEventNew.setSectorDocumentType(sectorDocumentTypeRepository.getBySiatIdAndCompanyIdAndActiveIsTrue(sectorDocumentType, company.getId().intValue()));
            WrapperEvent wrapperEvent = wrapperEventRepository.save(wrapperEventNew);

            for (int fileNumber = 0; fileNumber < listInvoices.size(); fileNumber++) {
                InvoiceWrapperEvent invoiceWrapperNew = new InvoiceWrapperEvent();
                invoiceWrapperNew.setFileNumber(fileNumber);
                invoiceWrapperNew.setResponseMessage(null);
                invoiceWrapperNew.setStatus(InvoiceWrapperEventStatusEnum.PENDING);
                invoiceWrapperNew.setInvoice(invoiceRepository.getById(listInvoices.get(fileNumber).getId()));
                invoiceWrapperNew.setWrapperEvent(wrapperEvent);
                invoiceWrapperEventRepository.save(invoiceWrapperNew);
            }
            log.debug("5. Registro del paquete de facturas");

        } while (listInvoice.size() == numberInvoice);
    }

    /**
     * Existe un error al sincronizar con impuestos. Si alguna de estas facturas están entre las pendientes
     * lo registro para su posterior tratamiento.
     *
     * @param listInvoices
     * @param errorDetail
     */
    private void registerIfPendingToFix(List<InvoiceMassiveDto> listInvoices, String errorDetail) {
        listInvoices.stream().forEach(inv->{
            Optional<PendingToFix> wpending = pendingToFixRepository.findByInvoiceId(inv.getId());
            if (wpending.isPresent()){
                PendingToFix pending =wpending.get();
                pending.setCounter(pending.getCounter()+1);
                pending.setFailReason(errorDetail);
                pendingToFixRepository.saveAndFlush(pending);
            }
        });
    }

    private InvoicePackRes sendPackInvoiceSiat(Company company, BranchOffice branchOffice, PointSale pointSale, Cuis cuis, Cufd cufd, Event event,
                                               ZonedDateTime dateTimeFromSiat, byte[] tarXml, String hashXml, Integer numberInvoice, Integer sectorDocumentType) {
        InvoicePackRes result = new InvoicePackRes();
        String receptionCode = "";
        String dateTimeSiat = this.convertZoneTimedateToString(dateTimeFromSiat);

        if (sectorDocumentType.equals(XmlSectorType.INVOICE_COMPRA_VENTA)) {
            bo.gob.impuestos.sfe.buysellinvoice.SolicitudRecepcionPaquete invoiceRequest = new bo.gob.impuestos.sfe.buysellinvoice.SolicitudRecepcionPaquete();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSale.getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(branchOffice.getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufd.getCufd());
            invoiceRequest.setCuis(cuis.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setArchivo(tarXml);
            invoiceRequest.setFechaEnvio(dateTimeSiat);
            invoiceRequest.setHashArchivo(hashXml);
            if (event.getCafc() != null)
                invoiceRequest.setCafc(event.getCafc());
            invoiceRequest.setCantidadFacturas(numberInvoice);
            invoiceRequest.setCodigoEvento(event.getReceptionCode());

            bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion service = soapUtil.getBuySellService(company.getToken());
            bo.gob.impuestos.sfe.buysellinvoice.RespuestaRecepcion response = service.recepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion())
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());
            receptionCode = response.getCodigoRecepcion();
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_ZONA_FRANCA) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_SEGUROS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_SECTOR_EDUCATIVO) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_FACTURAS_HOTELES) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_HOSPITAL_CLINICAS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_PREVALORADA)) {

            if (company.getModalitySiat().equals(ModalitySiatEnum.ELECTRONIC_BILLING)) {
                SolicitudRecepcionPaquete invoiceRequest = new SolicitudRecepcionPaquete();
                invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
                invoiceRequest.setCodigoPuntoVenta(pointSale.getPointSaleSiatId());
                invoiceRequest.setCodigoSistema(company.getSystemCode());
                invoiceRequest.setCodigoSucursal(branchOffice.getBranchOfficeSiatId());
                invoiceRequest.setNit(company.getNit());
                invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
                invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
                invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
                invoiceRequest.setCufd(cufd.getCufd());
                invoiceRequest.setCuis(cuis.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
                invoiceRequest.setArchivo(tarXml);
                invoiceRequest.setFechaEnvio(dateTimeSiat);
                invoiceRequest.setHashArchivo(hashXml);
                if (event.getCafc() != null)
                    invoiceRequest.setCafc(event.getCafc());
                invoiceRequest.setCantidadFacturas(numberInvoice);
                invoiceRequest.setCodigoEvento(event.getReceptionCode());

                ServicioFacturacion service = soapUtil.getElectronicService(company.getToken());
                RespuestaRecepcion response = service.recepcionPaqueteFactura(invoiceRequest);

                if (!response.isTransaccion())
                    throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                receptionCode = response.getCodigoRecepcion();
            } else {
                bo.gob.impuestos.sfe.computerizedinvoice.SolicitudRecepcionPaquete invoiceRequest = new bo.gob.impuestos.sfe.computerizedinvoice.SolicitudRecepcionPaquete();
                invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
                invoiceRequest.setCodigoPuntoVenta(pointSale.getPointSaleSiatId());
                invoiceRequest.setCodigoSistema(company.getSystemCode());
                invoiceRequest.setCodigoSucursal(branchOffice.getBranchOfficeSiatId());
                invoiceRequest.setNit(company.getNit());
                invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
                invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
                invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
                invoiceRequest.setCufd(cufd.getCufd());
                invoiceRequest.setCuis(cuis.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
                invoiceRequest.setArchivo(tarXml);
                invoiceRequest.setFechaEnvio(dateTimeSiat);
                invoiceRequest.setHashArchivo(hashXml);
                if (event.getCafc() != null)
                    invoiceRequest.setCafc(event.getCafc());
                invoiceRequest.setCantidadFacturas(numberInvoice);
                invoiceRequest.setCodigoEvento(event.getReceptionCode());

                bo.gob.impuestos.sfe.computerizedinvoice.ServicioFacturacion service = soapUtil.getComputerizedService(company.getToken());
                bo.gob.impuestos.sfe.computerizedinvoice.RespuestaRecepcion response = service.recepcionPaqueteFactura(invoiceRequest);

                if (!response.isTransaccion())
                    throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                receptionCode = response.getCodigoRecepcion();
            }
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_SERVICIOS_BASICOS)) {
            bo.gob.impuestos.sfe.basicserviceinvoice.SolicitudRecepcionPaquete invoiceRequest = new bo.gob.impuestos.sfe.basicserviceinvoice.SolicitudRecepcionPaquete();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSale.getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(branchOffice.getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufd.getCufd());
            invoiceRequest.setCuis(cuis.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setArchivo(tarXml);
            invoiceRequest.setFechaEnvio(dateTimeSiat);
            invoiceRequest.setHashArchivo(hashXml);
            if (event.getCafc() != null)
                invoiceRequest.setCafc(event.getCafc());
            invoiceRequest.setCantidadFacturas(numberInvoice);
            invoiceRequest.setCodigoEvento(event.getReceptionCode());

            bo.gob.impuestos.sfe.basicserviceinvoice.ServicioFacturacion service = soapUtil.getBasicService(company.getToken());
            bo.gob.impuestos.sfe.basicserviceinvoice.RespuestaRecepcion response = service.recepcionPaqueteFactura(invoiceRequest);

            //XXXXX
            //Error inesperado en recepcion de paquete: null

            if (!response.isTransaccion())
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());
            receptionCode = response.getCodigoRecepcion();
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS)) {
            bo.gob.impuestos.sfe.financialinvoice.SolicitudRecepcionPaquete invoiceRequest = new bo.gob.impuestos.sfe.financialinvoice.SolicitudRecepcionPaquete();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSale.getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(branchOffice.getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufd.getCufd());
            invoiceRequest.setCuis(cuis.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setArchivo(tarXml);
            invoiceRequest.setFechaEnvio(dateTimeSiat);
            invoiceRequest.setHashArchivo(hashXml);
            if (event.getCafc() != null)
                invoiceRequest.setCafc(event.getCafc());
            invoiceRequest.setCantidadFacturas(numberInvoice);
            invoiceRequest.setCodigoEvento(event.getReceptionCode());

            bo.gob.impuestos.sfe.financialinvoice.ServicioFacturacion service = soapUtil.getFinancialService(company.getToken());
            bo.gob.impuestos.sfe.financialinvoice.RespuestaRecepcion response = service.recepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion())
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());
            receptionCode = response.getCodigoRecepcion();
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_TELECOMUNICACIONES)) {
            bo.gob.impuestos.sfe.telecommunicationinvoice.SolicitudRecepcionPaquete invoiceRequest = new bo.gob.impuestos.sfe.telecommunicationinvoice.SolicitudRecepcionPaquete();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSale.getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(branchOffice.getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufd.getCufd());
            invoiceRequest.setCuis(cuis.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setArchivo(tarXml);
            invoiceRequest.setFechaEnvio(dateTimeSiat);
            invoiceRequest.setHashArchivo(hashXml);
            if (event.getCafc() != null)
                invoiceRequest.setCafc(event.getCafc());
            invoiceRequest.setCantidadFacturas(numberInvoice);
            invoiceRequest.setCodigoEvento(event.getReceptionCode());

            bo.gob.impuestos.sfe.telecommunicationinvoice.ServicioFacturacion service = soapUtil.getTelecommunicationsService(company.getToken());
            bo.gob.impuestos.sfe.telecommunicationinvoice.RespuestaRecepcion response = service.recepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion())
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());
            receptionCode = response.getCodigoRecepcion();
        }

        result.setCode(SiatResponseCodes.SUCCESS);
        result.setBody(receptionCode);
        log.debug("5. Resultado envío de paquete de facturas: {}", receptionCode);
        return result;
    }

    private InvoicePackRes sendValidatePackSiat(Company company, WrapperEvent wrapperEvent, Cuis cuis, Cufd cufd) {
        InvoicePackRes result = new InvoicePackRes();
        Integer sectorDocumentType = wrapperEvent.getSectorDocumentType().getSiatId();
        String receptionCode = wrapperEvent.getReceptionCode();
        InvoiceValidateDto responseValidate = new InvoiceValidateDto();

        if (sectorDocumentType.equals(XmlSectorType.INVOICE_COMPRA_VENTA)) {
            bo.gob.impuestos.sfe.buysellinvoice.SolicitudValidacionRecepcion invoiceRequest = new bo.gob.impuestos.sfe.buysellinvoice.SolicitudValidacionRecepcion();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(wrapperEvent.getPointSale().getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(wrapperEvent.getBranchOffice().getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufd.getCufd());
            invoiceRequest.setCuis(cuis.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoRecepcion(receptionCode);

            bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion service = soapUtil.getBuySellService(company.getToken());
            bo.gob.impuestos.sfe.buysellinvoice.RespuestaRecepcion response = service.validacionRecepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion())
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());

            responseValidate.setStatusCode(response.getCodigoEstado());
            responseValidate.setReceptionCode(response.getCodigoRecepcion());
            for (bo.gob.impuestos.sfe.buysellinvoice.MensajeRecepcion validateMessage : response.getMensajesList()) {
                if (null != validateMessage.getDescripcion() && !validateMessage.getDescripcion().contains("ADVERTENCIA")) {
                    responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
                }
            }
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_ZONA_FRANCA) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_SEGUROS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_SECTOR_EDUCATIVO) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_FACTURAS_HOTELES) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_HOSPITAL_CLINICAS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_PREVALORADA)) {

            if (company.getModalitySiat().equals(ModalitySiatEnum.ELECTRONIC_BILLING)) {
                SolicitudValidacionRecepcion invoiceRequest = new SolicitudValidacionRecepcion();
                invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
                invoiceRequest.setCodigoPuntoVenta(wrapperEvent.getPointSale().getPointSaleSiatId());
                invoiceRequest.setCodigoSistema(company.getSystemCode());
                invoiceRequest.setCodigoSucursal(wrapperEvent.getBranchOffice().getBranchOfficeSiatId());
                invoiceRequest.setNit(company.getNit());
                invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
                invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
                invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
                invoiceRequest.setCufd(cufd.getCufd());
                invoiceRequest.setCuis(cuis.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
                invoiceRequest.setCodigoRecepcion(receptionCode);

                ServicioFacturacion service = soapUtil.getElectronicService(company.getToken());
                RespuestaRecepcion response = service.validacionRecepcionPaqueteFactura(invoiceRequest);

                if (!response.isTransaccion())
                    throw new SiatException(response.getMensajesList().get(0).getDescripcion());

                responseValidate.setStatusCode(response.getCodigoEstado());
                responseValidate.setReceptionCode(response.getCodigoRecepcion());
                for (MensajeRecepcion validateMessage : response.getMensajesList()) {
                    if (null != validateMessage.getDescripcion() && !validateMessage.getDescripcion().contains("ADVERTENCIA")) {
                        responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
                    }
                }
            } else {
                bo.gob.impuestos.sfe.computerizedinvoice.SolicitudValidacionRecepcion invoiceRequest = new bo.gob.impuestos.sfe.computerizedinvoice.SolicitudValidacionRecepcion();
                invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
                invoiceRequest.setCodigoPuntoVenta(wrapperEvent.getPointSale().getPointSaleSiatId());
                invoiceRequest.setCodigoSistema(company.getSystemCode());
                invoiceRequest.setCodigoSucursal(wrapperEvent.getBranchOffice().getBranchOfficeSiatId());
                invoiceRequest.setNit(company.getNit());
                invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
                invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
                invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
                invoiceRequest.setCufd(cufd.getCufd());
                invoiceRequest.setCuis(cuis.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
                invoiceRequest.setCodigoRecepcion(receptionCode);

                bo.gob.impuestos.sfe.computerizedinvoice.ServicioFacturacion service = soapUtil.getComputerizedService(company.getToken());
                bo.gob.impuestos.sfe.computerizedinvoice.RespuestaRecepcion response = service.validacionRecepcionPaqueteFactura(invoiceRequest);

                if (!response.isTransaccion())
                    throw new SiatException(response.getMensajesList().get(0).getDescripcion());

                responseValidate.setStatusCode(response.getCodigoEstado());
                responseValidate.setReceptionCode(response.getCodigoRecepcion());
                for (bo.gob.impuestos.sfe.computerizedinvoice.MensajeRecepcion validateMessage : response.getMensajesList()) {
                    if (null != validateMessage.getDescripcion() && !validateMessage.getDescripcion().contains("ADVERTENCIA")) {
                        responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
                    }
                }
            }
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_SERVICIOS_BASICOS)) {
            bo.gob.impuestos.sfe.basicserviceinvoice.SolicitudValidacionRecepcion invoiceRequest = new bo.gob.impuestos.sfe.basicserviceinvoice.SolicitudValidacionRecepcion();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(wrapperEvent.getPointSale().getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(wrapperEvent.getBranchOffice().getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufd.getCufd());
            invoiceRequest.setCuis(cuis.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoRecepcion(receptionCode);

            bo.gob.impuestos.sfe.basicserviceinvoice.ServicioFacturacion service = soapUtil.getBasicService(company.getToken());
            bo.gob.impuestos.sfe.basicserviceinvoice.RespuestaRecepcion response = service.validacionRecepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion())
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());

            responseValidate.setStatusCode(response.getCodigoEstado());
            responseValidate.setReceptionCode(response.getCodigoRecepcion());
            for (bo.gob.impuestos.sfe.basicserviceinvoice.MensajeRecepcion validateMessage : response.getMensajesList()) {
                if (null != validateMessage.getDescripcion() && !validateMessage.getDescripcion().contains("ADVERTENCIA")) {
                    responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
                }
            }
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS)) {
            bo.gob.impuestos.sfe.financialinvoice.SolicitudValidacionRecepcion invoiceRequest = new bo.gob.impuestos.sfe.financialinvoice.SolicitudValidacionRecepcion();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(wrapperEvent.getPointSale().getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(wrapperEvent.getBranchOffice().getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufd.getCufd());
            invoiceRequest.setCuis(cuis.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoRecepcion(receptionCode);

            bo.gob.impuestos.sfe.financialinvoice.ServicioFacturacion service = soapUtil.getFinancialService(company.getToken());
            bo.gob.impuestos.sfe.financialinvoice.RespuestaRecepcion response = service.validacionRecepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion())
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());

            responseValidate.setStatusCode(response.getCodigoEstado());
            responseValidate.setReceptionCode(response.getCodigoRecepcion());
            for (bo.gob.impuestos.sfe.financialinvoice.MensajeRecepcion validateMessage : response.getMensajesList()) {
                if (null != validateMessage.getDescripcion() && !validateMessage.getDescripcion().contains("ADVERTENCIA")) {
                    responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
                }
            }
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_TELECOMUNICACIONES)) {
            bo.gob.impuestos.sfe.telecommunicationinvoice.SolicitudValidacionRecepcion invoiceRequest = new bo.gob.impuestos.sfe.telecommunicationinvoice.SolicitudValidacionRecepcion();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(wrapperEvent.getPointSale().getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(wrapperEvent.getBranchOffice().getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufd.getCufd());
            invoiceRequest.setCuis(cuis.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoRecepcion(receptionCode);

            bo.gob.impuestos.sfe.telecommunicationinvoice.ServicioFacturacion service = soapUtil.getTelecommunicationsService(company.getToken());
            bo.gob.impuestos.sfe.telecommunicationinvoice.RespuestaRecepcion response = service.validacionRecepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion())
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());

            responseValidate.setStatusCode(response.getCodigoEstado());
            responseValidate.setReceptionCode(response.getCodigoRecepcion());
            for (bo.gob.impuestos.sfe.telecommunicationinvoice.MensajeRecepcion validateMessage : response.getMensajesList()) {
                if (null != validateMessage.getDescripcion() && !validateMessage.getDescripcion().contains("ADVERTENCIA")) {
                    responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
                }
            }
        }

        result.setCode(SiatResponseCodes.SUCCESS);
        result.setBody(responseValidate);
        return result;
    }

    private void registerSignificantEventSiat(Company company, BranchOffice branchOffice, PointSale pointSale, Cuis cuis, Cufd cufd, Event event) throws Exception {
        SolicitudEventoSignificativo eventRequest = new SolicitudEventoSignificativo();

        eventRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
        eventRequest.setCodigoSistema(company.getSystemCode());
        eventRequest.setNit(company.getNit());
        eventRequest.setCuis(cuis.getCuis());
        eventRequest.setCufd(cufd.getCufd());
        eventRequest.setCodigoSucursal(branchOffice.getBranchOfficeSiatId());
        eventRequest.setCodigoPuntoVenta(new bo.gob.impuestos.sfe.operation.ObjectFactory().createSolicitudEventoSignificativoCodigoPuntoVenta(pointSale.getPointSaleSiatId()));
        eventRequest.setCodigoMotivoEvento(event.getSignificantEvent().getSiatId());
        eventRequest.setDescripcion(event.getDescription());
        XMLGregorianCalendar startDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(event.getStartDate()));
        eventRequest.setFechaHoraInicioEvento(startDate);
        XMLGregorianCalendar endDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(event.getEndDate()));
        eventRequest.setFechaHoraFinEvento(endDate);
        eventRequest.setCufdEvento(event.getCufdEvent());

        ServicioFacturacionOperaciones service = soapUtil.getOperationService(company.getToken());
        RespuestaListaEventos response = service.registroEventoSignificativo(eventRequest);

        if (!response.isTransaccion())
            throw new SiatException(response.getMensajesList().get(0).getDescripcion() + " " + eventRequest.getFechaHoraInicioEvento() + " " + eventRequest.getFechaHoraFinEvento());

        log.debug("3. Registro evento significativo: event_id={}  cod_recepcion={}", event.getId(), response.getCodigoRecepcionEventoSignificativo().intValue());
        event.setReceptionCode(response.getCodigoRecepcionEventoSignificativo().intValue());
    }

    private boolean populateEndDateInvoicePack(Company company, BranchOffice branchOffice, PointSale pointSale, Event event) {
        Timestamp endDateInvoice = invoiceRepository.findEndDatePackInvoicePending(company.getId(), branchOffice.getBranchOfficeSiatId(), pointSale.getPointSaleSiatId(),
            event.getCufdEvent(), SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
        if (null != endDateInvoice) {
            LocalDateTime endDate = endDateInvoice.toLocalDateTime();
            event.setEndDate(endDate.atZone(ZoneId.of("-04:00")));
            return true;
        }
        return false;
    }

    private Cuis obtainCuisActive(Long companyId, int branchOfficeSiat, int pointSaleSiat) throws CuisNotFoundException {
        Optional<Cuis> cuisOptional = cuisRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(pointSaleSiat, branchOfficeSiat, companyId);
        if (!cuisOptional.isPresent())
            throw new CuisNotFoundException();
        return cuisOptional.get();
    }

    private Cufd obtainCufdActive(Long companyId, int branchOfficeSiat, int pointSaleSiat) throws CufdNotFoundException {
        Optional<Cufd> cufdOptional = cufdRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(pointSaleSiat, branchOfficeSiat, companyId);
        if (!cufdOptional.isPresent())
            throw new CufdNotFoundException();
        return cufdOptional.get();
    }


    private ZonedDateTime obtainDateTimeFromSiat(Company company, Cuis cuis, int branchOfficeSiat, int pointSaleSiat) throws DatetimeNotSync {
        SolicitudSincronizacion request = new SolicitudSincronizacion();
        request.setNit(company.getNit());
        request.setCodigoSistema(company.getSystemCode());
        request.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
        request.setCodigoPuntoVenta(new ObjectFactory().createSolicitudSincronizacionCodigoPuntoVenta(pointSaleSiat));
        request.setCodigoSucursal(branchOfficeSiat);
        request.setCuis(cuis.getCuis());

        ServicioFacturacionSincronizacion serviceFechaHora = soapUtil.getSyncService(company.getToken());
        RespuestaFechaHora response = serviceFechaHora.sincronizarFechaHora(request);
        if (!response.isTransaccion())
            throw new DatetimeNotSync();
        return ZonedDateTime.parse(response.getFechaHora() + "-04:00[America/La_Paz]");
    }

    private byte[] compressionTar(List<InvoiceMassiveDto> listInvoices) throws Exception {
        String pathTempFolder = environment.getProperty(ApplicationProperties.INVOICE_PATH_TEMP_FOLDER);
        File fileTar = new File(pathTempFolder + System.currentTimeMillis() + ".tar");
        TarOutputStream tos = new TarOutputStream(new FileOutputStream(fileTar));
        for (InvoiceMassiveDto invoice : listInvoices) {
            TarEntry te = new TarEntry("/" + invoice.getId() + ".xml");
            byte[] data = invoice.getInvoiceXml().getBytes("UTF-8");
            te.setSize(data.length);
            tos.setLongFileMode(TarOutputStream.LONGFILE_GNU);
            tos.putNextEntry(te);
            tos.write(data);
            tos.closeEntry();
            tos.flush();
        }
        tos.close();

        byte[] file = FileCompress.fileToGzip(Files.readAllBytes(fileTar.toPath()));
        fileTar.delete();
        return file;
    }

    private String convertZoneTimedateToString(ZonedDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedString = time.format(formatter);
        return formattedString;
    }

    private boolean verifyOffline() {
        Optional<Offline> entity = offlineRepository.findAll().stream().findFirst();
        if (!entity.isPresent())
            return true;
        Offline offline = entity.get();
        return offline.getActive();
    }
}
