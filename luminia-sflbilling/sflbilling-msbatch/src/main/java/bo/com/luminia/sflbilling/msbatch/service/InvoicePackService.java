package bo.com.luminia.sflbilling.msbatch.service;

import bo.com.luminia.sflbilling.msbatch.repository.*;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatBroadcastType;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatInvoiceType;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msbatch.service.dto.InvoiceMassiveDto;
import bo.com.luminia.sflbilling.msbatch.service.dto.InvoiceValidateDto;
import bo.com.luminia.sflbilling.msbatch.service.dto.InvoiceValidateMessageDto;
import bo.com.luminia.sflbilling.msbatch.service.dto.PointSaleFullDto;
import bo.com.luminia.sflbilling.msbatch.service.utils.FileCompress;
import bo.com.luminia.sflbilling.msbatch.service.utils.FileHash;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.*;
import bo.com.luminia.sflbilling.domain.*;
import bo.com.luminia.sflbilling.domain.enumeration.InvoiceStatusEnum;
import bo.com.luminia.sflbilling.domain.enumeration.InvoiceWrapperEventStatusEnum;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import bo.com.luminia.sflbilling.domain.enumeration.WrapperEventStatusEnum;
import bo.com.luminia.sflbilling.msbatch.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msbatch.repository.*;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.*;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.InvoicePackReq;
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
import lombok.AllArgsConstructor;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class InvoicePackService extends ConnectivityBase{

    private final Environment environment;
    private final SoapUtil soapUtil;

    private final CompanyRepository companyRepository;
    private final PointSaleRepository pointSaleRepository;
    private final CuisRepository cuisRepository;
    private final CufdRepository cufdRepository;
    private final InvoiceRepository invoiceRepository;
    private final WrapperEventRepository wrapperEventRepository;
    private final InvoiceWrapperEventRepository invoiceWrapperEventRepository;
    private final BranchOfficeRepository branchOfficeRepository;
    private final SectorDocumentTypeRepository sectorDocumentTypeRepository;
    private final EventRepository eventRepository;

    /**
     * Método para la emisión del paquete.
     *
     * @param request Solicitud.
     * @return
     */
    public InvoicePackRes emitPack(InvoicePackReq request) {

        List<Integer> sectorDocumentTypeList = new ArrayList<>();
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_COMPRA_VENTA);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_SECTOR_EDUCATIVO);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_FACTURAS_HOTELES);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_HOSPITAL_CLINICAS);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_SERVICIOS_BASICOS);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_TELECOMUNICACIONES);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_PREVALORADA);

        // 1. Obtiene datos de la empresa.
        Optional<Company> companyFromDb = companyRepository.findByIdAndActiveTrue(request.getCompanyId());
        Company company = null;
        if (companyFromDb.isPresent()) {
            company = companyFromDb.get();
            log.debug("1. Recupera datos de la empresa : {}", company);
        }

        // 2. Obtiene la lista de puntos de venta activos.
        List listPointSale = pointSaleRepository.findAllByCompanyIdAndActive(company.getId());
        if (listPointSale.size() <= 0) {
            throw new RecordNotFoundException("No existen puntos de venta para la empresa.");
            //return new InvoicePackRes(SiatResponseCodes.ERROR_POINT_SALES_NOT_FOUND, "No existen puntos de venta para la empresa.");
        }
        // Mapea la lista de puntos de venta para la solicitud.
        List<PointSaleFullDto> listPointSales = ((ArrayList<Object[]>) listPointSale).stream().map(x -> {
            PointSaleFullDto obj = new PointSaleFullDto();
            obj.setBranchOfficeSiatId((int) x[1]);
            obj.setNameBranchOffice(x[2].toString());
            obj.setPointSaleSiatId((int) x[3]);
            obj.setNamePointSale(x[4].toString());
            return obj;
        }).collect(Collectors.toList());
        log.debug("2. Lista de puntos de venta : {}", listPointSales);

        // Itera la lista de puntos de venta.
        for (PointSaleFullDto pointSaleCurrent : listPointSales) {

            // 3. Obtiene el CUIS activo.
            Optional<Cuis> cuisOptional = null;
            try {
                cuisOptional = this.obtainCuisActive(company.getId(), pointSaleCurrent.getBranchOfficeSiatId(), pointSaleCurrent.getPointSaleSiatId());
            } catch (CuisNotFoundException e) {
                throw new CuisNotFoundException();
                //return new InvoicePackRes(SiatResponseCodes.ERROR_CUIS_NOT_FOUND, "Codigo Cuis no encontrado o no ha sido generado");
            }
            Cuis cuisFromDb = cuisOptional.get();
            log.debug("3. Datos del código CUIS : {}", cuisFromDb);

            // 4. Obtiene el CUFD activo.
            Optional<Cufd> cufdOptional = null;
            try {
                cufdOptional = this.obtainCufdActive(company.getId(), pointSaleCurrent.getBranchOfficeSiatId(), pointSaleCurrent.getPointSaleSiatId());
            } catch (CufdNotFoundException e) {
                throw new CufdNotFoundException();
                //return new InvoicePackRes(SiatResponseCodes.ERROR_CUFD_NOT_FOUND, "Codigo Cufd no encontrado o no ha sido generado");
            }
            Cufd cufdFromDb = cufdOptional.get();
            log.debug("4. Datos del código CUFD : {}", cufdFromDb);

            // 5. Verifica la comunicacion con los servicios del SIAT.
            int check = this.checkConnectivity(company.getToken(), environment, soapUtil);
            log.debug("5. Conexión verificada : {}", check);
            if (check != SiatResponseCodes.SIAT_HAS_CONNECTIVITY) {
                throw new SiatException(ResponseMessages.ERROR_NO_SIAT_CONNECTION,
                    ErrorEntities.INVOICE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
            }

            // 6. Obtiene fecha y hora actual del SIAT.
            /*ZonedDateTime dateTimeFromSiat = null;
            try {
                dateTimeFromSiat = this.obtainDateTimeFromSiat(company, cuisFromDb, pointSaleCurrent.getBranchOfficeSiatId(), pointSaleCurrent.getPointSaleSiatId());
            } catch (DatetimeNotSync datetimeNotSync) {
                throw new DatetimeNotSync();
            }
            log.debug("6. Fecha y hora actual del SIAT: {}", dateTimeFromSiat);
            */
            ZonedDateTime dateTimeFromSiat = ZonedDateTime.now();
            log.debug("6. Fecha y hora actual del servidor: {}", dateTimeFromSiat);


            List<Event> event = eventRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(pointSaleCurrent.getBranchOfficeSiatId(), pointSaleCurrent.getPointSaleSiatId(), company.getId());
            if (event.isEmpty()) {
                throw new SiatException("Evento significativo no encontrado o no ha sido generado",
                    ErrorEntities.INVOICE,
                    ErrorKeys.ERR_EVENT_NOT_FOUND);
                //return new InvoicePackRes(SiatResponseCodes.ERROR_EVENT_NOT_FOUND, "Evento significativo no encontrado o no ha sido generado");
            }
            Event eventCurrent = event.get(0);
            log.debug("7. Datos del evento significativo actual : {}", eventCurrent);

            try {
                // Registro del evento significativo en el SIAT.
                InvoicePackRes responseRegister = this.registerSignificantEventSiat(company, pointSaleCurrent, cuisFromDb, cufdFromDb, eventCurrent);
                if (responseRegister.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT)) {
                    throw new SiatException(responseRegister.getMessage(),
                        ErrorEntities.INVOICE,
                        ErrorKeys.ERR_SIAT_EXCEPTION);
                    //return responseRegister;
                }
                Long eventCode = (Long) responseRegister.getBody();

                for (Integer sectorDocumentType : sectorDocumentTypeList) {
                    // Envío paquete de facturas por tipo documento sector.
                    InvoicePackRes response = this.sendPackInvoice(company, pointSaleCurrent, cuisFromDb, cufdFromDb, eventCurrent, eventCode, dateTimeFromSiat, sectorDocumentType);
                    if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
                        return response;
                    }
                }

                // Actualiza el evento significativo actual.
                eventCurrent.setEndDate(dateTimeFromSiat);
                eventCurrent.setReceptionCode(eventCode.intValue());
                eventCurrent.setActive(false);
                eventRepository.save(eventCurrent);
                log.debug("6. Actualización del evento significativo: {}", eventCurrent);

            } catch (Exception e) {
                throw new SiatException(e.getMessage(),
                    ErrorEntities.INVOICE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return new InvoicePackRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Ocurrio un error en la recepción masiva de las facturas.");
            }
        }
        return new InvoicePackRes(SiatResponseCodes.SUCCESS, "El envío de paquete de facturas ha terminado correctamente");
    }

    /**
     * Método para la validación del paquete de facturas.
     *
     * @param companyId Identificador de la empresa.
     * @return
     */
    public InvoicePackRes validatePack(long companyId, boolean online) {
        // 1. Obtiene datos de la empresa.
        Optional<Company> companyFromDb = companyRepository.findByIdAndActiveTrue(companyId);
        Company company = null;
        if (companyFromDb.isPresent()) {
            company = companyFromDb.get();
            log.debug("1. Recupera datos de la empresa : {}", company);
        }

        // 2. Obtiene la lista de paquetes para validación.
        List<WrapperEvent> wrapperEventList = wrapperEventRepository.findPendingByCompanyId(company.getId());
        if (wrapperEventList.size() <= 0) {
            if (online)
                return new InvoicePackRes();

            throw new RecordNotFoundException("No existe paquetes para validacion");
        }
        log.debug("2. Cantidad de paquetes para validación : {}", wrapperEventList.size());

        // Itera la lista de paquetes.
        for (WrapperEvent wrapperEvent : wrapperEventList) {

            // 3. Obtiene el CUIS activo.
            Optional<Cuis> cuisOptional = null;
            try {
                cuisOptional = this.obtainCuisActive(company.getId(), wrapperEvent.getBranchOffice().getBranchOfficeSiatId(), wrapperEvent.getPointSale().getPointSaleSiatId());
            } catch (CuisNotFoundException e) {
                throw new CuisNotFoundException();
            }
            Cuis cuisFromDb = cuisOptional.get();
            log.debug("3. Datos del código CUIS : {}", cuisFromDb);

            // 4. Obtiene el CUFD activo.
            Optional<Cufd> cufdOptional = null;
            try {
                cufdOptional = this.obtainCufdActive(company.getId(), wrapperEvent.getBranchOffice().getBranchOfficeSiatId(), wrapperEvent.getPointSale().getPointSaleSiatId());
            } catch (CufdNotFoundException e) {
                throw new CufdNotFoundException();
            }
            Cufd cufdFromDb = cufdOptional.get();
            log.debug("4. Datos del código CUFD : {}", cufdFromDb);

            // 5. Verifica la comunicacion con los servicios del SIAT.
            int check = this.checkConnectivity(company.getToken(), environment, soapUtil);
            log.debug("5. Conexión verificada : {}", check);
            if (check != SiatResponseCodes.SIAT_HAS_CONNECTIVITY) {
                throw new SiatException(ResponseMessages.ERROR_NO_SIAT_CONNECTION);
            }

            // 6. Creación de la solicitud de validación.
            InvoicePackRes invoicePackRes = this.sendValidatePackSiat(company, wrapperEvent, cuisFromDb, cufdFromDb);
            if (invoicePackRes.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT)) {
                throw new SiatException(invoicePackRes.getMessage());
                //return invoicePackRes;
            }

            InvoiceValidateDto responseValidate = (InvoiceValidateDto) invoicePackRes.getBody();

            // Verifica el estado de la validación.
            if (responseValidate.getStatusCode().equals(SiatResponseCodes.SIAT_WRAPPER_VALIDATED) ||
                responseValidate.getStatusCode().equals(SiatResponseCodes.SIAT_WRAPPER_OBSERVED)) {

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

                // Marca las facturas en caso de observados.
                if (responseValidate.getStatusCode().equals(SiatResponseCodes.SIAT_WRAPPER_OBSERVED)) {
                    // Itera la lista de validaciones.
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
            if (responseValidate.getStatusCode().equals(SiatResponseCodes.SIAT_WRAPPER_PENDING)) {
                continue;
            }
            log.debug("6. Resultado validación recepción de la factura masiva: {}", responseValidate);
        }

        return new InvoicePackRes(SiatResponseCodes.SUCCESS, "La validación de la recepción de paquetes termino correctamente.");
    }

    /**
     * Método para el envío de paquete de facturas.
     *
     * @param company            Empresa.
     * @param pointSaleCurrent   Punto de venta.
     * @param cuisFromDb         Código único del sistema.
     * @param cufdFromDb         Código único diario.
     * @param eventCurrent       Evento actual.
     * @param eventCode          Código de recepción del evento significativo.
     * @param dateTimeFromSiat   Fecha actual SIAT.
     * @param sectorDocumentType Tipo de documento sector.
     * @return
     * @throws Exception
     */
    private InvoicePackRes sendPackInvoice(Company company, PointSaleFullDto pointSaleCurrent, Cuis cuisFromDb, Cufd cufdFromDb, Event eventCurrent, Long eventCode,
                                           ZonedDateTime dateTimeFromSiat, Integer sectorDocumentType) throws Exception {
        Integer numberInvoice = Integer.parseInt(environment.getProperty(ApplicationProperties.INVOICE_NUMBER_PACK));
        List listInvoice = null;
        do {
            // 9. Obtiene la lista de facturas.
            listInvoice = invoiceRepository.findAllPackInvoice(company.getId(),
                pointSaleCurrent.getBranchOfficeSiatId(), pointSaleCurrent.getPointSaleSiatId(), sectorDocumentType,
                SiatBroadcastType.BROADCAST_TYPE_OFFLINE, numberInvoice);
            if (listInvoice.size() <= 0) {
                return new InvoicePackRes(SiatResponseCodes.SUCCESS, "");
            }
            // Mapea la lista de facturas.
            List<InvoiceMassiveDto> listInvoices = ((ArrayList<Object[]>) listInvoice).stream().map(x -> {
                InvoiceMassiveDto invoice = new InvoiceMassiveDto();
                invoice.setId(((BigInteger) x[0]).longValue());
                invoice.setInvoiceXml(x[1].toString());
                return invoice;
            }).collect(Collectors.toList());
            log.debug("9. Cantidad de facturas paquete : {}", listInvoices.size());

            byte[] tarXml = this.compressionTar(listInvoices);
            log.debug("10. Compresión de las facturas encontradas.");

            // 11. Obtiene el hash del documento compreso.
            String hashXml = FileHash.sha256(tarXml);
            log.debug("11. Hash del documento compreso: {}", hashXml);

            // 12. Realiza la recepción masiva de las facturas en el SIAT.
            InvoicePackRes invoicePackRes = this.sendPackInvoiceSiat(company, pointSaleCurrent, cuisFromDb, cufdFromDb, eventCode, eventCurrent,
                dateTimeFromSiat, tarXml, hashXml, listInvoices.size(), sectorDocumentType);
            if (invoicePackRes.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT)) {
                throw new SiatException(invoicePackRes.getMessage());
                //return invoicePackRes;
            }
            String receptionCode = invoicePackRes.getBody().toString();

            // 13. Registra el paquete de facturas.
            WrapperEvent wrapperEventNew = new WrapperEvent();
            wrapperEventNew.setReceptionCode(receptionCode);
            wrapperEventNew.setStatus(WrapperEventStatusEnum.PENDING);
            wrapperEventNew.setEvent(eventCurrent);
            wrapperEventNew.setBranchOffice(branchOfficeRepository.findByBranchOfficeSiatIdActive(pointSaleCurrent.getBranchOfficeSiatId(), company.getId()));
            wrapperEventNew.setPointSale(pointSaleRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(pointSaleCurrent.getPointSaleSiatId(), pointSaleCurrent.getBranchOfficeSiatId(), company.getId()));
            wrapperEventNew.setSectorDocumentType(sectorDocumentTypeRepository.getBySiatIdAndCompanyIdAndActiveIsTrue(sectorDocumentType, company.getId().intValue()));
            WrapperEvent wrapperEvent = wrapperEventRepository.save(wrapperEventNew);

            // 14. Itera la lista de facturas para registro.
            for (int fileNumber = 0; fileNumber < listInvoices.size(); fileNumber++) {
                InvoiceWrapperEvent invoiceWrapperNew = new InvoiceWrapperEvent();
                invoiceWrapperNew.setFileNumber(fileNumber);
                invoiceWrapperNew.setResponseMessage(null);
                invoiceWrapperNew.setStatus(InvoiceWrapperEventStatusEnum.PENDING);
                invoiceWrapperNew.setInvoice(invoiceRepository.getById(listInvoices.get(fileNumber).getId()));
                invoiceWrapperNew.setWrapperEvent(wrapperEvent);
                invoiceWrapperEventRepository.save(invoiceWrapperNew);
            }
            log.debug("14. Registro del paquete de facturas");

        } while (listInvoice.size() == numberInvoice);

        return new InvoicePackRes(SiatResponseCodes.SUCCESS, "");
    }

    /**
     * Método para el envío del paquete de facturas al servicio del SIAT.
     *
     * @param company            Empresa.
     * @param pointSaleCurrent   Punto de venta actual.
     * @param cuisFromDb         Código único del sistema.
     * @param cufdFromDb         Código único diario.
     * @param eventCode          Código evento significativo.
     * @param dateTimeFromSiat   Fecha y hora actual.
     * @param tarXml             Paquete de facturas.
     * @param hashXml            Hash del paquete de facturas.
     * @param numberInvoice      Número de facturas del paquete.
     * @param sectorDocumentType Tipo de documento sector.
     * @return
     * @throws Exception
     */
    private InvoicePackRes sendPackInvoiceSiat(Company company, PointSaleFullDto pointSaleCurrent, Cuis cuisFromDb, Cufd cufdFromDb, Long eventCode, Event eventCurrent,
                                               ZonedDateTime dateTimeFromSiat, byte[] tarXml, String hashXml, Integer numberInvoice, Integer sectorDocumentType) throws Exception {
        InvoicePackRes result = new InvoicePackRes();
        String receptionCode = "";
        String dateTimeSiat = convertZoneTimedateToString(dateTimeFromSiat);

        // Identifica el tipo de factura para la envío del paquete.
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_COMPRA_VENTA)) {
            bo.gob.impuestos.sfe.buysellinvoice.SolicitudRecepcionPaquete invoiceRequest = new bo.gob.impuestos.sfe.buysellinvoice.SolicitudRecepcionPaquete();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleCurrent.getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(pointSaleCurrent.getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setArchivo(tarXml);
            invoiceRequest.setFechaEnvio(dateTimeSiat);
            invoiceRequest.setHashArchivo(hashXml);
            if (eventCurrent.getCafc() != null)
                invoiceRequest.setCafc(eventCurrent.getCafc());
            invoiceRequest.setCantidadFacturas(numberInvoice);
            invoiceRequest.setCodigoEvento(eventCode);

            bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion service = soapUtil.getBuySellService(company.getToken());
            bo.gob.impuestos.sfe.buysellinvoice.RespuestaRecepcion response = service.recepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion()) {
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                //return new InvoicePackRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            receptionCode = response.getCodigoRecepcion();
            log.debug("12. Resultado envío de paquete de facturas: {}", response);
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
                invoiceRequest.setCodigoPuntoVenta(pointSaleCurrent.getPointSaleSiatId());
                invoiceRequest.setCodigoSistema(company.getSystemCode());
                invoiceRequest.setCodigoSucursal(pointSaleCurrent.getBranchOfficeSiatId());
                invoiceRequest.setNit(company.getNit());
                invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
                invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
                invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
                invoiceRequest.setCufd(cufdFromDb.getCufd());
                invoiceRequest.setCuis(cuisFromDb.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
                invoiceRequest.setArchivo(tarXml);
                invoiceRequest.setFechaEnvio(dateTimeSiat);
                invoiceRequest.setHashArchivo(hashXml);
                if (eventCurrent.getCafc() != null)
                    invoiceRequest.setCafc(eventCurrent.getCafc());
                invoiceRequest.setCantidadFacturas(numberInvoice);
                invoiceRequest.setCodigoEvento(eventCode);

                ServicioFacturacion service = soapUtil.getElectronicService(company.getToken());
                RespuestaRecepcion response = service.recepcionPaqueteFactura(invoiceRequest);

                if (!response.isTransaccion()) {
                    throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                    //return new InvoicePackRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
                }
                receptionCode = response.getCodigoRecepcion();
                log.debug("12. Resultado envío de paquete de facturas: {}", response);
            } else {
                bo.gob.impuestos.sfe.computerizedinvoice.SolicitudRecepcionPaquete invoiceRequest = new bo.gob.impuestos.sfe.computerizedinvoice.SolicitudRecepcionPaquete();
                invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
                invoiceRequest.setCodigoPuntoVenta(pointSaleCurrent.getPointSaleSiatId());
                invoiceRequest.setCodigoSistema(company.getSystemCode());
                invoiceRequest.setCodigoSucursal(pointSaleCurrent.getBranchOfficeSiatId());
                invoiceRequest.setNit(company.getNit());
                invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
                invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
                invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
                invoiceRequest.setCufd(cufdFromDb.getCufd());
                invoiceRequest.setCuis(cuisFromDb.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
                invoiceRequest.setArchivo(tarXml);
                invoiceRequest.setFechaEnvio(dateTimeSiat);
                invoiceRequest.setHashArchivo(hashXml);
                if (eventCurrent.getCafc() != null)
                    invoiceRequest.setCafc(eventCurrent.getCafc());
                invoiceRequest.setCantidadFacturas(numberInvoice);
                invoiceRequest.setCodigoEvento(eventCode);

                bo.gob.impuestos.sfe.computerizedinvoice.ServicioFacturacion service = soapUtil.getComputerizedService(company.getToken());
                bo.gob.impuestos.sfe.computerizedinvoice.RespuestaRecepcion response = service.recepcionPaqueteFactura(invoiceRequest);

                if (!response.isTransaccion()) {
                    throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                    //return new InvoicePackRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
                }
                receptionCode = response.getCodigoRecepcion();
                log.debug("12. Resultado envío de paquete de facturas: {}", response);
            }
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_SERVICIOS_BASICOS)) {
            bo.gob.impuestos.sfe.basicserviceinvoice.SolicitudRecepcionPaquete invoiceRequest = new bo.gob.impuestos.sfe.basicserviceinvoice.SolicitudRecepcionPaquete();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleCurrent.getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(pointSaleCurrent.getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setArchivo(tarXml);
            invoiceRequest.setFechaEnvio(dateTimeSiat);
            invoiceRequest.setHashArchivo(hashXml);
            if (eventCurrent.getCafc() != null)
                invoiceRequest.setCafc(eventCurrent.getCafc());
            invoiceRequest.setCantidadFacturas(numberInvoice);
            invoiceRequest.setCodigoEvento(eventCode);

            bo.gob.impuestos.sfe.basicserviceinvoice.ServicioFacturacion service = soapUtil.getBasicService(company.getToken());
            bo.gob.impuestos.sfe.basicserviceinvoice.RespuestaRecepcion response = service.recepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion()) {
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                //return new InvoicePackRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            receptionCode = response.getCodigoRecepcion();
            log.debug("12. Resultado envío de paquete de facturas: {}", response);
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS)) {
            bo.gob.impuestos.sfe.financialinvoice.SolicitudRecepcionPaquete invoiceRequest = new bo.gob.impuestos.sfe.financialinvoice.SolicitudRecepcionPaquete();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleCurrent.getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(pointSaleCurrent.getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setArchivo(tarXml);
            invoiceRequest.setFechaEnvio(dateTimeSiat);
            invoiceRequest.setHashArchivo(hashXml);
            if (eventCurrent.getCafc() != null)
                invoiceRequest.setCafc(eventCurrent.getCafc());
            invoiceRequest.setCantidadFacturas(numberInvoice);
            invoiceRequest.setCodigoEvento(eventCode);

            bo.gob.impuestos.sfe.financialinvoice.ServicioFacturacion service = soapUtil.getFinancialService(company.getToken());
            bo.gob.impuestos.sfe.financialinvoice.RespuestaRecepcion response = service.recepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion()) {
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                //return new InvoicePackRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            receptionCode = response.getCodigoRecepcion();
            log.debug("12. Resultado envío de paquete de facturas: {}", response);
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_TELECOMUNICACIONES)) {
            bo.gob.impuestos.sfe.telecommunicationinvoice.SolicitudRecepcionPaquete invoiceRequest = new bo.gob.impuestos.sfe.telecommunicationinvoice.SolicitudRecepcionPaquete();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleCurrent.getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(pointSaleCurrent.getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setArchivo(tarXml);
            invoiceRequest.setFechaEnvio(dateTimeSiat);
            invoiceRequest.setHashArchivo(hashXml);
            if (eventCurrent.getCafc() != null)
                invoiceRequest.setCafc(eventCurrent.getCafc());
            invoiceRequest.setCantidadFacturas(numberInvoice);
            invoiceRequest.setCodigoEvento(eventCode);

            bo.gob.impuestos.sfe.telecommunicationinvoice.ServicioFacturacion service = soapUtil.getTelecommunicationsService(company.getToken());
            bo.gob.impuestos.sfe.telecommunicationinvoice.RespuestaRecepcion response = service.recepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion()) {
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                //return new InvoicePackRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            receptionCode = response.getCodigoRecepcion();
            log.debug("12. Resultado envío de paquete de facturas: {}", response);
        }

        result.setCode(SiatResponseCodes.SUCCESS);
        result.setBody(receptionCode);
        return result;
    }

    /**
     * Método que registra el evento significativo al servicio del SIAT.
     *
     * @param company          Empresa.
     * @param pointSaleCurrent Punto de venta.
     * @param cuisFromDb       Código Único del Sistema.
     * @param cufdFromDb       Código Único diario.
     * @param eventCurrent     Evento significativo actual.
     * @return
     * @throws Exception
     */
    private InvoicePackRes registerSignificantEventSiat(Company company, PointSaleFullDto pointSaleCurrent, Cuis cuisFromDb, Cufd cufdFromDb, Event eventCurrent) throws Exception {
        InvoicePackRes result = new InvoicePackRes();

        SolicitudEventoSignificativo eventRequest = new SolicitudEventoSignificativo();
        eventRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
        eventRequest.setCodigoSistema(company.getSystemCode());
        eventRequest.setNit(company.getNit());
        eventRequest.setCuis(cuisFromDb.getCuis());
        eventRequest.setCufd(cufdFromDb.getCufd());
        eventRequest.setCodigoSucursal(pointSaleCurrent.getBranchOfficeSiatId());
        eventRequest.setCodigoPuntoVenta(new bo.gob.impuestos.sfe.operation.ObjectFactory().createSolicitudEventoSignificativoCodigoPuntoVenta(pointSaleCurrent.getPointSaleSiatId()));
        eventRequest.setCodigoMotivoEvento(eventCurrent.getSignificantEvent().getSiatId());
        eventRequest.setDescripcion(eventCurrent.getDescription());
        XMLGregorianCalendar startDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(eventCurrent.getStartDate()));
        eventRequest.setFechaHoraInicioEvento(startDate);
        XMLGregorianCalendar endDate;
        if (null == eventCurrent.getReceptionCode()) {
            endDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(cufdFromDb.getStartDate()));
        } else {
            endDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(eventCurrent.getEndDate()));
        }
        eventRequest.setFechaHoraFinEvento(endDate);
        eventRequest.setCufdEvento(eventCurrent.getCufdEvent());

        ServicioFacturacionOperaciones service = soapUtil.getOperationService(company.getToken());
        RespuestaListaEventos response = service.registroEventoSignificativo(eventRequest);

        log.debug("8. Resultado registro evento significativo: {}", response);

        if (!response.isTransaccion()) {
            throw new SiatException(response.getMensajesList().get(0).getDescripcion()
                + " " + eventRequest.getFechaHoraInicioEvento() + " " + eventRequest.getFechaHoraFinEvento());
            //return new InvoicePackRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion() + " " + eventRequest.getFechaHoraInicioEvento() + " " + eventRequest.getFechaHoraFinEvento());
        }

        result.setCode(SiatResponseCodes.SUCCESS);
        result.setBody(response.getCodigoRecepcionEventoSignificativo());
        return result;
    }

    /**
     * Método para envío a validación al servicio del SIAT.
     *
     * @param company      Empresa.
     * @param wrapperEvent Paquete.
     * @param cuisFromDb   Código único del sistema.
     * @param cufdFromDb   Código único diario.
     * @return
     */
    private InvoicePackRes sendValidatePackSiat(Company company, WrapperEvent wrapperEvent, Cuis cuisFromDb, Cufd cufdFromDb) {
        InvoicePackRes result = new InvoicePackRes();
        Integer sectorDocumentType = wrapperEvent.getSectorDocumentType().getSiatId();
        String receptionCode = wrapperEvent.getReceptionCode();
        InvoiceValidateDto responseValidate = new InvoiceValidateDto();

        // Identifica el tipo de factura para validación.
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
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoRecepcion(receptionCode);

            bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion service = soapUtil.getBuySellService(company.getToken());
            bo.gob.impuestos.sfe.buysellinvoice.RespuestaRecepcion response = service.validacionRecepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion()) {
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                //return new InvoicePackRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            responseValidate.setStatusCode(response.getCodigoEstado());
            responseValidate.setReceptionCode(response.getCodigoRecepcion());
            for (bo.gob.impuestos.sfe.buysellinvoice.MensajeRecepcion validateMessage : response.getMensajesList()) {
                responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
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
                invoiceRequest.setCufd(cufdFromDb.getCufd());
                invoiceRequest.setCuis(cuisFromDb.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
                invoiceRequest.setCodigoRecepcion(receptionCode);

                ServicioFacturacion service = soapUtil.getElectronicService(company.getToken());
                RespuestaRecepcion response = service.validacionRecepcionPaqueteFactura(invoiceRequest);

                if (!response.isTransaccion()) {
                    throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                    //return new InvoicePackRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
                }
                responseValidate.setStatusCode(response.getCodigoEstado());
                responseValidate.setReceptionCode(response.getCodigoRecepcion());
                for (MensajeRecepcion validateMessage : response.getMensajesList()) {
                    responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
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
                invoiceRequest.setCufd(cufdFromDb.getCufd());
                invoiceRequest.setCuis(cuisFromDb.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
                invoiceRequest.setCodigoRecepcion(receptionCode);

                bo.gob.impuestos.sfe.computerizedinvoice.ServicioFacturacion service = soapUtil.getComputerizedService(company.getToken());
                bo.gob.impuestos.sfe.computerizedinvoice.RespuestaRecepcion response = service.validacionRecepcionPaqueteFactura(invoiceRequest);

                if (!response.isTransaccion()) {
                    throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                    //return new InvoicePackRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
                }
                responseValidate.setStatusCode(response.getCodigoEstado());
                responseValidate.setReceptionCode(response.getCodigoRecepcion());
                for (bo.gob.impuestos.sfe.computerizedinvoice.MensajeRecepcion validateMessage : response.getMensajesList()) {
                    responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
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
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoRecepcion(receptionCode);

            bo.gob.impuestos.sfe.basicserviceinvoice.ServicioFacturacion service = soapUtil.getBasicService(company.getToken());
            bo.gob.impuestos.sfe.basicserviceinvoice.RespuestaRecepcion response = service.validacionRecepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion()) {
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                //return new InvoicePackRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            responseValidate.setStatusCode(response.getCodigoEstado());
            responseValidate.setReceptionCode(response.getCodigoRecepcion());
            for (bo.gob.impuestos.sfe.basicserviceinvoice.MensajeRecepcion validateMessage : response.getMensajesList()) {
                responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
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
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoRecepcion(receptionCode);

            bo.gob.impuestos.sfe.financialinvoice.ServicioFacturacion service = soapUtil.getFinancialService(company.getToken());
            bo.gob.impuestos.sfe.financialinvoice.RespuestaRecepcion response = service.validacionRecepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion()) {
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                //return new InvoicePackRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            responseValidate.setStatusCode(response.getCodigoEstado());
            responseValidate.setReceptionCode(response.getCodigoRecepcion());
            for (bo.gob.impuestos.sfe.financialinvoice.MensajeRecepcion validateMessage : response.getMensajesList()) {
                responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
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
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoRecepcion(receptionCode);

            bo.gob.impuestos.sfe.telecommunicationinvoice.ServicioFacturacion service = soapUtil.getTelecommunicationsService(company.getToken());
            bo.gob.impuestos.sfe.telecommunicationinvoice.RespuestaRecepcion response = service.validacionRecepcionPaqueteFactura(invoiceRequest);

            if (!response.isTransaccion()) {
                throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                //return new InvoicePackRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            responseValidate.setStatusCode(response.getCodigoEstado());
            responseValidate.setReceptionCode(response.getCodigoRecepcion());
            for (bo.gob.impuestos.sfe.telecommunicationinvoice.MensajeRecepcion validateMessage : response.getMensajesList()) {
                responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
            }
        }

        result.setCode(SiatResponseCodes.SUCCESS);
        result.setBody(responseValidate);
        return result;
    }

    /**
     * Método que obtiene la hora actual del SIAT.
     *
     * @param company          Empresa.
     * @param cuis             Código único del sistema.
     * @param branchOfficeSiat Id de la sucursal Siat.
     * @param pointSaleSiat    Id punto de venta Siat.
     * @return
     * @throws DatetimeNotSync
     */
    protected ZonedDateTime obtainDateTimeFromSiat(Company company, Cuis cuis, int branchOfficeSiat, int pointSaleSiat) throws DatetimeNotSync {
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

    /**
     * Método que obtiene el código CUIS vigente.
     *
     * @param companyId        Id de la empresa.
     * @param branchOfficeSiat Id de la sucursal Siat.
     * @param pointSaleSiat    Id punto de venta Siat.
     * @return
     * @throws CuisNotFoundException
     */
    protected Optional<Cuis> obtainCuisActive(Long companyId, int branchOfficeSiat, int pointSaleSiat) throws CuisNotFoundException {
        Optional<Cuis> cuisOptional = cuisRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(pointSaleSiat, branchOfficeSiat, companyId);
        if (!cuisOptional.isPresent()) {
            throw new CuisNotFoundException();
        }
        return cuisOptional;
    }

    /**
     * Método que obtiene el código CUFD vigente.
     *
     * @param companyId        Id de la empresa.
     * @param branchOfficeSiat Id de la sucursal Siat.
     * @param pointSaleSiat    Id punto de venta Siat.
     * @return
     * @throws CufdNotFoundException
     */
    protected Optional<Cufd> obtainCufdActive(Long companyId, int branchOfficeSiat, int pointSaleSiat) throws CufdNotFoundException {
        Optional<Cufd> cufdOptional = cufdRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(pointSaleSiat, branchOfficeSiat, companyId);
        if (!cufdOptional.isPresent()) {
            throw new CufdNotFoundException();
        }
        return cufdOptional;
    }

    /**
     * Método que realiza la compresión TAR GZIP de la lista de facturas.
     *
     * @param listInvoices Lista de facturas.
     * @return
     * @throws Exception
     */
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

    /**
     * Método que realiza la conversión de formato para la fecha.
     *
     * @param time Fecha.
     * @return
     */
    private String convertZoneTimedateToString(ZonedDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedString = time.format(formatter);
        return formattedString;
    }
}
