package bo.com.luminia.sflbilling.msbatch.service;

import bo.com.luminia.sflbilling.msbatch.repository.*;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatBroadcastType;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatInvoiceType;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msbatch.service.dto.ApprovedProductFullDto;
import bo.com.luminia.sflbilling.msbatch.service.signature.SignatureService;
import bo.com.luminia.sflbilling.msbatch.service.utils.FileCompress;
import bo.com.luminia.sflbilling.msbatch.service.utils.FileHash;
import bo.com.luminia.sflbilling.domain.*;
import bo.com.luminia.sflbilling.domain.enumeration.BatchStatusEnum;
import bo.com.luminia.sflbilling.domain.enumeration.InvoiceStatusEnum;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import bo.com.luminia.sflbilling.msbatch.repository.*;
import bo.com.luminia.sflbilling.msbatch.siat.invoice.xml.XmlInvoiceAdapter;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.CufdNotFoundException;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.CuisNotFoundException;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.DefaultTransactionException;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.InvoiceCannotBeProccesed;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.InvoiceIssueReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.InvoiceUniqueCodeGeneratorCufReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.InvoiceUniqueCodeGeneratorCufRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.InvoiceMassiveRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.ResponseMessages;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.SoapUtil;
import bo.gob.impuestos.sfe.code.RespuestaVerificarNit;
import bo.gob.impuestos.sfe.code.ServicioFacturacionCodigos;
import bo.gob.impuestos.sfe.code.SolicitudVerificarNit;
import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlSectorType;
import bo.gob.impuestos.sfe.invoice.xml.cmp.*;
import bo.gob.impuestos.sfe.invoice.xml.detail.*;
import bo.gob.impuestos.sfe.invoice.xml.eltr.*;
import bo.gob.impuestos.sfe.invoice.xml.header.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class InvoiceBatchValidatorService {

    private final static Integer IDENTITY_DOCUMENT_TYPE_NIT = 5;
    private final static Integer CODE_EXCEPTION = 0;
    private final static String NIT_CI_99001 = "99001";
    private final static String NIT_CI_99002 = "99002";
    private final static String NIT_CI_99003 = "99003";
    private final static String BUSINESS_NAME_99002 = "CONTROL TRIBUTARIO";
    private final static String BUSINESS_NAME_99003 = "VENTAS MENORES DEL DIA";
    private final static Integer IDENTITY_DOCUMENT_CI = 1;

    private final Environment environment;
    private final SoapUtil soapUtil;

    private final BatchRepository batchRepository;
    private final InvoiceBatchRepository invoiceBatchRepository;
    private final InvoiceLegendRepository invoiceLegendRepository;
    private final CompanyRepository companyRepository;
    private final CuisRepository cuisRepository;
    private final CufdRepository cufdRepository;
    private final ApprovedProductRepository approvedProductRepository;
    private final OfflineRepository offlineRepository;
    private final InvoiceRepository invoiceRepository;
    private final SectorDocumentTypeRepository sectorDocumentTypeRepository;
    private final BroadcastTypeRepository broadcastTypeRepository;
    private final InvoiceTypeRepository invoiceTypeRepository;

    private final SignatureService signatureService;

    /**
     * Método para la validación de lotes para emisión masiva.
     *
     * @return
     */
    public InvoiceMassiveRes batchValidate() {
        Optional<Batch> entity = batchRepository.findFirstByStatusOrderByDateAsc(BatchStatusEnum.PENDING);
        if (!entity.isPresent()) {
            return new InvoiceMassiveRes(SiatResponseCodes.ERROR_BATCH_NOT_FOUND, "No existen paquetes para validación");
        }

        Batch batchFromDb = entity.get();
        List<InvoiceBatch> listInvoiceBatches = invoiceBatchRepository.findByBatchId(batchFromDb.getId());


        ObjectMapper mapper = new ObjectMapper();
        for (InvoiceBatch invoiceBatch : listInvoiceBatches) {
            try {
                InvoiceIssueReq invoiceIssueReq = mapper.readValue(invoiceBatch.getInvoiceJson(), InvoiceIssueReq.class);
                invoiceIssueReq.setCompanyId(batchFromDb.getCompany().getId());

                XmlBaseInvoice invoice = XmlInvoiceAdapter.convert(invoiceIssueReq, batchFromDb.getCompany().getModalitySiat(), invoiceLegendRepository);
                InvoiceMassiveRes response = this.emitInvoice(invoiceIssueReq, invoice);

                if (response.getCode().equals(SiatResponseCodes.SUCCESS))
                    invoiceBatch.setInvoice((Invoice) response.getBody());
                else
                    invoiceBatch.setResponseMessage(response.getMessage());
                invoiceBatchRepository.save(invoiceBatch);
            } catch (Exception e) {
                invoiceBatch.setResponseMessage(e.getMessage());
                invoiceBatchRepository.save(invoiceBatch);
            }
        }
        batchFromDb.setStatus(BatchStatusEnum.VALIDATED);
        batchRepository.save(batchFromDb);

        return new InvoiceMassiveRes(SiatResponseCodes.SUCCESS, "El proceso de validación ha terminado correctamente");
    }

    /**
     * Método que realiza la emisión de la factura.
     *
     * @param request Solicitud.
     * @param invoice Factura.
     * @return
     */
    private InvoiceMassiveRes emitInvoice(InvoiceIssueReq request, XmlBaseInvoice invoice) {

        // 0. Verifica si se usa el tipo de moneda.
        if (null != request.getUseCurrencyType() && request.getUseCurrencyType()) {
            this.convertToCurrencyType(invoice, request);
        }

        // 1. Obtiene datos de la empresa.
        Optional<Company> companyFromDb = companyRepository.findCompanyByIdAndActiveIsTrue(new Long(request.getCompanyId()));
        Company company = null;
        if (companyFromDb.isPresent()) {
            company = companyFromDb.get();
        }

        // 2. Obtiene el CUIS activo.
        Optional<Cuis> cuisOptional = null;
        try {
            cuisOptional = this.obtainCuisActive(request);
        } catch (CuisNotFoundException e) {
            return new InvoiceMassiveRes(SiatResponseCodes.ERROR_BATCH_VALIDATE, "Cuis no valido");
        }
        Cuis cuisFromDb = cuisOptional.get();
        log.debug("3. Datos del código CUIS : {}", cuisFromDb);

        // 3. Obtiene el CUFD activo.
        Optional<Cufd> cufdOptional = null;
        try {
            cufdOptional = this.obtainCufdActive(request);
        } catch (CufdNotFoundException e) {
            return new InvoiceMassiveRes(SiatResponseCodes.ERROR_BATCH_VALIDATE, "Cufd no valido");
        }
        Cufd cufdFromDb = cufdOptional.get();
        log.debug("4. Datos del código CUFD : {}", cufdFromDb);

        // 4. Obtiene fecha y hora actual.
        ZonedDateTime dateTimeFromSiat = ZonedDateTime.now();

        // 5. Generación del código CUF.
        String cuf = this.generateCuf(invoice, company, request, dateTimeFromSiat, cufdFromDb);
        log.debug("6. Generación del codigo CUF: {}", cuf);

        InvoiceMassiveRes invoiceIssueRes = this.preValidateInvoice(invoice);
        if (!invoiceIssueRes.getCode().equals(SiatResponseCodes.SUCCESS))
            return invoiceIssueRes;

        // 7. Genera y valida la factura XML.
        invoiceIssueRes = this.populateInvoice(invoice, request, company, cuf, cufdFromDb, cuisFromDb, dateTimeFromSiat);
        if (!invoiceIssueRes.getCode().equals(SiatResponseCodes.SUCCESS))
            return invoiceIssueRes;
        invoice = (XmlBaseInvoice) invoiceIssueRes.getBody();
        log.debug("7. Factura con datos completos");

        try {
            String xmlInvoice = invoice.generate(true);
            if (invoice.isValidXml(xmlInvoice)) {
                invoiceIssueRes = this.validateInvoice(invoice);
                if (!invoiceIssueRes.getCode().equals(SiatResponseCodes.SUCCESS))
                    return invoiceIssueRes;

                // Obtiene la factura en formato JSON.
                ObjectMapper mapper = new ObjectMapper();
                mapper.setTimeZone(TimeZone.getTimeZone("America/La_Paz"));
                String jsonInvoice = mapper.writeValueAsString(invoice);

                // 8. Firma el documento XML generado.
                if (company.getModalitySiat().equals(ModalitySiatEnum.ELECTRONIC_BILLING)) {
                    xmlInvoice = signatureService.singXml(xmlInvoice, company.getId());
                    log.debug("8. Factura XML firmada");
                }

                // 9. Empaqueta el documento XML firmado en Gzip.
                byte[] gzipXml = FileCompress.textToGzip(xmlInvoice);
                log.debug("10. Documento compreso de la factura XML firmada");

                // 10. Obtiene el hash del documento compreso.
                String hashXml = FileHash.sha256(gzipXml);
                log.debug("11. Hash del documento compreso: {}", hashXml);

                // 11. Registro de la factura en base de datos.
                Invoice entity = new Invoice();
                entity.setInvoiceNumber(((XmlBaseHeaderInvoice) invoice.getCabecera()).getNumeroFactura());
                entity.setCuf(cuf);
                entity.setBroadcastDate(dateTimeFromSiat);
                entity.setInvoiceXml(xmlInvoice);
                entity.setInvoiceHash(hashXml);
                entity.setInvoiceJson(jsonInvoice);
                entity.setNit(((XmlBaseHeaderInvoice) invoice.getCabecera()).getNumeroDocumento());
                entity.setBusinessName(((XmlBaseHeaderInvoice) invoice.getCabecera()).getNombreRazonSocial());
                entity.setReceptionCode(null);
                entity.setStatus(InvoiceStatusEnum.PENDING);
                entity.setModalitySiat(company.getModalitySiat());
                entity.setCufd(cufdFromDb);
                entity.setSectorDocumentType(sectorDocumentTypeRepository.getBySiatIdAndCompanyIdAndActiveIsTrue(request.getDocumentSectorType(), company.getId().intValue()));
                entity.setBroadcastType(broadcastTypeRepository.getBySiatIdAndCompanyIdAndActiveIsTrue(SiatBroadcastType.BROADCAST_TYPE_MASSIVE, company.getId().intValue()));
                entity.setInvoiceType(invoiceTypeRepository.getBySiatIdAndCompanyIdAndActiveIsTrue(SiatInvoiceType.map.get(request.getDocumentSectorType()), company.getId().intValue()));
                entity = invoiceRepository.save(entity);

                log.debug("12. Factura emitida correctamente");
                return new InvoiceMassiveRes(SiatResponseCodes.SUCCESS, "La factura ha sido emitida correctamente", entity);

            } else {
                return new InvoiceMassiveRes(SiatResponseCodes.ERROR_BATCH_VALIDATE, "Error en la generación del documento fiscal");
            }
        } catch (InvoiceCannotBeProccesed e) {
            return new InvoiceMassiveRes(SiatResponseCodes.ERROR_BATCH_VALIDATE, e.getMessage());
        } catch (Exception e) {
            return new InvoiceMassiveRes(SiatResponseCodes.ERROR_BATCH_VALIDATE, e.getMessage());
        }
    }

    /**
     * Método que verifica si el sistema se encuentra EN LINEA.
     *
     * @return
     */
    private boolean isSiatOffline() {
        Optional<Offline> searchOffline = offlineRepository.findAllNative().stream().findFirst();
        //Optional<Offline> searchOffline = offlineRepository.findAll().stream().findFirst();
        if (searchOffline.isPresent()) {
            Offline updateOffline = searchOffline.get();
            return updateOffline.getActive();
        }
        return false;
    }

    /**
     * Método que obtiene el código CUIS vigente.
     *
     * @param request
     * @return
     * @throws CuisNotFoundException
     */
    private Optional<Cuis> obtainCuisActive(InvoiceIssueReq request) throws CuisNotFoundException {
        Optional<Cuis> cuisOptional = cuisRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(request.getPointSaleSiat(), request.getBranchOfficeSiat(), new Long(request.getCompanyId()));
        if (!cuisOptional.isPresent()) {
            throw new CuisNotFoundException();
        }
        return cuisOptional;
    }

    /**
     * Método que obtiene el código CUFD vigente.
     *
     * @param request
     * @return
     * @throws CufdNotFoundException
     */
    private Optional<Cufd> obtainCufdActive(InvoiceIssueReq request) throws CufdNotFoundException {
        Optional<Cufd> cufdOptional = cufdRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(request.getPointSaleSiat(), request.getBranchOfficeSiat(), new Long(request.getCompanyId()));
        if (!cufdOptional.isPresent()) {
            throw new CufdNotFoundException();
        }
        return cufdOptional;
    }

    /**
     * Método que genera el código CUF.
     *
     * @param invoice          Factura.
     * @param company          Empresa.
     * @param request          Solicitud.
     * @param dateTimeFromSiat Fecha y hora actual.
     * @param cufdFromDb       Código único diario.
     * @return
     */
    private String generateCuf(XmlBaseInvoice invoice, Company company, InvoiceIssueReq request, ZonedDateTime dateTimeFromSiat, Cufd cufdFromDb) {
        InvoiceUniqueCodeCufService invoiceUniqueCodeService = new InvoiceUniqueCodeCufService();
        InvoiceUniqueCodeGeneratorCufReq cufReq = new InvoiceUniqueCodeGeneratorCufReq();
        cufReq.setNit(company.getNit());
        cufReq.setDatetime(Date.from(dateTimeFromSiat.toInstant()));
        cufReq.setBranchOffice(request.getBranchOfficeSiat());
        cufReq.setModality(company.getModalitySiat().getKey());
        cufReq.setEmisionType(SiatBroadcastType.BROADCAST_TYPE_MASSIVE);
        cufReq.setInvoiceType(SiatInvoiceType.map.get(request.getDocumentSectorType()));
        cufReq.setDocumentSectorType(request.getDocumentSectorType());
        cufReq.setInvoiceNumber(((XmlBaseHeaderInvoice) invoice.getCabecera()).getNumeroFactura());
        cufReq.setPointOfSale(request.getPointSaleSiat());
        cufReq.setControlCode(cufdFromDb.getControlCode());
        Optional<InvoiceUniqueCodeGeneratorCufRes> result = invoiceUniqueCodeService.generateCuf(cufReq);
        return result.get().getCuf();
    }

    /**
     * Método que realiza la validación de la factura.
     *
     * @param invoice Factura.
     * @return
     */
    private InvoiceMassiveRes preValidateInvoice(XmlBaseInvoice invoice) {
        InvoiceMassiveRes response = new InvoiceMassiveRes();
        List<String> messageList = new ArrayList<>();

        if (invoice instanceof XmlPrevaloradaEltrInvoice || invoice instanceof XmlPrevaloradaCmpInvoice) {
            response.setCode(SiatResponseCodes.SUCCESS);
            return response;
        }

        // Verifica el número de documento diferente de 0.
        XmlBaseHeaderInvoice baseHeader = ((XmlBaseHeaderInvoice) invoice.getCabecera());
        if (baseHeader.getNumeroDocumento().equals("0")) {
            messageList.add("El número de documento debe ser diferente de 0");
        }
        // Verifica el contenido del complemento.
        if (!baseHeader.getCodigoTipoDocumentoIdentidad().equals(IDENTITY_DOCUMENT_CI) &&
            (baseHeader.getComplemento() != null && !baseHeader.getComplemento().equals(""))) {
            messageList.add("El complemento solo puede ser enviado con el tipo de documento: Carnet de Identidad");
        }

        if (!messageList.isEmpty()) {
            return new InvoiceMassiveRes(SiatResponseCodes.ERROR_BATCH_VALIDATE, messageList.get(0));
        }
        response.setCode(SiatResponseCodes.SUCCESS);
        return response;
    }

    /**
     * Método que completa los datos de la factura.
     *
     * @param invoice          Factura.
     * @param request          Solicitud.
     * @param company          Empresa.
     * @param cuf              Código único de la factura.
     * @param cufdFromDb       Código únido diario.
     * @param dateTimeFromSiat Fecha y hora actual.
     * @return
     */
    private InvoiceMassiveRes populateInvoice(XmlBaseInvoice invoice, InvoiceIssueReq request, Company company, String cuf, Cufd cufdFromDb, Cuis cuisFromDb, ZonedDateTime dateTimeFromSiat) {
        InvoiceMassiveRes response = new InvoiceMassiveRes();
        XmlBaseHeaderInvoice headerBase = (XmlBaseHeaderInvoice) invoice.getCabecera();
        headerBase.setBarcode(request.getBarcode());

        if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_BOLETO_AEREO)) {

            // Completa los datos de la cabecera de la factura.
            XmlBaseHeaderInvoice header = (XmlBaseHeaderInvoice) invoice.getCabecera();
            header.setNitEmisor(company.getNit());
            header.setRazonSocialEmisor(company.getName());
            header.setCuf(cuf);
            header.setCufd(cufdFromDb.getCufd());
            header.setCodigoSucursal(request.getBranchOfficeSiat());
            header.setDireccion(cufdFromDb.getAddress());
            header.setCodigoPuntoVenta(request.getPointSaleSiat());
            header.setFechaEmision(Date.from(dateTimeFromSiat.toInstant()));
            header.setCodigoDocumentoSector(request.getDocumentSectorType());

            response.setCode(SiatResponseCodes.SUCCESS);
            response.setBody(invoice);

            return response;

        } else if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_NOTA_CONCILIACION)) {

            // 7. Obtiene el producto homologado.
            List listProduct = approvedProductRepository.findAllByCompanyIdAndActive(company.getId());
            if (listProduct.size() == 0) {
                throw new DefaultTransactionException(ResponseMessages.ERROR_PRODUCTOS_HOMOLOGADOS,
                    ErrorEntities.INVOICE, ErrorKeys.ERR_RECORD_NOT_FOUND);
            }
            // Mapea la lista de productos homologados.
            List<ApprovedProductFullDto> listProductFull = ((ArrayList<Object[]>) listProduct).stream().map(x -> {
                ApprovedProductFullDto obj = new ApprovedProductFullDto();
                obj.setProductCode((String) x[1]);
                obj.setDescription((String) x[2]);
                obj.setActivityCodeSiat((int) x[3]);
                obj.setProductCodeSiat((int) x[4]);
                obj.setMeasurementUnitSiat((int) x[5]);
                return obj;
            }).collect(Collectors.toList());
            log.debug("7. Lista de productos homologados: {}", listProductFull);

            // Completa los datos de la cabecera de la factura.
            XmlBaseHeaderInvoice header = (XmlBaseHeaderInvoice) invoice.getCabecera();
            header.setNitEmisor(company.getNit());
            header.setRazonSocialEmisor(company.getName());
            header.setMunicipio(company.getCity());
            header.setTelefono(company.getPhone());
            header.setCuf(cuf);
            header.setCufd(cufdFromDb.getCufd());
            header.setCodigoSucursal(request.getBranchOfficeSiat());
            header.setDireccion(cufdFromDb.getAddress());
            header.setCodigoPuntoVenta(request.getPointSaleSiat());
            header.setFechaEmision(Date.from(dateTimeFromSiat.toInstant()));
            header.setCodigoDocumentoSector(request.getDocumentSectorType());

            // Verifica el numero de documento.
            if (!header.getNumeroDocumento().equals(NIT_CI_99001) &&
                !header.getNumeroDocumento().equals(NIT_CI_99002) &&
                !header.getNumeroDocumento().equals(NIT_CI_99003)) {
                if (header.getCodigoTipoDocumentoIdentidad().equals(IDENTITY_DOCUMENT_TYPE_NIT)) {
                    if (header.getCodigoExcepcion() == null ||
                        (header.getCodigoExcepcion() != null && header.getCodigoExcepcion().equals(CODE_EXCEPTION))) {
                        if (!this.isSiatOffline()) {
                            Long clientNit = Long.parseLong(header.getNumeroDocumento());
                            response = this.verifyNit(company, clientNit, request.getBranchOfficeSiat(), cuisFromDb);
                        }
                    }
                }
            } else {
                switch (header.getNumeroDocumento()) {
                    case NIT_CI_99002:
                        header.setNombreRazonSocial(BUSINESS_NAME_99002);
                        break;
                    case NIT_CI_99003:
                        header.setNombreRazonSocial(BUSINESS_NAME_99003);
                        break;
                }
            }

            // Completa los datos del detalle de la factura.
            List<XmlConciliacionDetail> detailInvoice = null;
            List<XmlConciliacionOriginalDetail> originalDetailInvoice = null;

            if (invoice instanceof XmlConciliacionCmpInvoice) {
                detailInvoice = ((XmlConciliacionCmpInvoice) invoice).getDetalleConciliacion();
                originalDetailInvoice = ((XmlConciliacionCmpInvoice) invoice).getDetalle();
            } else if (invoice instanceof XmlConciliacionEltrInvoice) {
                detailInvoice = ((XmlConciliacionEltrInvoice) invoice).getDetalleConciliacion();
                originalDetailInvoice = ((XmlConciliacionEltrInvoice) invoice).getDetalle();
            }

            for (XmlConciliacionDetail detail : detailInvoice) {

                Optional<ApprovedProductFullDto> approvedProductFullDto = listProductFull.stream().filter(p -> p.getProductCode().equals(detail.getCodigoProducto())).findFirst();
                if (!approvedProductFullDto.isPresent()) {
                    return new InvoiceMassiveRes(SiatResponseCodes.ERROR_BATCH_VALIDATE, String.format(ResponseMessages.ERROR_NOT_FOUND_HOMOLOGADO, detail.getCodigoProducto()));
                } else {
                    detail.setActividadEconomica(approvedProductFullDto.get().getActivityCodeSiat().toString());
                    detail.setCodigoProductoSin(approvedProductFullDto.get().getProductCodeSiat());
                    if (null == detail.getDescripcion() || detail.getDescripcion().equals(""))
                        detail.setDescripcion(approvedProductFullDto.get().getDescription());
                    detail.setUnidadMedida(approvedProductFullDto.get().getMeasurementUnitSiat());
                }
            }

            for (XmlConciliacionOriginalDetail detail : originalDetailInvoice) {

                Optional<ApprovedProductFullDto> approvedProductFullDto = listProductFull.stream().filter(p -> p.getProductCode().equals(detail.getCodigoProducto())).findFirst();
                if (!approvedProductFullDto.isPresent()) {
                    return new InvoiceMassiveRes(SiatResponseCodes.ERROR_BATCH_VALIDATE, String.format(ResponseMessages.ERROR_NOT_FOUND_HOMOLOGADO, detail.getCodigoProducto()));
                } else {
                    detail.setActividadEconomica(approvedProductFullDto.get().getActivityCodeSiat().toString());
                    detail.setCodigoProductoSin(approvedProductFullDto.get().getProductCodeSiat());
                    if (null == detail.getDescripcion() || detail.getDescripcion().equals(""))
                        detail.setDescripcion(approvedProductFullDto.get().getDescription());
                    detail.setUnidadMedida(approvedProductFullDto.get().getMeasurementUnitSiat());
                }
            }

            response.setCode(SiatResponseCodes.SUCCESS);
            response.setBody(invoice);

            return response;
        } else {
            //homologacion solo cuando no sea boleto aereo
            // 7. Obtiene el producto homologado.
            List listProduct = approvedProductRepository.findAllByCompanyIdAndActive(company.getId());
            if (listProduct.size() == 0) {
                throw new DefaultTransactionException(
                    ResponseMessages.ERROR_PRODUCTOS_HOMOLOGADOS,
                    ErrorEntities.INVOICE,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
                //return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "No existen productos homologados para la empresa.");
            }
            // Mapea la lista de productos homologados.
            List<ApprovedProductFullDto> listProductFull = ((ArrayList<Object[]>) listProduct).stream().map(x -> {
                ApprovedProductFullDto obj = new ApprovedProductFullDto();
                obj.setProductCode((String) x[1]);
                obj.setDescription((String) x[2]);
                obj.setActivityCodeSiat((int) x[3]);
                obj.setProductCodeSiat((int) x[4]);
                obj.setMeasurementUnitSiat((int) x[5]);
                return obj;
            }).collect(Collectors.toList());
            log.debug("7. Lista de productos homologados: {}", listProductFull);

            // Completa los datos de la cabecera de la factura.
            XmlBaseHeaderInvoice header = (XmlBaseHeaderInvoice) invoice.getCabecera();
            header.setNitEmisor(company.getNit());
            header.setRazonSocialEmisor(company.getName());
            header.setMunicipio(company.getCity());
            header.setTelefono(company.getPhone());
            header.setCuf(cuf);
            header.setCufd(cufdFromDb.getCufd());
            header.setCodigoSucursal(request.getBranchOfficeSiat());
            header.setDireccion(cufdFromDb.getAddress());
            header.setCodigoPuntoVenta(request.getPointSaleSiat());
            header.setFechaEmision(Date.from(dateTimeFromSiat.toInstant()));
            header.setCodigoDocumentoSector(request.getDocumentSectorType());

            if (header.getNumeroTarjeta() != null && header.getNumeroTarjeta() != 0) {
                Long cardNumber = this.obfuscatCardNumber(header.getNumeroTarjeta());
                header.setNumeroTarjeta(cardNumber);
            }

            if (!request.getDocumentSectorType().equals(XmlSectorType.INVOICE_PREVALORADA)) {
                // Verifica el numero de documento.
                if (!header.getNumeroDocumento().equals(NIT_CI_99001) &&
                    !header.getNumeroDocumento().equals(NIT_CI_99002) &&
                    !header.getNumeroDocumento().equals(NIT_CI_99003)) {
                    if (header.getCodigoTipoDocumentoIdentidad().equals(IDENTITY_DOCUMENT_TYPE_NIT)) {
                        if (header.getCodigoExcepcion() == null ||
                            (header.getCodigoExcepcion() != null && header.getCodigoExcepcion().equals(CODE_EXCEPTION))) {
                            if (!this.isSiatOffline()) {
                                Long clientNit = Long.parseLong(header.getNumeroDocumento());
                                response = this.verifyNit(company, clientNit, request.getBranchOfficeSiat(), cuisFromDb);
                            }
                        }
                    }
                } else {
                    switch (header.getNumeroDocumento()) {
                        case NIT_CI_99002:
                            header.setNombreRazonSocial(BUSINESS_NAME_99002);
                            break;
                        case NIT_CI_99003:
                            header.setNombreRazonSocial(BUSINESS_NAME_99003);
                            break;
                    }
                }
            }

            // Completa los datos del detalle de la factura.
            List<XmlBaseDetailInvoice> detailInvoice = invoice.getDetalle();
            for (XmlBaseDetailInvoice detail : detailInvoice) {

                Optional<ApprovedProductFullDto> approvedProductFullDto = listProductFull.stream().filter(p -> p.getProductCode().equals(detail.getCodigoProducto())).findFirst();
                if (!approvedProductFullDto.isPresent()) {
                    return new InvoiceMassiveRes(SiatResponseCodes.ERROR_BATCH_VALIDATE, String.format(ResponseMessages.ERROR_NOT_FOUND_HOMOLOGADO, detail.getCodigoProducto()));
                } else {
                    detail.setActividadEconomica(approvedProductFullDto.get().getActivityCodeSiat().toString());
                    detail.setCodigoProductoSin(approvedProductFullDto.get().getProductCodeSiat());
                    if (null == detail.getDescripcion() || detail.getDescripcion().equals(""))
                        detail.setDescripcion(approvedProductFullDto.get().getDescription());
                    detail.setUnidadMedida(approvedProductFullDto.get().getMeasurementUnitSiat());
                }
            }
            response.setCode(SiatResponseCodes.SUCCESS);
            response.setBody(invoice);
            return response;
        }
    }

    /**
     * Método que realiza la validación de la factura.
     *
     * @param invoice Factura.
     * @return
     */
    private InvoiceMassiveRes validateInvoice(XmlBaseInvoice invoice) {

        InvoiceMassiveRes response = new InvoiceMassiveRes();

        if (invoice instanceof XmlBoletoAereoCmpInvoice) {
            response.setCode(SiatResponseCodes.SUCCESS);
            return response;
        }

        List<String> messageList = new ArrayList<>();

        if (invoice instanceof XmlCompraVentaEltrInvoice || invoice instanceof XmlCompraVentaCmpInvoice ||
            invoice instanceof XmlAlquilerBienesInmueblesEltrInvoice || invoice instanceof XmlAlquilerBienesInmueblesCmpInvoice ||
            invoice instanceof XmlZonaFrancaEltrInvoice || invoice instanceof XmlZonaFrancaCmpInvoice ||
            invoice instanceof XmlTuristicoHospedajeEltrInvoice || invoice instanceof XmlTuristicoHospedajeCmpInvoice ||
            invoice instanceof XmlAlimentariaAbastecimientoEltrInvoice || invoice instanceof XmlAlimentariaAbastecimientoCmpInvoice ||
            invoice instanceof XmlSectorEducativoEltrInvoice || invoice instanceof XmlSectorEducativoCmpInvoice ||
            invoice instanceof XmlHidrocarburosEltrInvoice || invoice instanceof XmlHidrocarburosCmpInvoice ||
            invoice instanceof XmlHotelEltrInvoice || invoice instanceof XmlHotelCmpInvoice ||
            invoice instanceof XmlHospitalClinicaEltrInvoice || invoice instanceof XmlHospitalClinicaCmpInvoice ||
            invoice instanceof XmlTelecomunicacionesEltrInvoice || invoice instanceof XmlTelecomunicacionesCmpInvoice ||
//            invoice instanceof XmlExportacionServicioEltrInvoice || invoice instanceof XmlExportacionServicioCmpInvoice ||
            invoice instanceof XmlSegurosEltrInvoice || invoice instanceof XmlSegurosCmpInvoice ||
            invoice instanceof XmlPrevaloradaEltrInvoice || invoice instanceof XmlPrevaloradaCmpInvoice) {

            XmlBaseHeaderInvoice header = ((XmlBaseHeaderInvoice) invoice.getCabecera());
            List<XmlBaseDetailInvoice> detailList = (List<XmlBaseDetailInvoice>) invoice.getDetalle();

            BigDecimal totalAmount = new BigDecimal(0);
            for (XmlBaseDetailInvoice detail : detailList) {
                BigDecimal subTotal = (detail.getCantidad().multiply(detail.getPrecioUnitario())).subtract(detail.getMontoDescuento() != null ? detail.getMontoDescuento() : BigDecimal.ZERO);
                totalAmount = totalAmount.add(subTotal);
                if (detail.getSubTotal().compareTo(subTotal) != 0) {
                    messageList.add(String.format("El subtotal del producto <%s> es incorrecto.", detail.getCodigoProducto()));
                }
            }
            totalAmount = totalAmount.subtract(header.getDescuentoAdicional() != null ? header.getDescuentoAdicional() : BigDecimal.ZERO);
            if (totalAmount.compareTo(header.getMontoTotal()) != 0) {
                messageList.add(String.format("El monto total es incorrecta."));
            }
            if (header.getMontoTotalMoneda().compareTo(totalAmount.divide(header.getTipoCambio(), 2, RoundingMode.HALF_UP)) != 0) {
                messageList.add(String.format("El monto total moneda es incorrecta."));
            }

//            if (invoice instanceof XmlCompraVentaEltrInvoice || invoice instanceof XmlCompraVentaCmpInvoice) {
//                if (header.getMontoGiftCard() != null &&
//                    header.getMontoTotalSujetoIva().compareTo(totalAmount.subtract(header.getMontoGiftCard())) != 0) {
//                    messageList.add(String.format("El monto total sujeto iva es incorrecta."));
//                }
//            }
            if (invoice instanceof XmlHidrocarburosEltrInvoice || invoice instanceof XmlHidrocarburosCmpInvoice) {
                XmlHidrocarburosHeader headerH = (XmlHidrocarburosHeader) invoice.getCabecera();
                if (headerH.getMontoTotalSujetoIvaLey317().compareTo(totalAmount.multiply(new BigDecimal(7)).divide(new BigDecimal(10), 2, RoundingMode.HALF_UP)) != 0) {
                    messageList.add(String.format("El monto sujeto a credito fiscal Ley 317 es incorrecta."));
                }
            }
        }

        if (invoice instanceof XmlComercialExportacionEltrInvoice || invoice instanceof XmlComercialExportacionCmpInvoice) {
            XmlComercialExportacionHeader header = ((XmlComercialExportacionHeader) invoice.getCabecera());
            List<XmlComercialExportacionDetail> detailList = (List<XmlComercialExportacionDetail>) invoice.getDetalle();

            BigDecimal detailAmount = new BigDecimal(0);
            for (XmlBaseDetailInvoice detail : detailList) {
                BigDecimal subTotal = (detail.getCantidad().multiply(detail.getPrecioUnitario())).subtract(detail.getMontoDescuento() != null ? detail.getMontoDescuento() : BigDecimal.ZERO);
                detailAmount = detailAmount.add(subTotal);
                if (detail.getSubTotal().compareTo(subTotal) != 0) {
                    messageList.add(String.format("El subtotal del producto <%s> es incorrecto.", detail.getCodigoProducto()));
                }
            }

            if (header.getMontoDetalle().compareTo(detailAmount) != 0) {
                messageList.add(String.format("El monto detalle es incorrecto."));
            }

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JSONObject object = objectMapper.convertValue(header.getCostosGastosNacionales(), JSONObject.class);
                Iterator<String> keysItr = object.keys();
                BigDecimal totalNalFob = new BigDecimal(0);
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    BigDecimal value = object.getBigDecimal(key);
                    totalNalFob = totalNalFob.add(value);
                }
                totalNalFob = totalNalFob.add(detailAmount);

                object = objectMapper.convertValue(header.getCostosGastosInternacionales(), JSONObject.class);
                keysItr = object.keys();
                BigDecimal totalExt = new BigDecimal(0);
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    BigDecimal value = object.getBigDecimal(key);
                    totalExt = totalExt.add(value);
                }
                BigDecimal currencyTotal = totalNalFob.add(totalExt);
                if (currencyTotal.compareTo(header.getTotalGastosNacionalesFob().add(header.getTotalGastosInternacionales())) != 0) {
                    messageList.add(String.format("El monto total moneda es incorrecto."));
                }
                if (currencyTotal.compareTo(header.getMontoTotal().divide(header.getTipoCambio(), 2, RoundingMode.HALF_UP)) != 0) {
                    messageList.add(String.format("El monto total es incorrecto."));
                }
            } catch (JSONException e) {
                log.debug(e.getMessage());
            }
        }

        if (invoice instanceof XmlNotaCreditoDebitoEltrInvoice || invoice instanceof XmlNotaCreditoDebitoCmpInvoice) {
            XmlNotaCreditoDebitoHeader header = ((XmlNotaCreditoDebitoHeader) invoice.getCabecera());
            List<XmlNotaCreditoDebitoDetail> detailList = (List<XmlNotaCreditoDebitoDetail>) invoice.getDetalle();

            BigDecimal totalAmountOriginal = new BigDecimal(0);
            for (XmlBaseDetailInvoice detail : detailList.stream().filter(x -> x.getCodigoDetalleTransaccion().equals(1)).collect(Collectors.toList())) {
                BigDecimal subTotal = (detail.getCantidad().multiply(detail.getPrecioUnitario())).subtract(detail.getMontoDescuento() != null ? detail.getMontoDescuento() : BigDecimal.ZERO);
                totalAmountOriginal = totalAmountOriginal.add(subTotal);
                if (detail.getSubTotal().compareTo(subTotal) != 0) {
                    messageList.add(String.format("El subtotal del producto <%s> es incorrecto.", detail.getCodigoProducto()));
                }
            }
            if (header.getMontoTotalOriginal().compareTo(totalAmountOriginal) != 0) {
                messageList.add(String.format("El monto total original es incorrecto."));
            }

            BigDecimal totalAmountReturn = new BigDecimal(0);
            for (XmlBaseDetailInvoice detail : detailList.stream().filter(x -> x.getCodigoDetalleTransaccion().equals(2)).collect(Collectors.toList())) {
                BigDecimal subTotal = (detail.getCantidad().multiply(detail.getPrecioUnitario())).subtract(detail.getMontoDescuento() != null ? detail.getMontoDescuento() : BigDecimal.ZERO);
                totalAmountReturn = totalAmountReturn.add(subTotal);
                if (detail.getSubTotal().compareTo(subTotal) != 0) {
                    messageList.add(String.format("El subtotal del producto <%s> es incorrecto.", detail.getCodigoProducto()));
                }
            }
            totalAmountReturn = totalAmountReturn.subtract(header.getMontoDescuentoCreditoDebito());
            if (header.getMontoTotalDevuelto().compareTo(totalAmountReturn) != 0) {
                messageList.add(String.format("El monto total devuelto es incorrecto."));
            }
            if (header.getMontoEfectivoCreditoDebito().compareTo(totalAmountReturn.multiply(new BigDecimal(13)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)) != 0) {
                messageList.add(String.format("El monto efectivo credito débito es incorrecto."));
            }
        }

        if (invoice instanceof XmlConciliacionEltrInvoice || invoice instanceof XmlConciliacionCmpInvoice) {
//            XmlConciliacionHeader header = ((XmlConciliacionHeader) invoice.getCabecera());
//            List<XmlConciliacionDetail> detailList = invoice instanceof XmlConciliacionEltrInvoice ?
//                ((XmlConciliacionEltrInvoice) invoice).getDetalleConciliacion() :
//                ((XmlConciliacionCmpInvoice) invoice).getDetalleConciliacion();
//
//            BigDecimal totalAmountC = new BigDecimal(0);
//            for (XmlConciliacionDetail detail : detailList) {
//                BigDecimal amountC = detail.getMontoFinal().subtract(detail.getMontoOriginal() != null ? detail.getMontoOriginal() : BigDecimal.ZERO);
//                totalAmountC = totalAmountC.add(amountC);
//                if (detail.getMontoConciliado().compareTo(amountC) != 0) {
//                    messageList.add(String.format("El monto conciliado del producto <%s> es incorrecto.", detail.getCodigoProducto()));
//                }
//            }
//            if (header.getMontoTotalConciliado().compareTo(totalAmountC) != 0) {
//                messageList.add(String.format("El monto total conciliado es incorrecto."));
//            }
        }

        if (invoice instanceof XmlServiciosBasicosEltrInvoice || invoice instanceof XmlServiciosBasicosCmpInvoice) {
            XmlServiciosBasicosHeader header = ((XmlServiciosBasicosHeader) invoice.getCabecera());
            List<XmlServiciosBasicosDetail> detailList = (List<XmlServiciosBasicosDetail>) invoice.getDetalle();

            BigDecimal sumSubTotal = new BigDecimal(0);
            for (XmlServiciosBasicosDetail detail : detailList) {
                BigDecimal subTotal = (detail.getCantidad().multiply(detail.getPrecioUnitario())).subtract(detail.getMontoDescuento() != null ? detail.getMontoDescuento() : BigDecimal.ZERO);
                sumSubTotal = sumSubTotal.add(subTotal);
                if (detail.getSubTotal().compareTo(subTotal) != 0) {
                    messageList.add(String.format("El subtotal del producto <%s> es incorrecto.", detail.getCodigoProducto()));
                }
            }
            BigDecimal amountTotal = sumSubTotal.add(header.getTasaAseo()).add(header.getTasaAlumbrado()).add(header.getOtrasTasas())
                .add(header.getAjusteSujetoIva()).add(header.getOtrosPagosNoSujetoIva())
                .subtract(header.getDescuentoAdicional() != null ? header.getDescuentoAdicional() : BigDecimal.ZERO);
            if (amountTotal.compareTo(header.getMontoTotal()) != 0) {
                messageList.add(String.format("El monto total es incorrecta."));
            }
            if (header.getMontoTotalMoneda().compareTo(amountTotal.divide(header.getTipoCambio(), 2, RoundingMode.HALF_UP)) != 0) {
                messageList.add(String.format("El monto total moneda es incorrecta."));
            }
            if (header.getMontoTotalSujetoIva().compareTo(amountTotal.subtract(header.getTasaAseo())
                .subtract(header.getTasaAlumbrado()).subtract(header.getOtrasTasas()).subtract(header.getOtrosPagosNoSujetoIva())) != 0) {
                messageList.add(String.format("El monto total sujeto iva es incorrecta."));
            }
        }

        if (!messageList.isEmpty()) {
            return new InvoiceMassiveRes(SiatResponseCodes.ERROR_BATCH_VALIDATE, messageList.get(0));
        }
        response.setCode(SiatResponseCodes.SUCCESS);
        return response;
    }

    /**
     * Método que verifica el Nit con el servicio del Siat.
     *
     * @param company          Empresa.
     * @param clientNit        Nit del cliente.
     * @param branchOfficeSiat Sucursal.
     * @param cuisFromDb       Código Cuis.
     * @return
     */
    private InvoiceMassiveRes verifyNit(Company company, Long clientNit, Integer branchOfficeSiat, Cuis cuisFromDb) {
        InvoiceMassiveRes result = new InvoiceMassiveRes();

        SolicitudVerificarNit requestNit = new SolicitudVerificarNit();
        requestNit.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
        requestNit.setCodigoModalidad(company.getModalitySiat().getKey());
        requestNit.setCodigoSistema(company.getSystemCode());
        requestNit.setCodigoSucursal(branchOfficeSiat);
        requestNit.setNit(company.getNit());
        requestNit.setCuis(cuisFromDb.getCuis());
        requestNit.setNitParaVerificacion(clientNit);

        ServicioFacturacionCodigos service = soapUtil.getCodeService(company.getToken());
        RespuestaVerificarNit response = service.verificarNit(requestNit);

        if (!response.isTransaccion()) {
            throw new InvoiceCannotBeProccesed(
                SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getMensajesList().get(0).getDescripcion());
        }
        result.setCode(SiatResponseCodes.SUCCESS);
        return result;
    }

    private Long obfuscatCardNumber(Long cardNumber) {
        String newNroCard = "XXXX00000000YYYY";
        String nroCard = cardNumber.toString();
        if (nroCard.length() >= 4) {
            newNroCard = newNroCard.replace("XXXX", nroCard.substring(0, 4));
            newNroCard = newNroCard.replace("YYYY", nroCard.substring(nroCard.length() - 4));
            return Long.parseLong(newNroCard);
        }
        return cardNumber;
    }

    /**
     * Método que realiza la conversión al tipo de moneda.
     *
     * @param invoice Factura.
     * @param request Solicitud.
     * @return
     */
    private void convertToCurrencyType(XmlBaseInvoice invoice, InvoiceIssueReq request) {
        // Obtiene la cabecera de la factura y tipo de cambio.
        XmlBaseHeaderInvoice header = (XmlBaseHeaderInvoice) invoice.getCabecera();
        BigDecimal exchangeRate = header.getTipoCambio();

        if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_NOTA_CREDITO_DEBITO)) {
            BigDecimal totalAmountOriginal = new BigDecimal(0);
            BigDecimal totalAmountReturn = new BigDecimal(0);

            // Obtiene el detalle de la factura de nota credito debito.
            List<XmlNotaCreditoDebitoDetail> detailInvoiceNdc = invoice.getDetalle();
            for (XmlNotaCreditoDebitoDetail detail : detailInvoiceNdc) {
                if (null != detail.getPrecioUnitario())
                    detail.setPrecioUnitario(exchangeRate.multiply(detail.getPrecioUnitario()).setScale(2, RoundingMode.HALF_UP));
                if (null != detail.getMontoDescuento())
                    detail.setMontoDescuento(exchangeRate.multiply(detail.getMontoDescuento()).setScale(2, RoundingMode.HALF_UP));

                BigDecimal subTotal = (detail.getCantidad()
                    .multiply(detail.getPrecioUnitario()))
                    .subtract(detail.getMontoDescuento() != null ? detail.getMontoDescuento() : BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
                detail.setSubTotal(subTotal);

                if (detail.getCodigoDetalleTransaccion().equals(1))
                    totalAmountOriginal = totalAmountOriginal.add(subTotal);
                else
                    totalAmountReturn = totalAmountReturn.add(subTotal);
            }

            // Obtiene la cabecera de la nota credito debito.
            XmlNotaCreditoDebitoHeader headerNcd = (XmlNotaCreditoDebitoHeader) invoice.getCabecera();

            headerNcd.setMontoDescuentoCreditoDebito(exchangeRate.multiply(headerNcd.getMontoDescuentoCreditoDebito() != null ? headerNcd.getMontoDescuentoCreditoDebito() : BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
            headerNcd.setMontoTotalOriginal(totalAmountOriginal);
            totalAmountReturn = totalAmountReturn.subtract(headerNcd.getMontoDescuentoCreditoDebito() != null ? headerNcd.getMontoDescuentoCreditoDebito() : BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            headerNcd.setMontoTotalDevuelto(totalAmountReturn);
            headerNcd.setMontoEfectivoCreditoDebito(totalAmountReturn.multiply(new BigDecimal(13)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));

        } else if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_NOTA_CONCILIACION)) {
            XmlConciliacionHeader headerNc = ((XmlConciliacionHeader) invoice.getCabecera());

            BigDecimal totalAmountOriginal = new BigDecimal(0);
            // Obtiene el detalle de la factura.
            List<XmlBaseDetailInvoice> detailInvoice = invoice.getDetalle();
            for (XmlBaseDetailInvoice detail : detailInvoice) {
                if (null != detail.getPrecioUnitario())
                    detail.setPrecioUnitario(exchangeRate.multiply(detail.getPrecioUnitario()).setScale(2, RoundingMode.HALF_UP));
                if (null != detail.getMontoDescuento())
                    detail.setMontoDescuento(exchangeRate.multiply(detail.getMontoDescuento()).setScale(2, RoundingMode.HALF_UP));

                BigDecimal subTotal = (detail.getCantidad()
                    .multiply(detail.getPrecioUnitario()))
                    .subtract(detail.getMontoDescuento() != null ? detail.getMontoDescuento() : BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
                detail.setSubTotal(subTotal);

                totalAmountOriginal = totalAmountOriginal.add(subTotal);
            }
            headerNc.setMontoTotalOriginal(totalAmountOriginal);

            BigDecimal totalAmountReconciled = new BigDecimal(0);
            // Obtiene el detalle de la conciliación.
            List<XmlConciliacionDetail> detailList = invoice instanceof XmlConciliacionEltrInvoice ?
                ((XmlConciliacionEltrInvoice) invoice).getDetalleConciliacion() : ((XmlConciliacionCmpInvoice) invoice).getDetalleConciliacion();
            for (XmlConciliacionDetail detail : detailList) {
                detail.setMontoOriginal(exchangeRate.multiply(detail.getMontoOriginal()).setScale(2, RoundingMode.HALF_UP));
                detail.setMontoFinal(exchangeRate.multiply(detail.getMontoFinal()).setScale(2, RoundingMode.HALF_UP));
                detail.setMontoConciliado(exchangeRate.multiply(detail.getMontoConciliado()).setScale(2, RoundingMode.HALF_UP));

                totalAmountReconciled = totalAmountReconciled.add(detail.getMontoConciliado());
            }
            headerNc.setMontoTotalConciliado(totalAmountReconciled);

            headerNc.setDebitoFiscalIva(exchangeRate.multiply(headerNc.getDebitoFiscalIva()).setScale(2, RoundingMode.HALF_UP));
            headerNc.setCreditoFiscalIva(exchangeRate.multiply(headerNc.getCreditoFiscalIva()).setScale(2, RoundingMode.HALF_UP));
        } else {
            if (null != header.getMontoGiftCard())
                header.setMontoGiftCard(exchangeRate.multiply(header.getMontoGiftCard()).setScale(2, RoundingMode.HALF_UP));
            if (null != header.getDescuentoAdicional())
                header.setDescuentoAdicional(exchangeRate.multiply(header.getDescuentoAdicional()).setScale(2, RoundingMode.HALF_UP));

            BigDecimal totalAmount = new BigDecimal(0);
            // Obtiene el detalle de la factura.
            List<XmlBaseDetailInvoice> detailInvoice = invoice.getDetalle();
            for (XmlBaseDetailInvoice detail : detailInvoice) {
                if (null != detail.getPrecioUnitario())
                    detail.setPrecioUnitario(exchangeRate.multiply(detail.getPrecioUnitario()).setScale(2, RoundingMode.HALF_UP));
                if (null != detail.getMontoDescuento())
                    detail.setMontoDescuento(exchangeRate.multiply(detail.getMontoDescuento()).setScale(2, RoundingMode.HALF_UP));

                BigDecimal subTotal = (detail.getCantidad()
                    .multiply(detail.getPrecioUnitario()))
                    .subtract(detail.getMontoDescuento() != null ? detail.getMontoDescuento() : BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
                detail.setSubTotal(subTotal);

                totalAmount = totalAmount.add(subTotal);
            }

            totalAmount = totalAmount.subtract(header.getDescuentoAdicional() != null ? header.getDescuentoAdicional() : BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            header.setMontoTotal(totalAmount);

            BigDecimal totalCurrencyAmount = totalAmount.divide(header.getTipoCambio(), 2, RoundingMode.HALF_UP);
            header.setMontoTotalMoneda(totalCurrencyAmount);

            if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_COMPRA_VENTA) ||
                request.getDocumentSectorType().equals(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES) ||
                request.getDocumentSectorType().equals(XmlSectorType.INVOICE_SECTOR_EDUCATIVO) ||
                request.getDocumentSectorType().equals(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS) ||
                request.getDocumentSectorType().equals(XmlSectorType.INVOICE_FACTURAS_HOTELES) ||
                request.getDocumentSectorType().equals(XmlSectorType.INVOICE_HOSPITAL_CLINICAS) ||
                request.getDocumentSectorType().equals(XmlSectorType.INVOICE_TELECOMUNICACIONES) ||
                request.getDocumentSectorType().equals(XmlSectorType.INVOICE_ZONA_FRANCA) ||
                request.getDocumentSectorType().equals(XmlSectorType.INVOICE_SEGUROS) ||
                request.getDocumentSectorType().equals(XmlSectorType.INVOICE_PREVALORADA)) {
                header.setMontoTotalSujetoIva(totalAmount);
            }

            if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS)) {
                header.setMontoTotalMoneda(totalAmount);
                header.setMontoTotal(exchangeRate.multiply(totalAmount).setScale(2, RoundingMode.HALF_UP));
            }

            if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS)) {
                XmlHidrocarburosHeader header1 = (XmlHidrocarburosHeader) invoice.getCabecera();
                header1.setMontoTotalSujetoIvaLey317(totalAmount.multiply(new BigDecimal(7)).divide(new BigDecimal(10), 2, RoundingMode.HALF_UP));
            }

            if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION)) {
                XmlComercialExportacionHeader headerCex = (XmlComercialExportacionHeader) invoice.getCabecera();
                headerCex.setMontoDetalle(totalAmount);

                try {
                    DecimalFormat df = new DecimalFormat("0.00");

                    // Gastos nacionales.
                    ObjectMapper objectMapper = new ObjectMapper();
                    JSONObject object = objectMapper.convertValue(headerCex.getCostosGastosNacionales(), JSONObject.class);
                    Iterator<String> keysItr = object.keys();
                    BigDecimal totalNalFob = new BigDecimal(0);
                    ObjectNode costNal = objectMapper.createObjectNode();
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        BigDecimal value = exchangeRate.multiply(object.getBigDecimal(key)).setScale(2, RoundingMode.HALF_UP);
                        totalNalFob = totalNalFob.add(value);
                        costNal.put(key, df.format(value).replace(",", "."));
                    }
                    headerCex.setCostosGastosNacionales(objectMapper.writeValueAsString(costNal));
                    totalNalFob = totalNalFob.add(totalAmount);
                    headerCex.setTotalGastosNacionalesFob(totalNalFob);

                    // Gastos internacionales.
                    object = objectMapper.convertValue(headerCex.getCostosGastosInternacionales(), JSONObject.class);
                    keysItr = object.keys();
                    BigDecimal totalExt = new BigDecimal(0);
                    ObjectNode costExt = objectMapper.createObjectNode();
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        BigDecimal value = exchangeRate.multiply(object.getBigDecimal(key)).setScale(2, RoundingMode.HALF_UP);
                        totalExt = totalExt.add(value);
                        costExt.put(key, df.format(value).replace(",", "."));
                    }
                    headerCex.setCostosGastosInternacionales(objectMapper.writeValueAsString(costExt));
                    headerCex.setTotalGastosInternacionales(totalExt);

                    BigDecimal currencyTotal = totalNalFob.add(totalExt);
                    headerCex.setMontoTotalMoneda(currencyTotal);
                    headerCex.setMontoTotal(exchangeRate.multiply(currencyTotal).setScale(2, RoundingMode.HALF_UP));
                } catch (JSONException | JsonProcessingException e) {
                    log.debug(e.getMessage());
                }
            }

            if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_SERVICIOS_BASICOS)) {
                XmlServiciosBasicosHeader headerSb = (XmlServiciosBasicosHeader) invoice.getCabecera();

                try {
                    DecimalFormat df = new DecimalFormat("0.00");
                    // Ajustes No sujeto a Iva.
                    ObjectMapper objectMapper = new ObjectMapper();
                    JSONObject object = objectMapper.convertValue(headerSb.getDetalleAjusteNoSujetoIva(), JSONObject.class);
                    Iterator<String> keysItr = object.keys();
                    BigDecimal totalNoSujIva = new BigDecimal(0);
                    ObjectNode detailNoSujIva = objectMapper.createObjectNode();
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        BigDecimal value = exchangeRate.multiply(object.getBigDecimal(key)).setScale(2, RoundingMode.HALF_UP);
                        totalNoSujIva = totalNoSujIva.add(value);
                        detailNoSujIva.put(key, df.format(value).replace(",", "."));
                    }
                    headerSb.setDetalleAjusteNoSujetoIva(objectMapper.writeValueAsString(detailNoSujIva));
                    headerSb.setAjusteNoSujetoIva(totalNoSujIva);

                    // Ajustes sujeto a Iva.
                    object = objectMapper.convertValue(headerSb.getDetalleAjusteSujetoIva(), JSONObject.class);
                    keysItr = object.keys();
                    BigDecimal totalSujIva = new BigDecimal(0);
                    ObjectNode detailSujIva = objectMapper.createObjectNode();
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        BigDecimal value = exchangeRate.multiply(object.getBigDecimal(key)).setScale(2, RoundingMode.HALF_UP);
                        totalSujIva = totalSujIva.add(value);
                        detailSujIva.put(key, df.format(value).replace(",", "."));
                    }
                    headerSb.setDetalleAjusteSujetoIva(objectMapper.writeValueAsString(detailSujIva));
                    headerSb.setAjusteSujetoIva(totalSujIva);

                    // Otros pagos no sujeto Iva.
                    object = objectMapper.convertValue(headerSb.getDetalleOtrosPagosNoSujetoIva(), JSONObject.class);
                    keysItr = object.keys();
                    BigDecimal totalOtherNoSujIva = new BigDecimal(0);
                    ObjectNode detailOtherNoSujIva = objectMapper.createObjectNode();
                    while (keysItr.hasNext()) {
                        String key = keysItr.next();
                        BigDecimal value = exchangeRate.multiply(object.getBigDecimal(key)).setScale(2, RoundingMode.HALF_UP);
                        totalOtherNoSujIva = totalOtherNoSujIva.add(value);
                        detailOtherNoSujIva.put(key, df.format(value).replace(",", "."));
                    }
                    headerSb.setDetalleOtrosPagosNoSujetoIva(objectMapper.writeValueAsString(detailOtherNoSujIva));
                    headerSb.setOtrosPagosNoSujetoIva(totalOtherNoSujIva);

                } catch (JSONException | JsonProcessingException e) {
                    log.debug(e.getMessage());
                }

                if (null != headerSb.getMontoDescuentoLey1886())
                    headerSb.setMontoDescuentoLey1886(exchangeRate.multiply(headerSb.getMontoDescuentoLey1886()).setScale(2, RoundingMode.HALF_UP));
                if (null != headerSb.getMontoDescuentoTarifaDignidad())
                    headerSb.setMontoDescuentoTarifaDignidad(exchangeRate.multiply(headerSb.getMontoDescuentoTarifaDignidad()).setScale(2, RoundingMode.HALF_UP));
                if (null != headerSb.getTasaAseo())
                    headerSb.setTasaAseo(exchangeRate.multiply(headerSb.getTasaAseo()).setScale(2, RoundingMode.HALF_UP));
                if (null != headerSb.getTasaAlumbrado())
                    headerSb.setTasaAlumbrado(exchangeRate.multiply(headerSb.getTasaAlumbrado()).setScale(2, RoundingMode.HALF_UP));

                headerSb.setMontoTotal(totalAmount.add(headerSb.getTasaAseo()).add(headerSb.getTasaAlumbrado())
                    .add(headerSb.getAjusteNoSujetoIva()).add(headerSb.getAjusteSujetoIva()).add(headerSb.getOtrosPagosNoSujetoIva()));

                headerSb.setMontoTotalSujetoIva(headerSb.getMontoTotal().subtract(headerSb.getTasaAseo()).
                    subtract(headerSb.getTasaAlumbrado()));
            }
        }
    }
}
