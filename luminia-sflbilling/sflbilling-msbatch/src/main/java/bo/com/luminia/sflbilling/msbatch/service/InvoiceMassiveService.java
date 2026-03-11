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
import bo.com.luminia.sflbilling.domain.enumeration.InvoiceWrapperStatusEnum;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import bo.com.luminia.sflbilling.domain.enumeration.WrapperStatusEnum;
import bo.com.luminia.sflbilling.msbatch.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msbatch.repository.*;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.*;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.InvoiceMassiveReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.InvoiceMassiveRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.ResponseMessages;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.SoapUtil;
import bo.gob.impuestos.sfe.electronicinvoice.*;
import bo.gob.impuestos.sfe.invoice.xml.XmlSectorType;
import bo.gob.impuestos.sfe.synchronization.ObjectFactory;
import bo.gob.impuestos.sfe.synchronization.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class InvoiceMassiveService  extends ConnectivityBase{

    private final Environment environment;
    private final SoapUtil soapUtil;

    private final CompanyRepository companyRepository;
    private final PointSaleRepository pointSaleRepository;
    private final CuisRepository cuisRepository;
    private final CufdRepository cufdRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceBatchRepository invoiceBatchRepository;
    private final WrapperRepository wrapperRepository;
    private final InvoiceWrapperRepository invoiceWrapperRepository;
    private final BranchOfficeRepository branchOfficeRepository;
    private final ScheduleSettingRepository scheduleSettingRepository;
    private final SectorDocumentTypeRepository sectorDocumentTypeRepository;

    private final NotificationService notificationService;

    /**
     * Método que realiza el envío masivo de facturas.
     *
     * @param invoiceMassiveReq Solicitud.
     * @return
     */
    public InvoiceMassiveRes emitMassive(InvoiceMassiveReq invoiceMassiveReq) {

        List<Integer> sectorDocumentTypeList = new ArrayList<>();
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_COMPRA_VENTA);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_ZONA_FRANCA);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_SECTOR_EDUCATIVO);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_FACTURAS_HOTELES);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_PREVALORADA);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_HOSPITAL_CLINICAS);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_SERVICIOS_BASICOS);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_TELECOMUNICACIONES);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_SEGUROS);
        sectorDocumentTypeList.add(XmlSectorType.INVOICE_BOLETO_AEREO);

        // 1. Obtiene datos de la empresa.
        Optional<Company> companyFromDb = companyRepository.findByIdAndActiveTrue(invoiceMassiveReq.getCompanyId());
        Company company = null;
        if (companyFromDb.isPresent()) {
            company = companyFromDb.get();
            log.debug("1. Recupera datos de la empresa : {}", company);
        }

        // 2. Obtiene la lista de puntos de venta activos.
        List listPointSale = pointSaleRepository.findAllByCompanyIdAndActive(company.getId());
        if (listPointSale.size() <= 0) {
            throw new RecordNotFoundException("No existen puntos de venta para la empresa.");
            //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_POINT_SALES_NOT_FOUND, "No existen puntos de venta para la empresa.");
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
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_CUIS_NOT_FOUND, "Codigo Cuis no encontrado o no ha sido generado");
            }
            Cuis cuisFromDb = cuisOptional.get();
            log.debug("3. Datos del código CUIS : {}", cuisFromDb);

            // 4. Obtiene el CUFD activo.
            Optional<Cufd> cufdOptional = null;
            try {
                cufdOptional = this.obtainCufdActive(company.getId(), pointSaleCurrent.getBranchOfficeSiatId(), pointSaleCurrent.getPointSaleSiatId());
            } catch (CufdNotFoundException e) {
                throw new CufdNotFoundException();
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_CUFD_NOT_FOUND, "Codigo Cufd no encontrado o no ha sido generado");
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
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "No hay conectividad con el Siat");
            }

            // 6. Obtiene fecha y hora actual del SIAT.
            ZonedDateTime dateTimeFromSiat = ZonedDateTime.now();
            /*try {
            ZonedDateTime dateTimeFromSiat = null;
                dateTimeFromSiat = this.obtainDateTimeFromSiat(company, cuisFromDb, pointSaleCurrent.getBranchOfficeSiatId(), pointSaleCurrent.getPointSaleSiatId());
            } catch (DatetimeNotSync datetimeNotSync) {
                throw new DatetimeNotSync();
            }
            log.debug("6. Fecha y hora actual del SIAT: {}", dateTimeFromSiat);
             */
            log.debug("6. Fecha y hora actual del servidor: {}", dateTimeFromSiat);

            try {
                for (Integer sectorDocumentType : sectorDocumentTypeList) {
                    // Envío masivo de facturas por tipo documento sector.
                    InvoiceMassiveRes response = this.sendMassiveInvoice(company, pointSaleCurrent, cuisFromDb, cufdFromDb, dateTimeFromSiat, invoiceMassiveReq, sectorDocumentType);
                    if (!response.getCode().equals(SiatResponseCodes.SUCCESS)) {
                        return response;
                    }
                }
            } catch (Exception e) {
                log.error("Ocurrio un error en la recepción masiva de las facturas", e);
                throw new SiatException("Ocurrio un error en la recepción masiva de las facturas.",
                    ErrorEntities.INVOICE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Ocurrio un error en la recepción masiva de las facturas.");
            }
        }
        return new InvoiceMassiveRes(SiatResponseCodes.SUCCESS, "En envío masivo de facturas ha terminado correctamente");
    }

    /**
     * Método que realiza la validación del envio masivo.
     *
     * @param companyId Identificador de la empresa.
     * @return
     */
    @Transactional
    public InvoiceMassiveRes validateMassive(long companyId) {
        // 1. Obtiene datos de la empresa.
        Optional<Company> companyFromDb = companyRepository.findByIdAndActiveTrue(companyId);
        Company company = null;
        if (companyFromDb.isPresent()) {
            company = companyFromDb.get();
            log.debug("1. Recupera datos de la empresa : {}", company);
        }

        // 2. Obtiene la lista de paquetes para validación.
        List<Wrapper> wrapperList = wrapperRepository.findPendingByCompanyId(company.getId());
        if (wrapperList.size() <= 0) {
            throw new SiatException("No existen paquetes para validación.");
        }
        log.debug("2. Cantidad de paquetes para validación : {}", wrapperList.size());

        // Itera la lista de paquetes.
        for (Wrapper wrapper : wrapperList) {
            // 3. Obtiene el CUIS activo.
            Optional<Cuis> cuisOptional = null;
            try {
                cuisOptional = this.obtainCuisActive(company.getId(), wrapper.getBranchOffice().getBranchOfficeSiatId(), wrapper.getPointSale().getPointSaleSiatId());
            } catch (CuisNotFoundException e) {
                throw new CuisNotFoundException();
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_CUIS_NOT_FOUND, "Codigo Cuis no encontrado o no ha sido generado");
            }
            Cuis cuisFromDb = cuisOptional.get();
            log.debug("3. Datos del código CUIS : {}", cuisFromDb);

            // 4. Obtiene el CUFD activo.
            Optional<Cufd> cufdOptional = null;
            try {
                cufdOptional = this.obtainCufdActive(company.getId(), wrapper.getBranchOffice().getBranchOfficeSiatId(), wrapper.getPointSale().getPointSaleSiatId());
            } catch (CufdNotFoundException e) {
                throw new CufdNotFoundException();
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_CUFD_NOT_FOUND, "Codigo Cufd no encontrado o no ha sido generado");
            }
            Cufd cufdFromDb = cufdOptional.get();
            log.debug("4. Datos del código CUFD : {}", cufdFromDb);

            // 5. Verifica la comunicacion con los servicios del SIAT.
            int check = checkConnectivity(company.getToken(), environment, soapUtil);
            log.debug("5. Conexión verificada : {}", check);
            if (check != SiatResponseCodes.SIAT_HAS_CONNECTIVITY) {
                throw new SiatException(ResponseMessages.ERROR_NO_SIAT_CONNECTION,
                    ErrorEntities.INVOICE_MASSIVE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "No hay conectividad con el Siat");
            }

            // 6. Creación de la solicitud de validación.
            InvoiceMassiveRes invoiceMassiveRes = this.sendValidateMassiveSiat(company, wrapper, cuisFromDb, cufdFromDb);
            if (invoiceMassiveRes.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT)) {
                throw new SiatException(invoiceMassiveRes.getMessage(),
                    ErrorEntities.INVOICE_MASSIVE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return invoiceMassiveRes;
            }

            InvoiceValidateDto responseValidate = (InvoiceValidateDto) invoiceMassiveRes.getBody();

            // Verifica el estado de la validación.
            if (responseValidate.getStatusCode().equals(SiatResponseCodes.SIAT_WRAPPER_VALIDATED) ||
                responseValidate.getStatusCode().equals(SiatResponseCodes.SIAT_WRAPPER_OBSERVED)) {

                wrapper.setStatus(responseValidate.getStatusCode().equals(SiatResponseCodes.SIAT_WRAPPER_VALIDATED) ? WrapperStatusEnum.VALIDATED : WrapperStatusEnum.OBSERVED);
                wrapperRepository.save(wrapper);

                List<InvoiceWrapper> invoiceWrapperList = invoiceWrapperRepository.findAllByWrapperId(wrapper.getId());
                for (InvoiceWrapper invoiceWrapper : invoiceWrapperList) {
                    invoiceWrapper.setStatus(InvoiceWrapperStatusEnum.VALIDATED);
                    invoiceWrapperRepository.save(invoiceWrapper);

                    Invoice invoiceUpdate = invoiceWrapper.getInvoiceBatch().getInvoice();
                    invoiceUpdate.setReceptionCode(responseValidate.getReceptionCode());
                    invoiceUpdate.setStatus(InvoiceStatusEnum.EMITTED);
                    invoiceRepository.save(invoiceUpdate);
                }

                // Marca las facturas en caso de observados.
                if (responseValidate.getStatusCode().equals(SiatResponseCodes.SIAT_WRAPPER_OBSERVED)) {
                    // Itera la lista de validaciones.
                    for (InvoiceValidateMessageDto validateMessage : responseValidate.getValidationMessages()) {
                        InvoiceWrapper invoiceWrapper = invoiceWrapperList.stream().filter(x -> x.getFileNumber().equals(validateMessage.getFileNumber())).findFirst().get();
                        invoiceWrapper.setStatus(InvoiceWrapperStatusEnum.OBSERVED);
                        invoiceWrapper.setResponseMessage(validateMessage.getDescription());
                        invoiceWrapperRepository.save(invoiceWrapper);

                        Invoice invoiceUpdate = invoiceWrapper.getInvoiceBatch().getInvoice();
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
        return new InvoiceMassiveRes(SiatResponseCodes.SUCCESS, "La validación de la recepción masiva termino correctamente.");
    }

    /**
     * Método para la envío masivo de facturas.
     *
     * @param company            Empresa.
     * @param pointSaleCurrent   Punto de venta.
     * @param cuisFromDb         Código único del sistema.
     * @param cufdFromDb         Código único diario.
     * @param dateTimeFromSiat   Fecha actual SIAT.
     * @param sectorDocumentType Tipo de documento sector.
     * @return
     * @throws Exception
     */
    private InvoiceMassiveRes sendMassiveInvoice(Company company, PointSaleFullDto pointSaleCurrent, Cuis cuisFromDb, Cufd cufdFromDb,
                                                 ZonedDateTime dateTimeFromSiat, InvoiceMassiveReq invoiceMassiveReq, Integer sectorDocumentType) throws Exception {

        Integer numberInvoice = Integer.parseInt(environment.getProperty(ApplicationProperties.INVOICE_NUMBER_MASSIVE));
        List listInvoice = null;
        do {
            // 7. Obtiene la lista de facturas.
            listInvoice = invoiceRepository.findAllMassiveInvoice(company.getId(),
                pointSaleCurrent.getBranchOfficeSiatId(), pointSaleCurrent.getPointSaleSiatId(), sectorDocumentType,
                SiatBroadcastType.BROADCAST_TYPE_MASSIVE, numberInvoice);
            if (listInvoice.size() <= 0) {
                return new InvoiceMassiveRes(SiatResponseCodes.SUCCESS, "");
            }
            // Mapea la lista de facturas.
            List<InvoiceMassiveDto> listInvoices = ((ArrayList<Object[]>) listInvoice).stream().map(x -> {
                InvoiceMassiveDto invoice = new InvoiceMassiveDto();
                invoice.setId(((BigInteger) x[0]).longValue());
                invoice.setInvoiceXml(x[1].toString());
                return invoice;
            }).collect(Collectors.toList());
            log.debug("7. Cantidad de facturas masivas : {}", listInvoices.size());

            byte[] tarXml = this.compressionTar(listInvoices);
            log.debug("8. Compresión de las facturas encontradas.");

            // 9. Obtiene el hash del documento compreso.
            String hashXml = FileHash.sha256(tarXml);
            log.debug("9. Hash del documento compreso: {}", hashXml);

            // 10. Realiza la recepción masiva de las facturas en el SIAT.
            InvoiceMassiveRes invoiceMassiveRes = this.sendMassiveInvoiceSiat(company, pointSaleCurrent, cuisFromDb, cufdFromDb,
                dateTimeFromSiat, tarXml, hashXml, listInvoices.size(), sectorDocumentType);
            if (invoiceMassiveRes.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT)) {
                throw new SiatException(invoiceMassiveRes.getMessage(),
                    ErrorEntities.INVOICE_MASSIVE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return invoiceMassiveRes;
            }
            String receptionCode = invoiceMassiveRes.getBody().toString();

            // 11. Registra el paquete de facturas.
            Wrapper wrapperNew = new Wrapper();
            wrapperNew.setCudfEvent(cufdFromDb.getCufd());
            wrapperNew.setStartDate(dateTimeFromSiat);
            wrapperNew.setEndDate(ZonedDateTime.now());
            wrapperNew.setReceptionCode(receptionCode);
            wrapperNew.setStatus(WrapperStatusEnum.PENDING);
            if (invoiceMassiveReq.getScheduleSettingId() != null)
                wrapperNew.setScheduleSetting(scheduleSettingRepository.getById(invoiceMassiveReq.getScheduleSettingId()));
            wrapperNew.setBranchOffice(branchOfficeRepository.findByBranchOfficeSiatIdActive(pointSaleCurrent.getBranchOfficeSiatId(), company.getId()));
            wrapperNew.setPointSale(pointSaleRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(pointSaleCurrent.getPointSaleSiatId(), pointSaleCurrent.getBranchOfficeSiatId(), company.getId()));
            wrapperNew.setSectorDocumentType(sectorDocumentTypeRepository.getBySiatIdAndCompanyIdAndActiveIsTrue(sectorDocumentType, company.getId().intValue()));
            Wrapper wrapper = wrapperRepository.save(wrapperNew);

            // 12. Itera la lista de facturas para registro.
            for (int fileNumber = 0; fileNumber < listInvoices.size(); fileNumber++) {
                InvoiceWrapper invoiceWrapperNew = new InvoiceWrapper();
                invoiceWrapperNew.setFileNumber(fileNumber);
                invoiceWrapperNew.setResponseMessage(null);
                invoiceWrapperNew.setStatus(InvoiceWrapperStatusEnum.PENDING);
                invoiceWrapperNew.setInvoiceBatch(invoiceBatchRepository.findByInvoiceId(listInvoices.get(fileNumber).getId()));
                invoiceWrapperNew.setWrapper(wrapper);
                invoiceWrapperRepository.save(invoiceWrapperNew);
            }
            log.debug("11. Registro del paquete de facturas");

        } while (listInvoice.size() == numberInvoice);

        return new InvoiceMassiveRes(SiatResponseCodes.SUCCESS, "");
    }

    /**
     * Método para el envío masivo al servicio del SIAT.
     *
     * @param company            Empresa.
     * @param pointSaleCurrent   Punto de venta actual.
     * @param cuisFromDb         Código único del sistema.
     * @param cufdFromDb         Código único diario.
     * @param dateTimeFromSiat   Fecha y hora actual.
     * @param tarXml             Paquete de facturas.
     * @param hashXml            Hash del paquete de facturas.
     * @param numberInvoice      Número de facturas del paquete.
     * @param sectorDocumentType Tipo de documento sector.
     * @return
     * @throws Exception
     */
    private InvoiceMassiveRes sendMassiveInvoiceSiat(Company company, PointSaleFullDto pointSaleCurrent, Cuis cuisFromDb, Cufd cufdFromDb,
                                                     ZonedDateTime dateTimeFromSiat, byte[] tarXml, String hashXml, Integer numberInvoice, Integer sectorDocumentType) throws Exception {
        InvoiceMassiveRes result = new InvoiceMassiveRes();
        String receptionCode = "";
        String strDateTimeFromSiat = convertZoneTimedateToString(dateTimeFromSiat);

        // Identifica el tipo de factura para la envío masivo.
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_COMPRA_VENTA)) {
            bo.gob.impuestos.sfe.buysellinvoice.SolicitudRecepcionMasiva invoiceRequest = new bo.gob.impuestos.sfe.buysellinvoice.SolicitudRecepcionMasiva();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleCurrent.getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(pointSaleCurrent.getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setArchivo(tarXml);
            invoiceRequest.setFechaEnvio(strDateTimeFromSiat);
            invoiceRequest.setHashArchivo(hashXml);
            invoiceRequest.setCantidadFacturas(numberInvoice);

            bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion service = soapUtil.getBuySellService(company.getToken());
            bo.gob.impuestos.sfe.buysellinvoice.RespuestaRecepcion response = service.recepcionMasivaFactura(invoiceRequest);
            log.debug("9.1. Resultado SIAT recepcion: {} : {}", response.isTransaccion(),
                null != response.getMensajesList() && response.getMensajesList().size() > 0 ? response.getMensajesList().get(0).getDescripcion() : "");

            if (!response.isTransaccion()) {
                log.error("Error en envío masivo para company_id:{}, company_name:{}, ", company.getId(), company.getName());
                notificationService.notifyMassiveInvoiceError(
                    company,
                    response.getMensajesList().get(0).getDescripcion()
                );
                throw new SiatException(response.getMensajesList().get(0).getDescripcion(),
                    ErrorEntities.INVOICE_MASSIVE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            receptionCode = response.getCodigoRecepcion();
            log.debug("10. Resultado recepción de la factura masiva: {}", response);
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_ZONA_FRANCA) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_SEGUROS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_SECTOR_EDUCATIVO) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_FACTURAS_HOTELES) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_PREVALORADA) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_HOSPITAL_CLINICAS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS)) {

            if (company.getModalitySiat().equals(ModalitySiatEnum.ELECTRONIC_BILLING)) {
                SolicitudRecepcionMasiva invoiceRequest = new SolicitudRecepcionMasiva();
                invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
                invoiceRequest.setCodigoPuntoVenta(pointSaleCurrent.getPointSaleSiatId());
                invoiceRequest.setCodigoSistema(company.getSystemCode());
                invoiceRequest.setCodigoSucursal(pointSaleCurrent.getBranchOfficeSiatId());
                invoiceRequest.setNit(company.getNit());
                invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
                invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
                invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
                invoiceRequest.setCufd(cufdFromDb.getCufd());
                invoiceRequest.setCuis(cuisFromDb.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
                invoiceRequest.setArchivo(tarXml);
                invoiceRequest.setFechaEnvio(strDateTimeFromSiat);
                invoiceRequest.setHashArchivo(hashXml);
                invoiceRequest.setCantidadFacturas(numberInvoice);

                ServicioFacturacion service = soapUtil.getElectronicService(company.getToken());
                RespuestaRecepcion response = service.recepcionMasivaFactura(invoiceRequest);
                log.debug("9.1. Resultado SIAT recepcion: {} : {}", response.isTransaccion(),
                    null != response.getMensajesList() && response.getMensajesList().size() > 0 ? response.getMensajesList().get(0).getDescripcion() : "");

                if (!response.isTransaccion()) {
                    log.error("Error en envío masivo para company_id:{}, company_name:{}, ", company.getId(), company.getName());
                    notificationService.notifyMassiveInvoiceError(
                        company,
                        response.getMensajesList().get(0).getDescripcion()
                    );
                    throw new SiatException(response.getMensajesList().get(0).getDescripcion(),
                        ErrorEntities.INVOICE_MASSIVE,
                        ErrorKeys.ERR_SIAT_EXCEPTION);
                    //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
                }
                receptionCode = response.getCodigoRecepcion();
                log.debug("10. Resultado recepción de la factura masiva: {}", response);
            } else {
                bo.gob.impuestos.sfe.computerizedinvoice.SolicitudRecepcionMasiva invoiceRequest = new bo.gob.impuestos.sfe.computerizedinvoice.SolicitudRecepcionMasiva();
                invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
                invoiceRequest.setCodigoPuntoVenta(pointSaleCurrent.getPointSaleSiatId());
                invoiceRequest.setCodigoSistema(company.getSystemCode());
                invoiceRequest.setCodigoSucursal(pointSaleCurrent.getBranchOfficeSiatId());
                invoiceRequest.setNit(company.getNit());
                invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
                invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
                invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
                invoiceRequest.setCufd(cufdFromDb.getCufd());
                invoiceRequest.setCuis(cuisFromDb.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
                invoiceRequest.setArchivo(tarXml);
                invoiceRequest.setFechaEnvio(strDateTimeFromSiat);
                invoiceRequest.setHashArchivo(hashXml);
                invoiceRequest.setCantidadFacturas(numberInvoice);

                bo.gob.impuestos.sfe.computerizedinvoice.ServicioFacturacion service = soapUtil.getComputerizedService(company.getToken());
                bo.gob.impuestos.sfe.computerizedinvoice.RespuestaRecepcion response = service.recepcionMasivaFactura(invoiceRequest);
                log.debug("9.1. Resultado SIAT recepcion: {} : {}", response.isTransaccion(),
                    null != response.getMensajesList() && response.getMensajesList().size() > 0 ? response.getMensajesList().get(0).getDescripcion() : "");

                if (!response.isTransaccion()) {
                    log.error("Error en envío masivo para company_id:{}, company_name:{}, ", company.getId(), company.getName());
                    notificationService.notifyMassiveInvoiceError(
                        company,
                        response.getMensajesList().get(0).getDescripcion()
                    );
                    throw new SiatException(response.getMensajesList().get(0).getDescripcion(),
                        ErrorEntities.INVOICE_MASSIVE,
                        ErrorKeys.ERR_SIAT_EXCEPTION);
                    //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
                }
                receptionCode = response.getCodigoRecepcion();
                log.debug("10. Resultado recepción de la factura masiva: {}", response);
            }
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_SERVICIOS_BASICOS)) {
            bo.gob.impuestos.sfe.basicserviceinvoice.SolicitudRecepcionMasiva invoiceRequest = new bo.gob.impuestos.sfe.basicserviceinvoice.SolicitudRecepcionMasiva();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleCurrent.getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(pointSaleCurrent.getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setArchivo(tarXml);
            invoiceRequest.setFechaEnvio(strDateTimeFromSiat);
            invoiceRequest.setHashArchivo(hashXml);
            invoiceRequest.setCantidadFacturas(numberInvoice);

            bo.gob.impuestos.sfe.basicserviceinvoice.ServicioFacturacion service = soapUtil.getBasicService(company.getToken());
            bo.gob.impuestos.sfe.basicserviceinvoice.RespuestaRecepcion response = service.recepcionMasivaFactura(invoiceRequest);
            log.debug("9.1. Resultado SIAT recepcion: {} : {}", response.isTransaccion(),
                null != response.getMensajesList() && response.getMensajesList().size() > 0 ? response.getMensajesList().get(0).getDescripcion() : "");

            if (!response.isTransaccion()) {
                log.error("Error en envío masivo para company_id:{}, company_name:{}, ", company.getId(), company.getName());
                notificationService.notifyMassiveInvoiceError(
                    company,
                    response.getMensajesList().get(0).getDescripcion()
                );
                throw new SiatException(response.getMensajesList().get(0).getDescripcion(),
                    ErrorEntities.INVOICE_MASSIVE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            receptionCode = response.getCodigoRecepcion();
            log.debug("10. Resultado recepción de la factura masiva: {}", response);
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS)) {
            bo.gob.impuestos.sfe.financialinvoice.SolicitudRecepcionMasiva invoiceRequest = new bo.gob.impuestos.sfe.financialinvoice.SolicitudRecepcionMasiva();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleCurrent.getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(pointSaleCurrent.getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setArchivo(tarXml);
            invoiceRequest.setFechaEnvio(strDateTimeFromSiat);
            invoiceRequest.setHashArchivo(hashXml);
            invoiceRequest.setCantidadFacturas(numberInvoice);

            bo.gob.impuestos.sfe.financialinvoice.ServicioFacturacion service = soapUtil.getFinancialService(company.getToken());
            bo.gob.impuestos.sfe.financialinvoice.RespuestaRecepcion response = service.recepcionMasivaFactura(invoiceRequest);
            log.debug("9.1. Resultado SIAT recepcion: {} : {}", response.isTransaccion(),
                null != response.getMensajesList() && response.getMensajesList().size() > 0 ? response.getMensajesList().get(0).getDescripcion() : "");

            if (!response.isTransaccion()) {
                log.error("Error en envío masivo para company_id:{}, company_name:{}, ", company.getId(), company.getName());
                notificationService.notifyMassiveInvoiceError(
                    company,
                    response.getMensajesList().get(0).getDescripcion()
                );
                throw new SiatException(response.getMensajesList().get(0).getDescripcion(),
                    ErrorEntities.INVOICE_MASSIVE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            receptionCode = response.getCodigoRecepcion();
            log.debug("10. Resultado recepción de la factura masiva: {}", response);
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_TELECOMUNICACIONES)) {
            bo.gob.impuestos.sfe.telecommunicationinvoice.SolicitudRecepcionMasiva invoiceRequest = new bo.gob.impuestos.sfe.telecommunicationinvoice.SolicitudRecepcionMasiva();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleCurrent.getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(pointSaleCurrent.getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setArchivo(tarXml);
            invoiceRequest.setFechaEnvio(strDateTimeFromSiat);
            invoiceRequest.setHashArchivo(hashXml);
            invoiceRequest.setCantidadFacturas(numberInvoice);

            bo.gob.impuestos.sfe.telecommunicationinvoice.ServicioFacturacion service = soapUtil.getTelecommunicationsService(company.getToken());
            bo.gob.impuestos.sfe.telecommunicationinvoice.RespuestaRecepcion response = service.recepcionMasivaFactura(invoiceRequest);
            log.debug("9.1. Resultado SIAT recepcion: {} : {}", response.isTransaccion(),
                null != response.getMensajesList() && response.getMensajesList().size() > 0 ? response.getMensajesList().get(0).getDescripcion() : "");

            if (!response.isTransaccion()) {
                log.error("Error en envío masivo para company_id:{}, company_name:{}, ", company.getId(), company.getName());
                notificationService.notifyMassiveInvoiceError(
                    company,
                    response.getMensajesList().get(0).getDescripcion()
                );
                throw new SiatException(response.getMensajesList().get(0).getDescripcion(),
                    ErrorEntities.INVOICE_MASSIVE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            receptionCode = response.getCodigoRecepcion();
            log.debug("10. Resultado recepción de la factura masiva: {}", response);
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_BOLETO_AEREO)) {
            bo.gob.impuestos.sfe.airticketinvoice.SolicitudRecepcionMasiva invoiceRequest = new bo.gob.impuestos.sfe.airticketinvoice.SolicitudRecepcionMasiva();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleCurrent.getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(pointSaleCurrent.getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setArchivo(tarXml);
            invoiceRequest.setFechaEnvio(strDateTimeFromSiat);
            invoiceRequest.setHashArchivo(hashXml);
            invoiceRequest.setCantidadFacturas(numberInvoice);

            bo.gob.impuestos.sfe.airticketinvoice.ServicioFacturacion service = soapUtil.getAirTicketService(company.getToken());
            bo.gob.impuestos.sfe.airticketinvoice.RespuestaRecepcion response = service.recepcionMasivaFactura(invoiceRequest);
            log.debug("9.1. Resultado SIAT recepcion: {} : {}", response.isTransaccion(),
                null != response.getMensajesList() && response.getMensajesList().size() > 0 ? response.getMensajesList().get(0).getDescripcion() : "");

            if (!response.isTransaccion()) {
                log.error("Error en envío masivo para company_id:{}, company_name:{}, ", company.getId(), company.getName());
                notificationService.notifyMassiveInvoiceError(
                    company,
                    response.getMensajesList().get(0).getDescripcion()
                );
                throw new SiatException(response.getMensajesList().get(0).getDescripcion(),
                    ErrorEntities.INVOICE_MASSIVE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            receptionCode = response.getCodigoRecepcion();
            log.debug("10. Resultado recepción de la factura masiva: {}", response);
        }

        result.setCode(SiatResponseCodes.SUCCESS);
        result.setBody(receptionCode);
        return result;
    }

    /**
     * Método para envío a validación al servicio del SIAT.
     *
     * @param company    Empresa.
     * @param wrapper    Paquete.
     * @param cuisFromDb Código único del sistema.
     * @param cufdFromDb Código único diario.
     * @return
     */
    private InvoiceMassiveRes sendValidateMassiveSiat(Company company, Wrapper wrapper, Cuis cuisFromDb, Cufd cufdFromDb) {
        InvoiceMassiveRes result = new InvoiceMassiveRes();
        Integer sectorDocumentType = wrapper.getSectorDocumentType().getSiatId();
        String receptionCode = wrapper.getReceptionCode();
        InvoiceValidateDto responseValidate = new InvoiceValidateDto();

        // Identifica el tipo de factura para validación.
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_COMPRA_VENTA)) {
            bo.gob.impuestos.sfe.buysellinvoice.SolicitudValidacionRecepcion invoiceRequest = new bo.gob.impuestos.sfe.buysellinvoice.SolicitudValidacionRecepcion();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(wrapper.getPointSale().getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(wrapper.getBranchOffice().getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoRecepcion(receptionCode);

            bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion service = soapUtil.getBuySellService(company.getToken());
            bo.gob.impuestos.sfe.buysellinvoice.RespuestaRecepcion response = service.validacionRecepcionMasivaFactura(invoiceRequest);

            if (!response.isTransaccion()) {
                log.error("Error en envío masivo para company_id:{}, company_name:{}, ", company.getId(), company.getName());
                throw new SiatException(response.getMensajesList().get(0).getDescripcion(),
                    ErrorEntities.INVOICE_MASSIVE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            responseValidate.setStatusCode(response.getCodigoEstado());
            responseValidate.setReceptionCode(response.getCodigoRecepcion());
            for (bo.gob.impuestos.sfe.buysellinvoice.MensajeRecepcion validateMessage : response.getMensajesList()) {
                responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
            }
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_ZONA_FRANCA) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_SEGUROS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_SECTOR_EDUCATIVO) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_FACTURAS_HOTELES) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_PREVALORADA) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_HOSPITAL_CLINICAS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS)) {

            if (company.getModalitySiat().equals(ModalitySiatEnum.ELECTRONIC_BILLING)) {
                SolicitudValidacionRecepcion invoiceRequest = new SolicitudValidacionRecepcion();
                invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
                invoiceRequest.setCodigoPuntoVenta(wrapper.getPointSale().getPointSaleSiatId());
                invoiceRequest.setCodigoSistema(company.getSystemCode());
                invoiceRequest.setCodigoSucursal(wrapper.getBranchOffice().getBranchOfficeSiatId());
                invoiceRequest.setNit(company.getNit());
                invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
                invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
                invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
                invoiceRequest.setCufd(cufdFromDb.getCufd());
                invoiceRequest.setCuis(cuisFromDb.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
                invoiceRequest.setCodigoRecepcion(receptionCode);

                ServicioFacturacion service = soapUtil.getElectronicService(company.getToken());
                RespuestaRecepcion response = service.validacionRecepcionMasivaFactura(invoiceRequest);

                if (!response.isTransaccion()) {
                    log.error("Error en envío masivo para company_id:{}, company_name:{}, ", company.getId(), company.getName());
                    throw new SiatException(response.getMensajesList().get(0).getDescripcion(),
                        ErrorEntities.INVOICE_MASSIVE,
                        ErrorKeys.ERR_SIAT_EXCEPTION);
                    //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
                }
                responseValidate.setStatusCode(response.getCodigoEstado());
                responseValidate.setReceptionCode(response.getCodigoRecepcion());
                for (MensajeRecepcion validateMessage : response.getMensajesList()) {
                    responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
                }
            } else {
                bo.gob.impuestos.sfe.computerizedinvoice.SolicitudValidacionRecepcion invoiceRequest = new bo.gob.impuestos.sfe.computerizedinvoice.SolicitudValidacionRecepcion();
                invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
                invoiceRequest.setCodigoPuntoVenta(wrapper.getPointSale().getPointSaleSiatId());
                invoiceRequest.setCodigoSistema(company.getSystemCode());
                invoiceRequest.setCodigoSucursal(wrapper.getBranchOffice().getBranchOfficeSiatId());
                invoiceRequest.setNit(company.getNit());
                invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
                invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
                invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
                invoiceRequest.setCufd(cufdFromDb.getCufd());
                invoiceRequest.setCuis(cuisFromDb.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
                invoiceRequest.setCodigoRecepcion(receptionCode);

                bo.gob.impuestos.sfe.computerizedinvoice.ServicioFacturacion service = soapUtil.getComputerizedService(company.getToken());
                bo.gob.impuestos.sfe.computerizedinvoice.RespuestaRecepcion response = service.validacionRecepcionMasivaFactura(invoiceRequest);

                if (!response.isTransaccion()) {
                    log.error("Error en envío masivo para company_id:{}, company_name:{}, ", company.getId(), company.getName());
                    throw new SiatException(response.getMensajesList().get(0).getDescripcion(),
                        ErrorEntities.INVOICE_MASSIVE,
                        ErrorKeys.ERR_SIAT_EXCEPTION);
                    //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
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
            invoiceRequest.setCodigoPuntoVenta(wrapper.getPointSale().getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(wrapper.getBranchOffice().getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoRecepcion(receptionCode);

            bo.gob.impuestos.sfe.basicserviceinvoice.ServicioFacturacion service = soapUtil.getBasicService(company.getToken());
            bo.gob.impuestos.sfe.basicserviceinvoice.RespuestaRecepcion response = service.validacionRecepcionMasivaFactura(invoiceRequest);

            if (!response.isTransaccion()) {
                log.error("Error en envío masivo para company_id:{}, company_name:{}, ", company.getId(), company.getName());
                throw new SiatException(response.getMensajesList().get(0).getDescripcion(),
                    ErrorEntities.INVOICE_MASSIVE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
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
            invoiceRequest.setCodigoPuntoVenta(wrapper.getPointSale().getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(wrapper.getBranchOffice().getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoRecepcion(receptionCode);

            bo.gob.impuestos.sfe.financialinvoice.ServicioFacturacion service = soapUtil.getFinancialService(company.getToken());
            bo.gob.impuestos.sfe.financialinvoice.RespuestaRecepcion response = service.validacionRecepcionMasivaFactura(invoiceRequest);

            if (!response.isTransaccion()) {
                log.error("Error en envío masivo para company_id:{}, company_name:{}, ", company.getId(), company.getName());
                throw new SiatException(response.getMensajesList().get(0).getDescripcion(),
                    ErrorEntities.INVOICE_MASSIVE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
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
            invoiceRequest.setCodigoPuntoVenta(wrapper.getPointSale().getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(wrapper.getBranchOffice().getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoRecepcion(receptionCode);

            bo.gob.impuestos.sfe.telecommunicationinvoice.ServicioFacturacion service = soapUtil.getTelecommunicationsService(company.getToken());
            bo.gob.impuestos.sfe.telecommunicationinvoice.RespuestaRecepcion response = service.validacionRecepcionMasivaFactura(invoiceRequest);

            if (!response.isTransaccion()) {
                log.error("Error en envío masivo para company_id:{}, company_name:{}, ", company.getId(), company.getName());
                throw new SiatException(response.getMensajesList().get(0).getDescripcion(),
                    ErrorEntities.INVOICE_MASSIVE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            responseValidate.setStatusCode(response.getCodigoEstado());
            responseValidate.setReceptionCode(response.getCodigoRecepcion());
            for (bo.gob.impuestos.sfe.telecommunicationinvoice.MensajeRecepcion validateMessage : response.getMensajesList()) {
                responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
            }
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_BOLETO_AEREO)) {

            bo.gob.impuestos.sfe.airticketinvoice.SolicitudValidacionRecepcion invoiceRequest = new bo.gob.impuestos.sfe.airticketinvoice.SolicitudValidacionRecepcion();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(wrapper.getPointSale().getPointSaleSiatId());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(wrapper.getBranchOffice().getBranchOfficeSiatId());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoRecepcion(receptionCode);

            bo.gob.impuestos.sfe.airticketinvoice.ServicioFacturacion service = soapUtil.getAirTicketService(company.getToken());
            bo.gob.impuestos.sfe.airticketinvoice.RespuestaRecepcion response = service.validacionRecepcionMasivaFactura(invoiceRequest);

            if (!response.isTransaccion()) {
                log.error("Error en envío masivo para company_id:{}, company_name:{}, ", company.getId(), company.getName());
                throw new SiatException(response.getMensajesList().get(0).getDescripcion(),
                    ErrorEntities.INVOICE_MASSIVE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
                //return new InvoiceMassiveRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            responseValidate.setStatusCode(response.getCodigoEstado());
            responseValidate.setReceptionCode(response.getCodigoRecepcion());
            for (bo.gob.impuestos.sfe.airticketinvoice.MensajeRecepcion validateMessage : response.getMensajesList()) {
                responseValidate.getValidationMessages().add(new InvoiceValidateMessageDto(validateMessage.getNumeroArchivo(), validateMessage.getDescripcion()));
            }
        }

        result.setCode(SiatResponseCodes.SUCCESS);
        result.setBody(responseValidate);
        return result;
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

    private String convertZoneTimedateToString(ZonedDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedString = time.format(formatter);
        return formattedString;
    }
}
