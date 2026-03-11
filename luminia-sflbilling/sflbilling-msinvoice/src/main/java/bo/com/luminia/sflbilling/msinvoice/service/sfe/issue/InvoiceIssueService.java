package bo.com.luminia.sflbilling.msinvoice.service.sfe.issue;

import bo.com.luminia.sflbilling.msinvoice.repository.*;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec.*;
import bo.com.luminia.sflbilling.domain.*;
import bo.com.luminia.sflbilling.domain.enumeration.InvoiceStatusEnum;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import bo.com.luminia.sflbilling.msinvoice.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msinvoice.repository.*;
import bo.com.luminia.sflbilling.msinvoice.service.constants.SiatBroadcastType;
import bo.com.luminia.sflbilling.msinvoice.service.constants.SiatInvoiceType;
import bo.com.luminia.sflbilling.msinvoice.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msinvoice.service.dto.siat.ApprovedProductFullDto;
import bo.com.luminia.sflbilling.msinvoice.service.sfe.signature.SignatureService;
import bo.com.luminia.sflbilling.msinvoice.service.utils.FileCompress;
import bo.com.luminia.sflbilling.msinvoice.service.utils.FileHash;
import bo.com.luminia.sflbilling.msinvoice.service.utils.NotificationService;
import bo.com.luminia.sflbilling.msinvoice.service.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec.*;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.InvoiceUniqueCodeGeneratorCufReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.InvoiceUniqueCodeGeneratorCufRes;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.CheckNitReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceIssueManualReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceIssueReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.response.sfe.invoice.InvoiceIssueRes;
import bo.gob.impuestos.sfe.code.RespuestaVerificarNit;
import bo.gob.impuestos.sfe.code.ServicioFacturacionCodigos;
import bo.gob.impuestos.sfe.code.SolicitudVerificarNit;
import bo.gob.impuestos.sfe.electronicinvoice.RespuestaRecepcion;
import bo.gob.impuestos.sfe.electronicinvoice.ServicioFacturacion;
import bo.gob.impuestos.sfe.electronicinvoice.SolicitudRecepcionFactura;
import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlSectorType;
import bo.gob.impuestos.sfe.invoice.xml.cmp.*;
import bo.gob.impuestos.sfe.invoice.xml.detail.*;
import bo.gob.impuestos.sfe.invoice.xml.eltr.*;
import bo.gob.impuestos.sfe.invoice.xml.header.*;
import bo.gob.impuestos.sfe.synchronization.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class InvoiceIssueService {
    private final InvoiceRequestRepository invoiceRequestRepository;
    private final BranchOfficeRepository branchOfficeRepository;

    private final static Integer IDENTITY_DOCUMENT_TYPE_NIT = 5;
    private final static Integer CODE_EXCEPTION = 0;
    private final static String NIT_CI_99001 = "99001";
    private final static String NIT_CI_99002 = "99002";
    private final static String NIT_CI_99003 = "99003";
    private final static String BUSINESS_NAME_99002 = "CONTROL TRIBUTARIO";
    private final static String BUSINESS_NAME_99003 = "VENTAS MENORES DEL DIA";
    private final static Integer IDENTITY_DOCUMENT_CI = 1;

    private final Environment environment;
    private final InvoiceSoapUtil invoiceSoapUtil;
    private final CompanyRepository companyRepository;
    private final CuisRepository cuisRepository;
    private final CufdRepository cufdRepository;
    private final ApprovedProductRepository approvedProductRepository;
    private final InvoiceRepository invoiceRepository;
    private final SectorDocumentTypeRepository sectorDocumentTypeRepository;
    private final BroadcastTypeRepository broadcastTypeRepository;
    private final InvoiceTypeRepository invoiceTypeRepository;
    private final OfflineRepository offlineRepository;
    private final EventRepository eventRepository;

    private final SignatureService signatureService;
    private final ConnectivityService connectivityService;
    private final ValidIdentityDocumentTypeRepository validIdentityDocumentTypeRepository;
    private final NotificationService notificationService;


    /**
     * Método que realiza la emisión de la factura.
     *
     * @param request Solicitud.
     * @param invoice Factura.
     * @return
     */
    public InvoiceIssueRes emit(InvoiceIssueReq request, XmlBaseInvoice invoice, Long recordId, StopWatch watch) {
        watch.start("Data validation: 0-5");
        InvoiceRequest record = invoiceRequestRepository.getById(recordId);

        // 0. Verifica si se usa el tipo de moneda.
        if (null != request.getUseCurrencyType() && request.getUseCurrencyType()) {
            log.debug("0. Ingresando a cálculo de tipo de cambio");
            this.convertToCurrencyType(invoice, request);
        } else {
            log.debug("0. Saltando cálculo de tipo de cambio");
        }

        /* ya hace esta verificacion arriba*/
        // 1. Obtiene datos de la empresa.
        Optional<Company> companyFromDb = companyRepository.findCompanyByIdAndActiveIsTrue(new Long(request.getCompanyId()));
        Company company = null;
        if (companyFromDb.isPresent()) {
            company = companyFromDb.get();
            log.debug("1. Recupera datos de la empresa : {}", company.getBusinessName());
        }

        if (!company.getActive()) {
            log.error("La empresa está inactiva : {}", company.getBusinessName());
            throw new SiatException(
                ResponseMessages.ERROR_COMPANY_NOT_ENABLED,
                ErrorEntities.INVOICE,
                ErrorKeys.ERR_VALIDATIONS);
        }


        boolean skipCheckExtraEvents = checkSkipExecuteExtraEvents();
        if (!connectivityService.isSiatOffline()) {
            if (!skipCheckExtraEvents) {
                // 2. Verifica la comunicacion con los servicios del SIAT.
                int check = checkConnectivity(company.getToken());
                log.debug("2. Conexión verificada : {}", check);
                if (check != SiatResponseCodes.SIAT_HAS_CONNECTIVITY) {
                    throw new SiatException(
                        String.format(ResponseMessages.ERROR_NO_SIAT_CONNECTION, SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT),
                        ErrorEntities.INVOICE,
                        ErrorKeys.ERR_SIAT_NO_CONNECTION);
                }
            } else {
                log.debug("2. Saltando verificación conexión");
            }
        }

        // 3. Obtiene el CUIS activo.
        Optional<Cuis> cuisOptional = null;
        try {
            cuisOptional = this.obtainCuisActive(request);
        } catch (CuisNotFoundException e) {
            throw new CuisNotFoundException();
        }
        Cuis cuisFromDb = cuisOptional.get();
        log.debug("3. Datos del código CUIS : {}", cuisFromDb);

        // 4. Obtiene el CUFD activo.
        Optional<Cufd> cufdOptional = null;
        try {
            cufdOptional = this.obtainCufdActive(request);
        } catch (CufdNotFoundException e) {
            throw new CufdNotFoundException();
        }
        Cufd cufdFromDb = cufdOptional.get();
        log.debug("4. Datos del código CUFD : {}", cufdFromDb);

        // 5. Obtiene fecha y hora actual del SIAT.
        ZonedDateTime dateTimeFromSiat = null;
        if (!skipCheckExtraEvents) {
            try {
                dateTimeFromSiat = this.obtainDateTimeFromSiat(company, cuisFromDb, request);
            } catch (DatetimeNotSync datetimeNotSync) {
                throw new DatetimeNotSync();
            }
            log.debug("5. Fecha y hora actual del SIAT: {}", dateTimeFromSiat);
        } else {
            dateTimeFromSiat = ZonedDateTime.now();
            log.debug("5. Fecha y hora actual del servidor: {}", dateTimeFromSiat);
        }

        if (connectivityService.isSiatOffline()) {
            if (!this.canIssueInvoice(request.getBranchOfficeSiat(), request.getPointSaleSiat(), company.getId(), dateTimeFromSiat, cufdFromDb)) {
                throw new SiatException("La validez del Código Único de Facturación (CUFD) ha caducado",
                    ErrorEntities.INVOICE, ErrorKeys.ERR_SIAT_EXCEPTION);
            }
        }

        watch.stop(); //0-5
        //MODIFICACION - ABEL
        log.debug("TIEMPO 0-5 (Milisegundos): {}", watch.getTotalTimeMillis());
        //

        watch.start("Generate cuf: 6");

        // 6. Generación del código CUF.
        String cuf = this.generateCuf(invoice, company, request, dateTimeFromSiat, cufdFromDb, false);
        log.debug("6. Generación del codigo CUF: {}", cuf);

        record.setCuf(cuf);
        invoiceRequestRepository.saveAndFlush(record);

        //stop to cron part 2 of emmit invoice
        watch.stop();//6

        //MODIFICACION - ABEL
        log.debug("TIEMPO 6 (Milisegundos): {}", watch.getTotalTimeMillis());
        //

        watch.start("Validating invoice: 7-11");

        InvoiceIssueRes invoiceIssueRes = this.preValidateInvoice(invoice);
        if (!invoiceIssueRes.getCode().equals(SiatResponseCodes.SUCCESS))
            return invoiceIssueRes;
        log.debug("7. Prevalidate invoice");

        // 8. Genera y valida la factura XML.
        invoiceIssueRes = this.populateInvoice(invoice, request, company, cuf, cufdFromDb, cuisFromDb, dateTimeFromSiat);
        if (!invoiceIssueRes.getCode().equals(SiatResponseCodes.SUCCESS))
            return invoiceIssueRes;
        invoice = (XmlBaseInvoice) invoiceIssueRes.getBody();
        log.debug("8. Factura con datos completos");

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

                // 9. Firma el documento XML generado.
                if (company.getModalitySiat().equals(ModalitySiatEnum.ELECTRONIC_BILLING)) {
                    xmlInvoice = signatureService.singXml(xmlInvoice, company.getId());
                    log.debug("9. Factura XML firmada");
                }

                // 10. Empaqueta el documento XML firmado en Gzip.
                byte[] gzipXml = FileCompress.textToGzip(xmlInvoice);
                log.debug("10. Documento compreso de la factura XML firmada");

                // 11. Obtiene el hash del documento compreso.
                String hashXml = FileHash.sha256(gzipXml);
                log.debug("11. Hash del documento compreso: {}", hashXml);


                watch.stop();//7-11

                //MODIFICACION - ABEL
                log.debug("TIEMPO 7-11 (Milisegundos): {}", watch.getTotalTimeMillis());
                //

                String receptionCode = null;
                if (this.getSiatBroadcastType(company) == SiatBroadcastType.BROADCAST_TYPE_ONLINE) {
                    watch.start("Sending invoice to SIAT: 12");

                    log.debug("12. preparando envío al siat");
                    invoiceIssueRes = this.sendInvoiceSiat(company, request, cuisFromDb, cufdFromDb, gzipXml, hashXml, dateTimeFromSiat);
                    if (invoiceIssueRes.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT)) {
                        throw new SiatException(invoiceIssueRes.getMessage() + " Codigo Error:" + invoiceIssueRes.getCode(),
                            ErrorEntities.INVOICE,
                            ErrorKeys.ERR_SIAT_EXCEPTION);
                    }

                    receptionCode = invoiceIssueRes.getBody().toString();

                    //MODIFICACION - ABEL
                    log.debug("TIEMPO 12 (Running - Milisegundos): {}", watch.getTotalTimeMillis());
                    //

                    watch.stop();//12

                    //MODIFICACION - ABEL
                    log.debug("TIEMPO 12 (Milisegundos): {}", watch.getTotalTimeMillis());
                    int timeoutSend = Integer.parseInt(environment.getProperty(ApplicationProperties.INVOICE_TIME_SEND_SIAT));
                    if (watch.getTotalTimeMillis() > timeoutSend){
                        log.debug("ERROR DE TIMEOUT !!!!");
                       // throw new DefaultTransactionException("", ErrorEntities.INVOICE);
                       // throw new SiatException("Tiempo de espera Superado");
                    }
                    //
                }

                watch.start("Register to BD: 13");

                log.debug("13. Preparando guardado de factura en BD");
                Invoice entity = new Invoice();
                entity.setInvoiceNumber(((XmlBaseHeaderInvoice) invoice.getCabecera()).getNumeroFactura());
                entity.setCuf(cuf);
                entity.setBroadcastDate(dateTimeFromSiat);
                entity.setInvoiceXml(xmlInvoice);
                entity.setInvoiceHash(hashXml);
                entity.setInvoiceJson(jsonInvoice);
                entity.setNit(((XmlBaseHeaderInvoice) invoice.getCabecera()).getNumeroDocumento());
                entity.setBusinessName(((XmlBaseHeaderInvoice) invoice.getCabecera()).getNombreRazonSocial());
                entity.setReceptionCode(receptionCode);
                entity.setStatus(this.getSiatBroadcastType(company) == SiatBroadcastType.BROADCAST_TYPE_ONLINE ? InvoiceStatusEnum.EMITTED : InvoiceStatusEnum.PENDING);
                entity.setModalitySiat(company.getModalitySiat());
                entity.setCufd(cufdFromDb);
                entity.setSectorDocumentType(sectorDocumentTypeRepository.getBySiatIdAndCompanyIdAndActiveIsTrue(request.getDocumentSectorType(), company.getId().intValue()));
                entity.setBroadcastType(broadcastTypeRepository.getBySiatIdAndCompanyIdAndActiveIsTrue(this.getSiatBroadcastType(company), company.getId().intValue()));
                entity.setInvoiceType(invoiceTypeRepository.getBySiatIdAndCompanyIdAndActiveIsTrue(SiatInvoiceType.map.get(request.getDocumentSectorType()), company.getId().intValue()));
                entity = invoiceRepository.save(entity);

                record.setInvoice(entity);
                invoiceRequestRepository.saveAndFlush(record);

                log.debug("13. Factura guardada en BD");
                watch.stop(); //13

                //MODIFICACION - ABEL
                log.debug("TIEMPO 13 (Milisegundos): {}", watch.getTotalTimeMillis());
                //

                // 14. Preparando envío de correo.
                if (request.getEmail() != null && !request.getEmail().equals("")) {
                    watch.start("Send invoice: 14");

                    log.debug("14. Preparando envío de email");
                    String currentToken = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
                    this.sendInvoiceEmail(entity.getCuf(), request.getEmail(), currentToken);
                    log.debug("14. Email enviado");

                    watch.stop(); //14

                    //MODIFICACION - ABEL
                    log.debug("TIEMPO 14 (Milisegundos): {}", watch.getTotalTimeMillis());
                    //
                }

                if (!connectivityService.isSiatOffline()) {
                    if (!"0".equals(entity.getNit()) &&
                        !NIT_CI_99001.equals(entity.getNit()) &&
                        !NIT_CI_99002.equals(entity.getNit()) &&
                        !NIT_CI_99003.equals(entity.getNit())
                    ) {
                        if (0 == ((XmlBaseHeaderInvoice) invoice.getCabecera()).getCodigoExcepcion()) {
                            watch.start(" Registrando documento en BD: 15");
                            log.debug("15. Registrando documento en BD");
                            Optional<ValidIdentityDocumentType> document = validIdentityDocumentTypeRepository.findBySiatCodeAndDocument(
                                ((XmlBaseHeaderInvoice) invoice.getCabecera()).getCodigoTipoDocumentoIdentidad().byteValue(),
                                entity.getNit()
                            );
                            if (!document.isPresent()) {
                                log.debug("15. document no presente, guardando");
                                ValidIdentityDocumentType doc = new ValidIdentityDocumentType();
                                doc.setSiatCode(((XmlBaseHeaderInvoice) invoice.getCabecera()).getCodigoTipoDocumentoIdentidad().byteValue());
                                doc.setDocument(entity.getNit());
                                doc.setComplement(((XmlBaseHeaderInvoice) invoice.getCabecera()).getComplemento());
                                doc.setSocialReason(((XmlBaseHeaderInvoice) invoice.getCabecera()).getNombreRazonSocial());
                                validIdentityDocumentTypeRepository.save(doc);
                                log.debug("15. documento guardado");
                            }
                            watch.stop(); //15

                            //MODIFICACION - ABEL
                            log.debug("TIEMPO 15 (Milisegundos): {}", watch.getTotalTimeMillis());
                            //
                        }
                    }
                }

                log.debug("16. Factura emitida correctamente");
                HashMap<String, Object> body = new HashMap<>();
                body.put("id", entity.getId());
                body.put("cuf", entity.getCuf());

                writeLogResume(watch, request, entity, "");
                return new InvoiceIssueRes(SiatResponseCodes.SUCCESS, "La factura ha sido emitida correctamente", body);

            } else {
                notificationService.notifyErrorInvoiceValidation(request, "invalid xml");
                writeLogResume(watch, request, null, "invalid xml");
                throw new SiatException(
                    ResponseMessages.ERROR_INVOICE_VALIDATION,
                    ErrorEntities.INVOICE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
            }
        } catch (InvoiceCannotBeProccesed e) {
            notificationService.notifyErrorInvoiceEmition(request, e.getMessage());
            log.error("InvoiceCannotBeProccesed, Error al emitir factura: {}", e.getMessage(), e);
            writeLogResume(watch, request, null, e.getMessage());
            throw new SiatException(
                e.getMessage(),
                ErrorEntities.INVOICE,
                ErrorKeys.ERR_SIAT_EXCEPTION);
        } catch (Exception e) {
            notificationService.notifyErrorInvoiceEmition(request, e.getMessage());
            log.error("Exception, Error al emitir factura: {}", e.getMessage(), e);
            writeLogResume(watch, request, null, e.getMessage());
            throw new SiatException(
                e.getMessage(),
                ErrorEntities.INVOICE,
                ErrorKeys.ERR_SIAT_EXCEPTION);
        }
    }

    /**
     * Write extra logs for timing the invoice
     *
     * @param watch    Springboot stopwatch
     * @param request  invoice request
     * @param entity   Invoice (if transaction is ok)
     * @param extraMsg (optional) extra message
     */
    private void writeLogResume(StopWatch watch, InvoiceIssueReq request, Invoice entity, String extraMsg) {
        if (watch.isRunning())
            watch.stop();

        boolean printCronDetail = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.INVOICE_PRINT_EMIT_CRON_DETAIL));

        StringBuilder builder = new StringBuilder();
        if (printCronDetail) {
            builder.append("\n");
            builder.append("company: ").append(request.getCompanyId()).append("\n");
            builder.append("branchOffice: ").append(request.getBranchOfficeSiat()).append("\n");
            if (entity != null) {
                builder.append("id: ").append(entity.getId()).append("\n");
                builder.append("cuf: ").append(entity.getCuf()).append("\n");
            }
            if (Strings.isBlank(extraMsg)) {
                builder.append("msg: ").append(extraMsg).append("\n");
            }
        }
        builder.append("EMIT_INVOICE|").append(watch.getTotalTimeMillis()).append("\n");
        if (printCronDetail)
            builder.append(watch.prettyPrint());

        log.debug(builder.toString());
    }


    /**
     * Método que realiza la emisión de la factura.
     *
     * @param request Solicitud.
     * @param invoice Factura.
     * @return
     */
    public InvoiceIssueRes emitManual(InvoiceIssueManualReq request, XmlBaseInvoice invoice, InvoiceRequest record, StopWatch watch) {
        watch.start("Cálculo de datos 0-4");

        // 0. Verifica si se usa el tipo de moneda.
        if (null != request.getUseCurrencyType() && request.getUseCurrencyType()) {
            this.convertToCurrencyType(invoice, request);
        }

        /* ya hace esta verificacion arriba*/
        // 1. Obtiene datos de la empresa.
        Optional<Company> companyFromDb = companyRepository.findCompanyByIdAndActiveIsTrue(new Long(request.getCompanyId()));
        Company company = null;
        if (companyFromDb.isPresent()) {
            company = companyFromDb.get();
            log.debug("1. Recupera datos de la empresa : {}", company.getBusinessName());
        }

        // 3. Obtiene el CUIS activo.
        Optional<Cuis> cuisOptional = null;
        try {
            cuisOptional = this.obtainCuisActive(request);
        } catch (CuisNotFoundException e) {
            throw new CuisNotFoundException();
        }
        Cuis cuisFromDb = cuisOptional.get();
        log.debug("3. Datos del código CUIS : {}", cuisFromDb);

        // 4. Obtiene el CUFD activo.
        Optional<Cufd> cufdOptional = null;
        try {
            cufdOptional = this.obtainCufdActive(request);
        } catch (CufdNotFoundException e) {
            throw new CufdNotFoundException();
        }
        Cufd cufdFromDb = cufdOptional.get();
        log.debug("4. Datos del código CUFD : {}", cufdFromDb);

        watch.stop();

        watch.start("Cálculo de datos 5");
        // 5. Obtiene fecha y hora actual.
        ZonedDateTime dateTimeFromSiat = ZonedDateTime.now();
        if (!this.canIssueInvoice(request.getBranchOfficeSiat(), request.getPointSaleSiat(), company.getId(), dateTimeFromSiat, cufdFromDb)) {
            watch.stop();
            throw new SiatException("La validez del Código Único de Facturación (CUFD) ha caducado",
                ErrorEntities.INVOICE, ErrorKeys.ERR_SIAT_EXCEPTION);
        }
        watch.stop();

        watch.start("Generacion cuf 5");

        // 6. Generación del código CUF.
        String cuf = this.generateCuf(invoice, company, request, dateTimeFromSiat, cufdFromDb, true);
        log.debug("6. Generación del codigo CUF: {}", cuf);

        record.setCuf(cuf);
        invoiceRequestRepository.saveAndFlush(record);

        watch.stop();

        watch.start("Prevalidando factura 7");
        InvoiceIssueRes invoiceIssueRes = this.preValidateInvoice(invoice);
        watch.stop();

        if (!invoiceIssueRes.getCode().equals(SiatResponseCodes.SUCCESS))
            return invoiceIssueRes;

        watch.start("Computo XML 8");

        // 8. Genera y valida la factura XML.
        invoiceIssueRes = this.populateInvoice(invoice, request, company, cuf, cufdFromDb, cuisFromDb, dateTimeFromSiat);
        if (!invoiceIssueRes.getCode().equals(SiatResponseCodes.SUCCESS))
            return invoiceIssueRes;
        invoice = (XmlBaseInvoice) invoiceIssueRes.getBody();
        log.debug("8. Factura con datos completos");

        watch.stop();

        try {

            watch.start("Generar xml");
            String xmlInvoice = invoice.generate(true);
            watch.stop();

            if (invoice.isValidXml(xmlInvoice)) {

                watch.start("Prepara facturas 9-12");

                invoiceIssueRes = this.validateInvoice(invoice);
                if (!invoiceIssueRes.getCode().equals(SiatResponseCodes.SUCCESS))
                    return invoiceIssueRes;

                // Obtiene la factura en formato JSON.
                ObjectMapper mapper = new ObjectMapper();
                mapper.setTimeZone(TimeZone.getTimeZone("America/La_Paz"));
                String jsonInvoice = mapper.writeValueAsString(invoice);

                // 9. Firma el documento XML generado.
                if (company.getModalitySiat().equals(ModalitySiatEnum.ELECTRONIC_BILLING)) {
                    xmlInvoice = signatureService.singXml(xmlInvoice, company.getId());
                    log.debug("9. Factura XML firmada");
                }

                // 10. Empaqueta el documento XML firmado en Gzip.
                byte[] gzipXml = FileCompress.textToGzip(xmlInvoice);
                log.debug("10. Documento compreso de la factura XML firmada");

                // 11. Obtiene el hash del documento compreso.
                String hashXml = FileHash.sha256(gzipXml);
                log.debug("11. Hash del documento compreso: {}", hashXml);

                watch.stop();

                watch.start("Registro factura en BD 12");

                // 12. Registro de la factura en base de datos.
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
                entity.setBroadcastType(broadcastTypeRepository.getBySiatIdAndCompanyIdAndActiveIsTrue(SiatBroadcastType.BROADCAST_TYPE_OFFLINE, company.getId().intValue()));
                entity.setInvoiceType(invoiceTypeRepository.getBySiatIdAndCompanyIdAndActiveIsTrue(SiatInvoiceType.map.get(request.getDocumentSectorType()), company.getId().intValue()));
                entity.setCafc(request.getCafc());
                entity = invoiceRepository.save(entity);

                record.setInvoice(entity);
                invoiceRequestRepository.saveAndFlush(record);


                if (request.getEmail() != null && !request.getEmail().equals("")) {
                    watch.start("Envío de correo");
                    String currentToken = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
                    this.sendInvoiceEmail(entity.getCuf(), request.getEmail(), currentToken);
                    watch.stop();
                }

                log.debug("13. Factura emitida correctamente");

                HashMap<String, Object> body = new HashMap<>();
                body.put("id", entity.getId());
                body.put("cuf", entity.getCuf());

                return new InvoiceIssueRes(SiatResponseCodes.SUCCESS, "La factura manual ha sido registrada correctamente", body);
            } else {
                notificationService.notifyErrorInvoiceValidation(request, "Error de validaciones de factura");
                throw new SiatException(
                    ResponseMessages.ERROR_INVOICE_VALIDATION,
                    ErrorEntities.INVOICE,
                    ErrorKeys.ERR_SIAT_EXCEPTION);
            }
        } catch (InvoiceCannotBeProccesed e) {
            notificationService.notifyErrorInvoiceEmition(request, e.getMessage());
            e.printStackTrace();
            throw new SiatException(
                ResponseMessages.ERROR_INVOICE_GENERATION,
                ErrorEntities.INVOICE,
                ErrorKeys.ERR_SIAT_EXCEPTION);
        } catch (Exception e) {
            notificationService.notifyErrorInvoiceEmition(request, e.getMessage());
            throw new SiatException(
                ResponseMessages.ERROR_INVOICE_GENERATION,
                ErrorEntities.INVOICE,
                ErrorKeys.ERR_SIAT_EXCEPTION);
        }
    }

    /**
     * Método que realiza la verificación del Nit.
     *
     * @param company Empresa.
     * @param request Datos de la solicitud.
     * @return
     */
    public InvoiceIssueRes checkNit(Company company, CheckNitReq request) {

        /*
        if (!this.isSiatOffline()) {
            // 1. Verifica la comunicacion con los servicios del SIAT.
            int check = checkConnectivity(company.getToken());
            log.debug("1. Conexión verificada : {}", check);
            if (check != SiatResponseCodes.SIAT_HAS_CONNECTIVITY) {
                throw new SiatException(
                    String.format(ResponseMessages.ERROR_NO_SIAT_CONNECTION, SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT),
                    ErrorEntities.INVOICE,
                    ErrorKeys.ERR_SIAT_NO_CONNECTION);
            }
        }*/

        // 1. Verifica la comunicacion con los servicios del SIAT.
        if (connectivityService.isSiatOffline()) {
            log.debug("1. Verificación nit fallida, sistema en modo offline: {}", request.getNit());
            throw new SiatException(
                String.format(ResponseMessages.ERROR_NO_SIAT_CONNECTION, SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT),
                ErrorEntities.INVOICE,
                ErrorKeys.ERR_SIAT_NO_CONNECTION);
        } else {
            log.debug("1. Verificación nit en proceso: {}", request.getNit());
        }

        // 2. Obtiene el CUIS activo.
        Optional<Cuis> cuisOptional = null;
        try {
            cuisOptional = this.obtainCodeCuisActive(request);
        } catch (CuisNotFoundException e) {
            throw new CuisNotFoundException();
        }
        Cuis cuisFromDb = cuisOptional.get();
        log.debug("2. Datos del código CUIS : {}", cuisFromDb);

        InvoiceIssueRes invoiceIssueRes = this.verifyNit(company, request.getNit(), request.getBranchOfficeSiat(), cuisFromDb);
        invoiceIssueRes.setMessage("El Nit es válido");
        return invoiceIssueRes;
    }

    /**
     * Método que envia la factura al servicio del SIAT.
     *
     * @param company          Empresa.
     * @param request          Solicitud.
     * @param cuisFromDb       Código Único del Sistema.
     * @param cufdFromDb       Código Único diario.
     * @param gzipXml          Factura empaquetada en formato Gzip.
     * @param hashXml          Hash del documento.
     * @param dateTimeFromSiat Fecha y hora actual.
     * @return
     * @throws Exception
     */
    private InvoiceIssueRes sendInvoiceSiat(Company company, InvoiceIssueReq request, Cuis cuisFromDb, Cufd cufdFromDb,
                                            byte[] gzipXml, String hashXml, ZonedDateTime dateTimeFromSiat) throws Exception {
        InvoiceIssueRes result = new InvoiceIssueRes();
        String receptionCode = "";

        //Obs. Impuestos: la fecha de envío debe ser antes del envío, para que no tome en cuenta tiempos de
        //generación de xml, compresión u otros
        dateTimeFromSiat = ZonedDateTime.now();

        String strDateTimeFromSiat = convertZoneTimedateToString(dateTimeFromSiat);
        StopWatch cron = new StopWatch("Envio factura a SIAT");

        // Identifica el tipo de factura para la recepción.
        if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_COMPRA_VENTA)) {
            log.debug("INVOICE_COMPRA_VENTA");
            bo.gob.impuestos.sfe.buysellinvoice.SolicitudRecepcionFactura invoiceRequest = new bo.gob.impuestos.sfe.buysellinvoice.SolicitudRecepcionFactura();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(request.getPointSaleSiat());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(request.getBranchOfficeSiat());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(request.getDocumentSectorType());
            invoiceRequest.setCodigoEmision(this.getSiatBroadcastType(company));
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(request.getDocumentSectorType()));
            invoiceRequest.setArchivo(gzipXml);
            //XMLGregorianCalendar dateTimeSiat = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(dateTimeFromSiat));
            invoiceRequest.setFechaEnvio(strDateTimeFromSiat);
            invoiceRequest.setHashArchivo(hashXml);

            bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion service = invoiceSoapUtil.getCompraVentaService(company.getToken());

            cron.start();
            bo.gob.impuestos.sfe.buysellinvoice.RespuestaRecepcion response;
            try {
                response = service.recepcionFactura(invoiceRequest);
            } catch (Exception e) {
                cron.stop();
                log.debug("ONLY_INVOICE|{}|Error al enviar|{}", cron.getTotalTimeMillis(), e.getMessage());
                throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Error de conexión con el Siat");
            }
            cron.stop();
            log.debug("ONLY_INVOICE|{}", cron.getTotalTimeMillis());

            if (!response.isTransaccion()) {
                log.debug("Error al enviar factura, response siat: {}", response);
                if (!response.getMensajesList().isEmpty()) {
                    throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getMensajesList().get(0).getDescripcion());
                } else {
                    //caso especial: A veces impuestos no envía el descriptivo del error
                    if (Strings.isBlank(response.getCodigoDescripcion()) || "null".equals(response.getCodigoDescripcion())) {
                        throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Error sin detalle por parte del Siat");
                    } else {
                        throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getCodigoDescripcion());
                    }
                }
            }
            receptionCode = response.getCodigoRecepcion();
            log.debug("12. Resultado recepción de la factura: {}", response.getCodigoEstado());

        } else if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES) ||
            request.getDocumentSectorType().equals(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION) ||
            request.getDocumentSectorType().equals(XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE) ||
            request.getDocumentSectorType().equals(XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD) ||
            request.getDocumentSectorType().equals(XmlSectorType.INVOICE_SECTOR_EDUCATIVO) ||
            request.getDocumentSectorType().equals(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS) ||
            request.getDocumentSectorType().equals(XmlSectorType.INVOICE_FACTURAS_HOTELES) ||
            request.getDocumentSectorType().equals(XmlSectorType.INVOICE_PREVALORADA) ||
            request.getDocumentSectorType().equals(XmlSectorType.INVOICE_HOSPITAL_CLINICAS) ||
            request.getDocumentSectorType().equals(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS)) {
            log.debug("TO_INVOICE: {}", request.getDocumentSectorType());

            if (company.getModalitySiat().equals(ModalitySiatEnum.ELECTRONIC_BILLING)) {
                log.debug("Invoice type: " + request.getDocumentSectorType() + " Electronic billing");
                SolicitudRecepcionFactura invoiceRequest = new SolicitudRecepcionFactura();
                invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
                invoiceRequest.setCodigoPuntoVenta(request.getPointSaleSiat());
                invoiceRequest.setCodigoSistema(company.getSystemCode());
                invoiceRequest.setCodigoSucursal(request.getBranchOfficeSiat());
                invoiceRequest.setNit(company.getNit());
                invoiceRequest.setCodigoDocumentoSector(request.getDocumentSectorType());
                invoiceRequest.setCodigoEmision(this.getSiatBroadcastType(company));
                invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
                invoiceRequest.setCufd(cufdFromDb.getCufd());
                invoiceRequest.setCuis(cuisFromDb.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(request.getDocumentSectorType()));
                invoiceRequest.setArchivo(gzipXml);
                //XMLGregorianCalendar dateTimeSiat = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(dateTimeFromSiat));
                invoiceRequest.setFechaEnvio(strDateTimeFromSiat);
                invoiceRequest.setHashArchivo(hashXml);

                ServicioFacturacion service = invoiceSoapUtil.getElectronicaInvoice(company.getToken());

                cron.start();
                RespuestaRecepcion response;
                try {
                    response = service.recepcionFactura(invoiceRequest);
                } catch (Exception e) {
                    cron.stop();
                    log.debug("ONLY_INVOICE|{}|Error al enviar|{}", cron.getTotalTimeMillis(), e.getMessage());
                    throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Error de conexión con el Siat");
                }
                cron.stop();
                log.debug("ONLY_INVOICE|{}", cron.getTotalTimeMillis());

                if (!response.isTransaccion()) {
                    log.debug("Error al enviar factura, response siat: {}", response);
                    if (!response.getMensajesList().isEmpty()) {
                        throw new InvoiceCannotBeProccesed(
                            SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getMensajesList().get(0).getDescripcion());
                    } else {
                        if (Strings.isBlank(response.getCodigoDescripcion()) || "null".equals(response.getCodigoDescripcion())) {
                            throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Error sin detalle por parte del Siat");
                        } else {
                            throw new InvoiceCannotBeProccesed(
                                SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getCodigoDescripcion());
                        }
                    }
                }
                receptionCode = response.getCodigoRecepcion();
                log.debug("12. Resultado recepción de la factura: {}", response.getCodigoEstado());
            } else {
                log.debug("Invoice type: " + request.getDocumentSectorType() + " Computarized");

                bo.gob.impuestos.sfe.computerizedinvoice.SolicitudRecepcionFactura invoiceRequest = new bo.gob.impuestos.sfe.computerizedinvoice.SolicitudRecepcionFactura();
                invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
                invoiceRequest.setCodigoPuntoVenta(request.getPointSaleSiat());
                invoiceRequest.setCodigoSistema(company.getSystemCode());
                invoiceRequest.setCodigoSucursal(request.getBranchOfficeSiat());
                invoiceRequest.setNit(company.getNit());
                invoiceRequest.setCodigoDocumentoSector(request.getDocumentSectorType());
                invoiceRequest.setCodigoEmision(this.getSiatBroadcastType(company));
                invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
                invoiceRequest.setCufd(cufdFromDb.getCufd());
                invoiceRequest.setCuis(cuisFromDb.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(request.getDocumentSectorType()));
                invoiceRequest.setArchivo(gzipXml);
                //XMLGregorianCalendar dateTimeSiat = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(dateTimeFromSiat));
                invoiceRequest.setFechaEnvio(strDateTimeFromSiat);
                invoiceRequest.setHashArchivo(hashXml);

                bo.gob.impuestos.sfe.computerizedinvoice.ServicioFacturacion service = invoiceSoapUtil.getComputarizadaInvoice(company.getToken());

                cron.start();
                bo.gob.impuestos.sfe.computerizedinvoice.RespuestaRecepcion response;
                try {
                    response = service.recepcionFactura(invoiceRequest);
                } catch (Exception e) {
                    cron.stop();
                    log.debug("ONLY_INVOICE|{}|Error al enviar|{}", cron.getTotalTimeMillis(), e.getMessage());
                    throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Error de conexión con el Siat");
                }
                cron.stop();
                log.debug("ONLY_INVOICE|{}", cron.getTotalTimeMillis());

                if (!response.isTransaccion()) {
                    log.debug("Error al enviar factura, response siat: {}", response);
                    if (!response.getMensajesList().isEmpty()) {
                        throw new InvoiceCannotBeProccesed(
                            SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getMensajesList().get(0).getDescripcion());
                    } else {
                        if (Strings.isBlank(response.getCodigoDescripcion()) || "null".equals(response.getCodigoDescripcion())) {
                            throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Error sin detalle por parte del Siat");
                        } else {
                            throw new InvoiceCannotBeProccesed(
                                SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getCodigoDescripcion());
                        }
                    }
                }
                receptionCode = response.getCodigoRecepcion();
                log.debug("12. Resultado recepción de la factura: {}", response.getCodigoEstado());
            }
        }

        if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_SERVICIOS_BASICOS)) {
            log.debug("INVOICE_SERVICIOS_BASICOS");
            bo.gob.impuestos.sfe.basicserviceinvoice.SolicitudRecepcionFactura invoiceRequest = new bo.gob.impuestos.sfe.basicserviceinvoice.SolicitudRecepcionFactura();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(request.getPointSaleSiat());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(request.getBranchOfficeSiat());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(request.getDocumentSectorType());
            invoiceRequest.setCodigoEmision(this.getSiatBroadcastType(company));
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(request.getDocumentSectorType()));
            invoiceRequest.setArchivo(gzipXml);
            //XMLGregorianCalendar dateTimeSiat = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(dateTimeFromSiat));
            invoiceRequest.setFechaEnvio(strDateTimeFromSiat);
            invoiceRequest.setHashArchivo(hashXml);

            bo.gob.impuestos.sfe.basicserviceinvoice.ServicioFacturacion service = invoiceSoapUtil.getServiciosBasicos(company.getToken());

            cron.start();
            bo.gob.impuestos.sfe.basicserviceinvoice.RespuestaRecepcion response;
            try {
                response = service.recepcionFactura(invoiceRequest);
            } catch (Exception e) {
                cron.stop();
                log.debug("ONLY_INVOICE|{}|Error al enviar|{}", cron.getTotalTimeMillis(), e.getMessage());
                throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Error de conexión con el Siat");
            }
            cron.stop();
            log.debug("ONLY_INVOICE|{}", cron.getTotalTimeMillis());

            if (!response.isTransaccion()) {
                log.debug("Error al enviar factura, response siat: {}", response);
                if (!response.getMensajesList().isEmpty()) {
                    throw new InvoiceCannotBeProccesed(
                        SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getMensajesList().get(0).getDescripcion());
                } else {
                    if (Strings.isBlank(response.getCodigoDescripcion()) || "null".equals(response.getCodigoDescripcion())) {
                        throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Error sin detalle por parte del Siat");
                    } else {
                        throw new InvoiceCannotBeProccesed(
                            SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getCodigoDescripcion());
                    }
                }
            }
            receptionCode = response.getCodigoRecepcion();
            log.debug("12. Resultado recepción de la factura: {}", response.getCodigoEstado());
        }

        if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS)) {
            log.debug("INVOICE_ENTIDADES_FINANCIERAS");
            bo.gob.impuestos.sfe.financialinvoice.SolicitudRecepcionFactura invoiceRequest = new bo.gob.impuestos.sfe.financialinvoice.SolicitudRecepcionFactura();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(request.getPointSaleSiat());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(request.getBranchOfficeSiat());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(request.getDocumentSectorType());
            invoiceRequest.setCodigoEmision(this.getSiatBroadcastType(company));
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(request.getDocumentSectorType()));
            invoiceRequest.setArchivo(gzipXml);
            //XMLGregorianCalendar dateTimeSiat = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(dateTimeFromSiat));
            invoiceRequest.setFechaEnvio(strDateTimeFromSiat);
            invoiceRequest.setHashArchivo(hashXml);

            bo.gob.impuestos.sfe.financialinvoice.ServicioFacturacion service = invoiceSoapUtil.getEntidadesFinancieras(company.getToken());

            cron.start();
            bo.gob.impuestos.sfe.financialinvoice.RespuestaRecepcion response;
            try {
                response = service.recepcionFactura(invoiceRequest);
            } catch (Exception e) {
                cron.stop();
                log.debug("ONLY_INVOICE|{}|Error al enviar|{}", cron.getTotalTimeMillis(), e.getMessage());
                throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Error de conexión con el Siat");
            }
            cron.stop();
            log.debug("ONLY_INVOICE|{}", cron.getTotalTimeMillis());

            if (!response.isTransaccion()) {
                log.debug("Error al enviar factura, response siat: {}", response);
                if (!response.getMensajesList().isEmpty()) {
                    throw new InvoiceCannotBeProccesed(
                        SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getMensajesList().get(0).getDescripcion());
                } else {
                    if (Strings.isBlank(response.getCodigoDescripcion()) || "null".equals(response.getCodigoDescripcion())) {
                        throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Error sin detalle por parte del Siat");
                    } else {
                        throw new InvoiceCannotBeProccesed(
                            SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getCodigoDescripcion());
                    }
                }
            }
            receptionCode = response.getCodigoRecepcion();
            log.debug("12. Resultado recepción de la factura: {}", response.getCodigoEstado());
        }

        if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_TELECOMUNICACIONES)) {
            log.debug("INVOICE_TELECOMUNICACIONES");
            bo.gob.impuestos.sfe.telecommunicationinvoice.SolicitudRecepcionFactura invoiceRequest = new bo.gob.impuestos.sfe.telecommunicationinvoice.SolicitudRecepcionFactura();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(request.getPointSaleSiat());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(request.getBranchOfficeSiat());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(request.getDocumentSectorType());
            invoiceRequest.setCodigoEmision(this.getSiatBroadcastType(company));
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(request.getDocumentSectorType()));
            invoiceRequest.setArchivo(gzipXml);
            //XMLGregorianCalendar dateTimeSiat = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(dateTimeFromSiat));
            invoiceRequest.setFechaEnvio(strDateTimeFromSiat);
            invoiceRequest.setHashArchivo(hashXml);

            bo.gob.impuestos.sfe.telecommunicationinvoice.ServicioFacturacion service = invoiceSoapUtil.getTelecomunicacionesInvoice(company.getToken());

            cron.start();
            bo.gob.impuestos.sfe.telecommunicationinvoice.RespuestaRecepcion response;
            try {
                response = service.recepcionFactura(invoiceRequest);
            } catch (Exception e) {
                cron.stop();
                log.debug("ONLY_INVOICE|{}|Error al enviar|{}", cron.getTotalTimeMillis(), e.getMessage());
                throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Error de conexión con el Siat");
            }
            cron.stop();
            log.debug("ONLY_INVOICE|{}", cron.getTotalTimeMillis());

            if (!response.isTransaccion()) {
                log.debug("Error al enviar factura, response siat: {}", response);
                if (!response.getMensajesList().isEmpty()) {
                    throw new InvoiceCannotBeProccesed(
                        SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getMensajesList().get(0).getDescripcion());
                } else {
                    if (Strings.isBlank(response.getCodigoDescripcion()) || "null".equals(response.getCodigoDescripcion())) {
                        throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Error sin detalle por parte del Siat");
                    } else {
                        throw new InvoiceCannotBeProccesed(
                            SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getCodigoDescripcion());
                    }
                }
            }
            receptionCode = response.getCodigoRecepcion();
            log.debug("12. Resultado recepción de la factura: {}", response.getCodigoEstado());
        }

        if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_NOTA_CREDITO_DEBITO) ||
            request.getDocumentSectorType().equals(XmlSectorType.INVOICE_NOTA_CONCILIACION)
        ) {
            log.debug("Type:" + request.getDocumentSectorType() + "INVOICE_NOTA_CREDITO_DEBITO | INVOICE_NOTA_CONCILIACION");
            bo.gob.impuestos.sfe.creditdebitnoteinvoice.SolicitudRecepcionFactura invoiceRequest = new bo.gob.impuestos.sfe.creditdebitnoteinvoice.SolicitudRecepcionFactura();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(request.getPointSaleSiat());
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(request.getBranchOfficeSiat());
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(request.getDocumentSectorType());
            invoiceRequest.setCodigoEmision(this.getSiatBroadcastType(company));
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(request.getDocumentSectorType()));
            invoiceRequest.setArchivo(gzipXml);
//            XMLGregorianCalendar dateTimeSiat = DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar.from(dateTimeFromSiat));
            invoiceRequest.setFechaEnvio(strDateTimeFromSiat);
            invoiceRequest.setHashArchivo(hashXml);

            bo.gob.impuestos.sfe.creditdebitnoteinvoice.ServicioFacturacion service = invoiceSoapUtil.getCreditoDebitoService(company.getToken());

            cron.start();
            bo.gob.impuestos.sfe.creditdebitnoteinvoice.RespuestaRecepcion response;
            try {
                response = service.recepcionDocumentoAjuste(invoiceRequest);
            } catch (Exception e) {
                cron.stop();
                log.debug("ONLY_INVOICE|{}|Error al enviar|{}", cron.getTotalTimeMillis(), e.getMessage());
                throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Error de conexión con el Siat");
            }
            cron.stop();
            log.debug("ONLY_INVOICE|{}", cron.getTotalTimeMillis());

            if (!response.isTransaccion()) {
                log.debug("Error al enviar factura, response siat: {}", response);
                if (!response.getMensajesList().isEmpty()) {
                    throw new InvoiceCannotBeProccesed(
                        SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getMensajesList().get(0).getDescripcion());
                } else {
                    if (Strings.isBlank(response.getCodigoDescripcion()) || "null".equals(response.getCodigoDescripcion())) {
                        throw new InvoiceCannotBeProccesed(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Error sin detalle por parte del Siat");
                    } else {
                        throw new InvoiceCannotBeProccesed(
                            SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getCodigoDescripcion());
                    }
                }
            }
            receptionCode = response.getCodigoRecepcion();
            log.debug("12. Resultado recepción de la factura: {}", response.getCodigoEstado());
        }


        result.setCode(SiatResponseCodes.SUCCESS);
        result.setBody(receptionCode);
        return result;
    }


    /**
     * Método que verifica la conexión.
     *
     * @return
     */
    private int checkConnectivity(String token) {
        ServicioFacturacionSincronizacion serviceConectividad = invoiceSoapUtil.getSincronizacion(token);
        RespuestaComunicacion response = serviceConectividad.verificarComunicacion();

        if (response.getMensajesList().isEmpty())
            return 0; //no hay conectividad

        for (MensajeServicio ms : response.getMensajesList()) {
            return ms.getCodigo().intValue(); // es el codigo que devuelve el SIAT
        }

        return 0;
    }

    /**
     * Método que obtiene el código CUIS vigente.
     *
     * @param request
     * @return
     * @throws CuisNotFoundException
     */
    public Optional<Cuis> obtainCuisActive(InvoiceIssueReq request) throws CuisNotFoundException {
        Optional<Cuis> cuisOptional = cuisRepository.
            findByPointSaleSiatIdBranchOfficeSiatIdActive(request.getPointSaleSiat(), request.getBranchOfficeSiat(), new Long(request.getCompanyId()));

        if (!cuisOptional.isPresent()) {
            throw new CuisNotFoundException();
        }

        return cuisOptional;
    }

    /**
     * Método que obtiene el código CUIS vigente.
     *
     * @param request
     * @return
     * @throws CuisNotFoundException
     */
    public Optional<Cuis> obtainCodeCuisActive(CheckNitReq request) throws CuisNotFoundException {
        Optional<Cuis> cuisOptional = cuisRepository.
            findByPointSaleSiatIdBranchOfficeSiatIdActive(request.getPointSaleSiat(), request.getBranchOfficeSiat(), new Long(request.getCompanyId()));

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
    protected Optional<Cufd> obtainCufdActive(InvoiceIssueReq request) throws CufdNotFoundException {
        Optional<Cufd> cufdOptional = cufdRepository
            .findByPointSaleSiatIdBranchOfficeSiatIdActive(request.getPointSaleSiat(), request.getBranchOfficeSiat(), new Long(request.getCompanyId()));

        if (!cufdOptional.isPresent()) {
            throw new CufdNotFoundException();
        }

        return cufdOptional;
    }

    /**
     * Método que obtiene la hora actual del SIAT.
     *
     * @param company
     * @param cuis
     * @param invoiceIssueReq
     * @return
     * @throws DatetimeNotSync
     */
    public ZonedDateTime obtainDateTimeFromSiat(final Company company, final Cuis cuis, final InvoiceIssueReq invoiceIssueReq) throws DatetimeNotSync {

        // Retorna la hora actual del sistema.
        if (connectivityService.isSiatOffline()) {
            return ZonedDateTime.now();
        }

        SolicitudSincronizacion request = new SolicitudSincronizacion();
        request.setNit(company.getNit());
        request.setCodigoSistema(company.getSystemCode());
        request.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
        request.setCodigoPuntoVenta(new ObjectFactory().createSolicitudSincronizacionCodigoPuntoVenta(invoiceIssueReq.getPointSaleSiat()));
        request.setCodigoSucursal(invoiceIssueReq.getBranchOfficeSiat());
        request.setCuis(cuis.getCuis());

        ServicioFacturacionSincronizacion serviceFechaHora = invoiceSoapUtil.getSincronizacion(company.getToken());
        RespuestaFechaHora response = serviceFechaHora.sincronizarFechaHora(request);

        if (!response.isTransaccion())
            throw new DatetimeNotSync();

        return ZonedDateTime.parse(response.getFechaHora() + "-04:00[America/La_Paz]");
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
    private InvoiceIssueRes populateInvoice(XmlBaseInvoice invoice, InvoiceIssueReq request, Company company, String cuf, Cufd cufdFromDb, Cuis cuisFromDb, ZonedDateTime dateTimeFromSiat) {
        InvoiceIssueRes response = new InvoiceIssueRes();
        XmlBaseHeaderInvoice headerBase = (XmlBaseHeaderInvoice) invoice.getCabecera();
        headerBase.setBarcode(request.getBarcode());
        BranchOffice branchOffice = branchOfficeRepository.findByCompanyIdAndBranchOfficeSiatIdAndActiveIsTrue(company.getId(), request.getBranchOfficeSiat());

        if (request.getDocumentSectorType().equals(XmlSectorType.INVOICE_BOLETO_AEREO)) {

            // Completa los datos de la cabecera de la factura.
            XmlBaseHeaderInvoice header = (XmlBaseHeaderInvoice) invoice.getCabecera();
            header.setNitEmisor(company.getNit());
            header.setRazonSocialEmisor(company.getName());
            //header.setMunicipio(company.getCity());
            //header.setTelefono(company.getPhone());
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
            log.debug("7. Lista de productos homologados: {}", listProductFull.size());

            // Completa los datos de la cabecera de la factura.
            XmlBaseHeaderInvoice header = (XmlBaseHeaderInvoice) invoice.getCabecera();
            header.setNitEmisor(company.getNit());
            header.setRazonSocialEmisor(company.getName());
            if (branchOffice != null && !Strings.isBlank(branchOffice.getCity())) {
                header.setMunicipio(branchOffice.getCity());
            } else {
                header.setMunicipio(company.getCity());
            }
            if (branchOffice != null && !Strings.isBlank(branchOffice.getPhone())) {
                header.setTelefono(branchOffice.getPhone());
            } else {
                header.setTelefono(company.getPhone());
            }
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
                        if (!connectivityService.isSiatOffline()) {
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
                    throw new DefaultTransactionException(
                        String.format(ResponseMessages.ERROR_NOT_FOUND_HOMOLOGADO, detail.getCodigoProducto()),
                        ErrorEntities.INVOICE,
                        ErrorKeys.ERR_RECORD_NOT_FOUND);
                    //response.setMessage("El código de producto: " + detail.getCodigoProducto() + " no se encuentra homologado");
                    //response.setCode(SiatResponseCodes.ERROR_APPROVED_PRODUCT_NOT_FOUND);
                    //return response;
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
                    throw new DefaultTransactionException(
                        String.format(ResponseMessages.ERROR_NOT_FOUND_HOMOLOGADO, detail.getCodigoProducto()),
                        ErrorEntities.INVOICE,
                        ErrorKeys.ERR_RECORD_NOT_FOUND);

                    //response.setMessage("El código de producto: " + detail.getCodigoProducto() + " no se encuentra homologado");
                    //response.setCode(SiatResponseCodes.ERROR_APPROVED_PRODUCT_NOT_FOUND);
                    //return response;
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
            log.debug("7. Lista de productos homologados: {}", listProductFull.size());

            // Completa los datos de la cabecera de la factura.
            XmlBaseHeaderInvoice header = (XmlBaseHeaderInvoice) invoice.getCabecera();
            header.setNitEmisor(company.getNit());
            header.setRazonSocialEmisor(company.getName());
            if (branchOffice != null && !Strings.isBlank(branchOffice.getCity())) {
                header.setMunicipio(branchOffice.getCity());
            } else {
                header.setMunicipio(company.getCity());
            }
            if (branchOffice != null && !Strings.isBlank(branchOffice.getPhone())) {
                header.setTelefono(branchOffice.getPhone());
            } else {
                header.setTelefono(company.getPhone());
            }
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
                            if (!connectivityService.isSiatOffline()) {
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
                    throw new DefaultTransactionException(
                        String.format(ResponseMessages.ERROR_NOT_FOUND_HOMOLOGADO, detail.getCodigoProducto()),
                        ErrorEntities.INVOICE,
                        ErrorKeys.ERR_RECORD_NOT_FOUND);
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
     * Método que verifica el Nit con el servicio del Siat.
     *
     * @param company          Empresa.
     * @param clientNit        Nit del cliente.
     * @param branchOfficeSiat Sucursal.
     * @param cuisFromDb       Código Cuis.
     * @return
     */
    private InvoiceIssueRes verifyNit(Company company, Long clientNit, Integer branchOfficeSiat, Cuis cuisFromDb) {
        InvoiceIssueRes result = new InvoiceIssueRes();


        // Si está habilitado, los nits se extraen de la base ed datos para no llamar al SIAT
        // ya que el método web es lento
        boolean useSavedDocuments = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.SIAT_USE_SAVED_IDENTITY_DOCUMENT_TYPES));
        log.debug("Buscar en nits guardados: {}", useSavedDocuments);

        if (useSavedDocuments) {
            StopWatch cron = new StopWatch("Verificación de NIT");
            cron.start();

            Optional<ValidIdentityDocumentType> document = validIdentityDocumentTypeRepository.findBySiatCodeAndDocument(
                IDENTITY_DOCUMENT_TYPE_NIT.byteValue(),
                clientNit.toString()
            );

            if (document.isPresent()) {
                cron.stop();
                log.debug("ONLY_CHECK_NIT_TABLE|{}|{}", clientNit, cron.getTotalTimeMillis());
                return new InvoiceIssueRes(200, "El Nit es válido");
            }

            cron.stop();
            log.debug("ONLY_CHECK_NIT_TABLE|{}|{}|no encontrado", clientNit, cron.getTotalTimeMillis());
        }

        SolicitudVerificarNit requestNit = new SolicitudVerificarNit();
        requestNit.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
        requestNit.setCodigoModalidad(company.getModalitySiat().getKey());
        requestNit.setCodigoSistema(company.getSystemCode());
        requestNit.setCodigoSucursal(branchOfficeSiat);
        requestNit.setNit(company.getNit());
        requestNit.setCuis(cuisFromDb.getCuis());
        requestNit.setNitParaVerificacion(clientNit);

        ServicioFacturacionCodigos service = invoiceSoapUtil.getCodeService(company.getToken());

        StopWatch cron = new StopWatch("Verificación de NIT");
        cron.start();

        RespuestaVerificarNit response = service.verificarNit(requestNit);

        cron.stop();
        log.debug("ONLY_CHECK_NIT|{}|{}", clientNit, cron.getTotalTimeMillis());

        if (!response.isTransaccion()) {
            throw new InvoiceCannotBeProccesed(
                SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "Mensaje Del Siat :" + response.getMensajesList().get(0).getDescripcion());
        }
        result.setCode(SiatResponseCodes.SUCCESS);
        return result;
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
    private String generateCuf(XmlBaseInvoice invoice, Company company, InvoiceIssueReq request, ZonedDateTime dateTimeFromSiat, Cufd cufdFromDb, boolean isInvoiceManual) {
        InvoiceUniqueCodeCufService invoiceUniqueCodeService = new InvoiceUniqueCodeCufService();
        InvoiceUniqueCodeGeneratorCufReq cufReq = new InvoiceUniqueCodeGeneratorCufReq();
        cufReq.setNit(company.getNit());
        cufReq.setDatetime(Date.from(dateTimeFromSiat.toInstant()));
        cufReq.setBranchOffice(request.getBranchOfficeSiat());
        cufReq.setModality(company.getModalitySiat().getKey());

        if (isInvoiceManual)
            cufReq.setEmisionType(SiatBroadcastType.BROADCAST_TYPE_OFFLINE);
        else
            cufReq.setEmisionType(this.getSiatBroadcastType(company));

        cufReq.setInvoiceType(SiatInvoiceType.map.get(request.getDocumentSectorType()));
        cufReq.setDocumentSectorType(request.getDocumentSectorType());
        cufReq.setInvoiceNumber(((XmlBaseHeaderInvoice) invoice.getCabecera()).getNumeroFactura());
        cufReq.setPointOfSale(request.getPointSaleSiat());
        cufReq.setControlCode(cufdFromDb.getControlCode());
        Optional<InvoiceUniqueCodeGeneratorCufRes> result = invoiceUniqueCodeService.generateCuf(cufReq);
        return result.get().getCuf();
    }

    /**
     * Método que retorna si el tipo de emisión de la factura.
     *
     * @param company Empresa.
     * @return
     */
    private int getSiatBroadcastType(Company company) {
        // Verifica si la empresa esta habilitada para emisión fuera de linea.
        if (company.getEventSend() && connectivityService.isSiatOffline()) {
            return SiatBroadcastType.BROADCAST_TYPE_OFFLINE;
        }
        return SiatBroadcastType.BROADCAST_TYPE_ONLINE;
    }

    /**
     * Método que verifica si el sistema se encuentra EN LINEA.
     *
     * @return
     */
    private boolean isSiatOffline() {
        //Optional<Offline> searchOffline = offlineRepository.findAll().stream().findFirst();
        Optional<Offline> searchOffline = offlineRepository.findAllNative().stream().findFirst();

        if (searchOffline.isPresent()) {
            Offline updateOffline = searchOffline.get();
            return updateOffline.getActive();
        }
        return false;
    }

    /**
     * Método que verifica la validez de emisión en comparación al evento significativo.
     *
     * @param branchOfficeSiatId Id de la sucursal.
     * @param pointSaleSiatId    Id del punto de venta.
     * @param companyId          Id de la empresa.
     * @param currentDate        Fecha actual.
     * @return
     */
    private boolean canIssueInvoice(Integer branchOfficeSiatId, Integer pointSaleSiatId, Long companyId, ZonedDateTime currentDate, Cufd cufdFromDb) {
        List<Event> event = eventRepository.findDistinctByPointSaleSiatIdBranchOfficeSiatIdActive(branchOfficeSiatId, pointSaleSiatId, companyId);
        if (event.isEmpty()) {
            throw new SiatException("Evento significativo no encontrado o no ha sido generado",
                ErrorEntities.INVOICE, ErrorKeys.ERR_EVENT_NOT_FOUND);
        }
        Integer eventCurrent = event.get(0).getSignificantEvent().getSiatId();
        ZonedDateTime cufdDate = null;
        log.debug("Evento significativo actual : {}", eventCurrent);
        switch (eventCurrent) {
            case 1:
            case 2:
                // Verifica la emisión sumada 72 horas.
                cufdDate = cufdFromDb.getStartDate().plusHours(72);
                if (cufdDate.compareTo(currentDate) > 0) {
                    return true;
                }
                break;
            case 3:
            case 4:
                // Verifica la emisión sumada 15 dias.
                cufdDate = cufdFromDb.getStartDate().plusDays(15);
                if (cufdDate.compareTo(currentDate) > 0) {
                    return true;
                }
                break;
            case 5:
            case 6:
            case 7:
                // Verifica la emisión sumada 3 dias.
                cufdDate = cufdFromDb.getStartDate().plusDays(3);
                if (cufdDate.compareTo(currentDate) > 0) {
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * Método que realiza la validación de la factura.
     *
     * @param invoice Factura.
     * @return
     */
    private InvoiceIssueRes preValidateInvoice(XmlBaseInvoice invoice) {
        InvoiceIssueRes response = new InvoiceIssueRes();
        List<String> messageList = new ArrayList<>();

        if (invoice instanceof XmlPrevaloradaEltrInvoice || invoice instanceof XmlPrevaloradaCmpInvoice) {
            response.setCode(SiatResponseCodes.SUCCESS);
            return response;
        }

        // Verifica el número de documento diferente de 0.
        XmlBaseHeaderInvoice baseHeader = ((XmlBaseHeaderInvoice) invoice.getCabecera());
//        if (null != baseHeader.getCodigoExcepcion() && baseHeader.getCodigoExcepcion() == 0 &&
//            baseHeader.getCodigoTipoDocumentoIdentidad().equals(IDENTITY_DOCUMENT_TYPE_NIT) && this.isSiatOffline()) {
//            messageList.add("El código de excepción debe ser igual a 1");
//        }

        if (baseHeader.getNumeroDocumento().equals("0")) {
            messageList.add("El número de documento debe ser diferente de 0");
        }
        // Verifica el contenido del complemento.
        if (!baseHeader.getCodigoTipoDocumentoIdentidad().equals(IDENTITY_DOCUMENT_CI) &&
            (baseHeader.getComplemento() != null && !baseHeader.getComplemento().equals(""))) {
            messageList.add("El complemento solo puede ser enviado con el tipo de documento: Carnet de Identidad");
        }

        if (!messageList.isEmpty()) {
            throw new DefaultTransactionException(messageList.get(0),
                ErrorEntities.INVOICE, ErrorKeys.ERR_VALIDATIONS);
        }
        response.setCode(SiatResponseCodes.SUCCESS);
        return response;
    }

    /**
     * Método que realiza la validación de la factura.
     *
     * @param invoice Factura.
     * @return
     */
    private InvoiceIssueRes validateInvoice(XmlBaseInvoice invoice) {

        InvoiceIssueRes response = new InvoiceIssueRes();

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
            invoice instanceof XmlSegurosEltrInvoice || invoice instanceof XmlSegurosCmpInvoice ||
            invoice instanceof XmlPrevaloradaEltrInvoice || invoice instanceof XmlPrevaloradaCmpInvoice ||
            invoice instanceof XmlTasaCeroEltrInvoice || invoice instanceof XmlTasaCeroCmpInvoice
        ) {

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
            throw new DefaultTransactionException(messageList.get(0),
                ErrorEntities.INVOICE, ErrorKeys.ERR_VALIDATIONS);
        }
        response.setCode(SiatResponseCodes.SUCCESS);
        return response;
    }

    private String convertZoneTimedateToString(ZonedDateTime time) {

        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedString = time.format(formatter);
        return formattedString;
    }

    private void sendInvoiceEmail(String cuf, String email, String currentToken) {
        new Thread(() -> {
            try {
                String emailUrl = environment.getProperty(ApplicationProperties.NOTIFICATION_ENDPOINT);
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", currentToken);

                JSONObject content = new JSONObject();
                content.put("cuf", cuf);
                content.put("email", email);
                HttpEntity<String> requestEmail = new HttpEntity<String>(content.toString(), headers);
                String resultEmail = restTemplate.postForObject(emailUrl, requestEmail, String.class);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(resultEmail);
                if (root.get("code").equals("200")) {
                    log.debug(String.format("Envio exitoso al correo electrónico: %s", email));
                }
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }).start();
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
                if (null != headerSb.getOtrasTasas())
                    headerSb.setOtrasTasas(exchangeRate.multiply(headerSb.getOtrasTasas()).setScale(2, RoundingMode.HALF_UP));

                headerSb.setMontoTotal(totalAmount.add(headerSb.getTasaAseo()).add(headerSb.getTasaAlumbrado()).add(headerSb.getOtrasTasas())
                    .add(headerSb.getAjusteSujetoIva()).add(headerSb.getOtrosPagosNoSujetoIva()));

                headerSb.setMontoTotalSujetoIva(headerSb.getMontoTotal().subtract(headerSb.getTasaAseo()).
                    subtract(headerSb.getTasaAlumbrado()).subtract(headerSb.getOtrasTasas()).subtract(headerSb.getOtrosPagosNoSujetoIva()));

                BigDecimal totalCurrencyAmountSb = headerSb.getMontoTotal().divide(header.getTipoCambio(), 2, RoundingMode.HALF_UP);
                headerSb.setMontoTotalMoneda(totalCurrencyAmountSb);
            }
        }
    }

    private boolean checkSkipExecuteExtraEvents() {
        return Boolean.parseBoolean(environment.getProperty(ApplicationProperties.SIAT_SKIP_CHECK_EVENTS));
    }

}
