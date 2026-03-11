package bo.com.luminia.sflbilling.msinvoice.web.rest;

import bo.com.luminia.sflbilling.msinvoice.repository.*;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.*;
import bo.com.luminia.sflbilling.domain.*;
import bo.com.luminia.sflbilling.domain.enumeration.*;
import bo.com.luminia.sflbilling.msinvoice.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msinvoice.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msinvoice.service.dto.siat.XmlRecoveredDto;
import bo.com.luminia.sflbilling.domain.enumeration.InvoiceStatusEnum;
import bo.com.luminia.sflbilling.msinvoice.repository.*;
import bo.com.luminia.sflbilling.msinvoice.service.sfe.issue.ConnectivityService;
import bo.com.luminia.sflbilling.msinvoice.service.sfe.issue.InvoiceBatchService;
import bo.com.luminia.sflbilling.msinvoice.service.sfe.issue.InvoiceIssueService;
import bo.com.luminia.sflbilling.msinvoice.service.sfe.issue.InvoiceOperationService;
import bo.com.luminia.sflbilling.msinvoice.service.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msinvoice.siat.invoice.xml.XmlInvoiceAdapter;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.BadRequestAlertException;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.NotFoundAlertException;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.*;
import bo.com.luminia.sflbilling.msinvoice.web.rest.response.sfe.invoice.InvoiceIssueRes;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/invoices")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
public class InvoiceIssueController {

    private final CompanyRepository companyRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceLegendRepository invoiceLegendRepository;
    private final InvoiceRequestRepository invoiceRequestRepository;
    private final InvoiceBatchService invoiceBatchService;
    private final InvoiceIssueService issueService;
    private final InvoiceOperationService invoiceOperationService;
    private final ConnectivityService cnnectivityService;
    private final ValidIdentityDocumentTypeRepository validIdentityDocumentTypeRepository;
    private final Environment environment;

    @ApiOperation(value = "Método para la emisión de facturas.", response = InvoiceIssueRes.class)
    @PostMapping("/issues")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<InvoiceIssueRes> issue(@Valid @RequestBody InvoiceIssueReq invoiceIssueReq) {
        StopWatch watch = new StopWatch("Inicio facturación company_id=" + invoiceIssueReq.getCompanyId());
        log.debug("Iniciando emisión de factura : {}", invoiceIssueReq.getBusinessCode());

        watch.start("Registrando request y validaciones");
        InvoiceRequest record = createInvoiceIssueRequest(invoiceIssueReq, InvoiceRequestTypeEnum.EMIT);

        //Cuando estamos offline, existen algunos clientes que colocan un CI pero lo envían como NIT
        //Internamente se ha acordado que
        if (cnnectivityService.isSiatOffline()) {
            Integer exceptionCode = Integer.parseInt(invoiceIssueReq.getHeader().getOrDefault("codigoExcepcion", "0").toString());
            Integer documentType = Integer.parseInt(invoiceIssueReq.getHeader().getOrDefault("codigoTipoDocumentoIdentidad", null).toString());
            if (IdentityDocumentTypeEnum.NIT.getKey().equals(documentType)) {
                log.debug("Factura será en offline y es con nit, formamos la excepción para asegurar la transacción");
                invoiceIssueReq.getHeader().put("codigoExcepcion", "1");
            }
        }

        watch.stop();

        InvoiceIssueRes response = new InvoiceIssueRes();
        Optional<Company> companyFromDb = companyRepository.findByBusinessCodeAndActiveTrue(invoiceIssueReq.getBusinessCode());

        if (!companyFromDb.isPresent()) {
            finishInvoiceRequestError(record.getId(), watch, "Company not found");
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, invoiceIssueReq.getBusinessCode()));
        } else {

            watch.start("Creación xml");
            Company company = companyFromDb.get();
            invoiceIssueReq.setCompanyId(company.getId());
            XmlBaseInvoice baseInvoice = null;
            baseInvoice = XmlInvoiceAdapter.convert(invoiceIssueReq, company.getModalitySiat(), invoiceLegendRepository);
            watch.stop();

            record.setCompany(company);
            invoiceRequestRepository.saveAndFlush(record);
            try {
                response = issueService.emit(invoiceIssueReq, baseInvoice, record.getId(), watch);
                finishInvoiceRequestSuccess(record.getId(), watch);
            } catch (Exception e) {
                finishInvoiceRequestError(record.getId(), watch, e.getMessage());
                throw e;
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    /**
     * Crea un registro exitoso de la solicitud de factura
     *
     * @param recordId
     * @param watch
     */
    private void finishInvoiceRequestSuccess(Long recordId, StopWatch watch) {
        if (watch.isRunning())
            watch.stop();
        InvoiceRequest record = invoiceRequestRepository.getById(recordId);
        record.setResponse(true);
        record.setResponseDate(ZonedDateTime.now());
        record.setElapsedTime(watch.getTotalTimeMillis());
        invoiceRequestRepository.saveAndFlush(record);
    }

    /**
     * Crea un registro de error de la solicitud de factura
     *
     * @param recordId
     * @param watch
     */
    private void finishInvoiceRequestError(Long recordId, StopWatch watch, String observation) {
        if (watch.isRunning())
            watch.stop();
        InvoiceRequest record = invoiceRequestRepository.getById(recordId);
        record.setResponse(false);
        record.setResponseDate(ZonedDateTime.now());
        record.setElapsedTime(watch.getTotalTimeMillis());
        record.setObservation(observation);
        invoiceRequestRepository.saveAndFlush(record);
    }

    /**
     * Crea el inicio de una solicitud
     *
     * @param obj
     * @param transactionType
     * @return
     */
    private InvoiceRequest createInvoiceIssueRequest(Object obj, InvoiceRequestTypeEnum transactionType) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getTimeZone("America/La_Paz"));

        InvoiceRequest record = new InvoiceRequest();
        record.setRequestDate(ZonedDateTime.now());
        try {
            record.setRequest(mapper.writeValueAsString(obj));
        } catch (Exception e) {
            e.printStackTrace();
        }
        record.setType(transactionType);
        record.setElapsedTime(0L);
        record.setResponse(false);

        invoiceRequestRepository.saveAndFlush(record);
        return record;
    }

    @ApiOperation(value = "Método para la emisión de facturas manuales.", response = InvoiceIssueRes.class)
    @PostMapping("/issues/manuals")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<InvoiceIssueRes> issueManual(@Valid @RequestBody InvoiceIssueManualReq invoiceIssueManualReq) {
        StopWatch watch = new StopWatch("Inicio facturación manual company_id=" + invoiceIssueManualReq.getCompanyId());
        log.debug("Iniciando emisión de factura manual: {}", invoiceIssueManualReq.getBusinessCode());

        watch.start("Registrando request y validaciones");
        InvoiceRequest record = createInvoiceIssueRequest(invoiceIssueManualReq, InvoiceRequestTypeEnum.EMIT_MANUAL);

        InvoiceIssueRes response = new InvoiceIssueRes();
        Optional<Company> companyFromDb = companyRepository.findByBusinessCodeAndActiveTrue(invoiceIssueManualReq.getBusinessCode());
        watch.stop();

        if (!companyFromDb.isPresent()) {
            finishInvoiceRequestError(record.getId(), watch, "Empresa no encontrada");
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, invoiceIssueManualReq.getBusinessCode()));
        } else {
            watch.start("Generando xml");

            Company company = companyFromDb.get();
            invoiceIssueManualReq.setCompanyId(company.getId());
            XmlBaseInvoice baseInvoice = null;
            baseInvoice = XmlInvoiceAdapter.convert(invoiceIssueManualReq, company.getModalitySiat(), invoiceLegendRepository);

            record.setCompany(company);
            invoiceRequestRepository.saveAndFlush(record);

            watch.stop();

            try {
                response = issueService.emitManual(invoiceIssueManualReq, baseInvoice, record, watch);
                finishInvoiceRequestSuccess(record.getId(), watch);
            } catch (Exception e) {
                finishInvoiceRequestError(record.getId(), watch, e.getMessage());
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PostMapping("/issues/massive")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<InvoiceIssueRes> reception(@Valid @RequestBody InvoiceBatchReq request) {
        log.debug("Recepción de lote de facturas, cantidad: {}", request.getInvoices().size());
        Optional<Company> companyFromDb = companyRepository.findByBusinessCodeAndActiveTrue(request.getBusinessCode());

        if (!companyFromDb.isPresent()) {
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, request.getBusinessCode()));
        } else {
            InvoiceIssueRes response = invoiceBatchService.reception(companyFromDb.get(), request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Método  para la anulación de facturas.", response = InvoiceIssueRes.class)
    @PostMapping("/cancellations")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<InvoiceIssueRes> cancellation(@Valid @RequestBody InvoiceCancellationReq request) {
        log.debug("Anulación de la factura : {}", request);
        StopWatch watch = new StopWatch("Anulación de la factura");

        watch.start("Registrando request anulación");
        InvoiceRequest record = createInvoiceIssueRequest(request, InvoiceRequestTypeEnum.CANCELLATION);

        InvoiceIssueRes response = new InvoiceIssueRes();
        Optional<Invoice> invoiceFromDb = invoiceRepository.findByCufAndStatus(request.getCuf(), InvoiceStatusEnum.EMITTED);
        watch.stop();

        if (!invoiceFromDb.isPresent()) {
            finishInvoiceRequestError(record.getId(), watch, "Factura no encontrada");
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_INVOICE_NOT_FOUND, request.getCuf()));
        } else {
            Invoice invoice = invoiceFromDb.get();

            record.setInvoice(invoice);
            record.setCompany(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany());
            record.setCuf(request.getCuf());
            invoiceRequestRepository.saveAndFlush(record);

            try {
                response = invoiceOperationService.cancellation(request, invoice, record.getId(), watch);
            } catch (Exception e) {
                finishInvoiceRequestError(record.getId(), watch, e.getMessage());
            }

            if (response.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT)) {
                finishInvoiceRequestError(record.getId(), watch, response.getMessage());
            } else {
                finishInvoiceRequestSuccess(record.getId(), watch);
            }

            boolean printCronDetail = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.INVOICE_PRINT_EMIT_CRON_DETAIL));
            if (printCronDetail) {
                StringBuilder builder = new StringBuilder();
                builder.append("\n");
                builder.append("Cancelación factura: ").append(request.getCuf()).append("\n");
                builder.append("Empresa: ").append(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getName()).append("\n");
                builder.append("Tiempo:").append(watch.getTotalTimeMillis()).append("\n");
                builder.append("Code:").append(response.getCode()).append("\n");
                builder.append("Response:").append(response.getMessage()).append("\n");
                builder.append(watch.prettyPrint());
                log.debug(builder.toString());
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Método  para la reversion de facturas anuladas.", response = InvoiceIssueRes.class)
    @PostMapping("/cancellations/reversal")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<InvoiceIssueRes> cancellationReversal(@Valid @RequestBody InvoiceCancellationReversalReq request) {
        log.debug("Reversion de factura anulada : {}", request);
        StopWatch watch = new StopWatch("Reversion de factura anulada");

        watch.start("Registrando request reversion");
        InvoiceRequest record = createInvoiceIssueRequest(request, InvoiceRequestTypeEnum.REVERSION);

        InvoiceIssueRes response = new InvoiceIssueRes();
        Optional<Invoice> invoiceFromDb = invoiceRepository.findByCufAndStatus(request.getCuf(), InvoiceStatusEnum.CANCELED);
        watch.stop();

        if (!invoiceFromDb.isPresent()) {
            finishInvoiceRequestError(record.getId(), watch, "Factura no encontrada");
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_INVOICE_CANCELLED_NOT_FOUND, request.getCuf()));
        } else {
            Invoice invoice = invoiceFromDb.get();

            record.setInvoice(invoice);
            record.setCompany(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany());
            record.setCuf(request.getCuf());
            invoiceRequestRepository.saveAndFlush(record);

            try {
                response = invoiceOperationService.cancellationReversal(request, invoice, record.getId(), watch);
            } catch (Exception e) {
                finishInvoiceRequestError(record.getId(), watch, e.getMessage());
            }

            if (response.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT)) {
                finishInvoiceRequestError(record.getId(), watch, response.getMessage());
            } else {
                finishInvoiceRequestSuccess(record.getId(), watch);
            }

            boolean printCronDetail = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.INVOICE_PRINT_EMIT_CRON_DETAIL));
            if (printCronDetail) {
                StringBuilder builder = new StringBuilder();
                builder.append("\n");
                builder.append("Reversion de factura anulada: ").append(request.getCuf()).append("\n");
                builder.append("Empresa: ").append(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getName()).append("\n");
                builder.append("Tiempo:").append(watch.getTotalTimeMillis()).append("\n");
                builder.append("Code:").append(response.getCode()).append("\n");
                builder.append("Response:").append(response.getMessage()).append("\n");
                builder.append(watch.prettyPrint());
                log.debug(builder.toString());
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PostMapping("/check-nit")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<InvoiceIssueRes> checkNit(@Valid @RequestBody CheckNitReq checkNitReq) {
        log.debug("Emision de facturas : {}", checkNitReq);

        InvoiceIssueRes response;
        Optional<Company> companyFromDb = companyRepository.findByBusinessCodeAndActiveTrue(checkNitReq.getBusinessCode());
        if (!companyFromDb.isPresent()) {
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, checkNitReq.getBusinessCode()));
        } else {
            Company company = companyFromDb.get();
            checkNitReq.setCompanyId(company.getId());
            response = issueService.checkNit(company, checkNitReq);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }


    /*

      private final SignatureService signatureService;
      @PostMapping("/regenerate")
//    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.COMPANY_ADMIN + "')")
    public ResponseEntity<Void> regenerateXml(@Valid @RequestBody Map<String, String> request) throws JsonProcessingException {
        log.debug("Regenerate xml invoices: {}", request);

        Long invoiceId = Long.valueOf(request.get("invoiceId"));

        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new NotFoundAlertException("Not found", "Not found"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getTimeZone("America/La_Paz"));
        XmlCompraVentaEltrInvoice xml = mapper.readValue(invoice.getInvoiceJson(), XmlCompraVentaEltrInvoice.class);

        Long companyNit = xml.getCabecera().getNitEmisor();
        Company company = companyRepository.findByNit(companyNit).orElseThrow(() -> new NotFoundAlertException("Company not found", "Not found"));

        try {
            String xmlInvoice = xml.generate(true);

            if (xml.isValidXml(xmlInvoice)) {
                xmlInvoice = signatureService.singXml(xmlInvoice, company.getId());
//                log.debug("Xml firmado: {}", xmlInvoice);

                byte[] gzipXml = FileCompress.textToGzip(xmlInvoice);
                log.debug("Factura comprimida: {}", gzipXml.length);

                String hashXml = FileHash.sha256(gzipXml);
                log.debug("Hash del documento compreso: {}", hashXml);

                if (invoice.getInvoiceXml() == null || invoice.getInvoiceXml().isEmpty()) {
                    invoice.setInvoiceXml(xmlInvoice);
                    log.debug("Invoice xml found and setted");

                    invoice.setInvoiceHash(hashXml);
                    log.debug("Invoice hash found and setted");

                    invoiceRepository.save(invoice);
                    log.info("Invoice has updated, id: {}, hash: {}", invoice.getId(), invoice.getInvoiceHash());

                } else {
                    log.debug("This invoice contains xml and hash");
                }
            }

        } catch (Exception e) {
            log.error("Error al generar el xml");
            throw new BadRequestAlertException("ERROR_GENERATE_XML", "Error al generar el xml");
        }

        return ResponseEntity.ok().build();
    }
    */

    @PostMapping("/resend")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<InvoiceIssueRes> resendInvoice(@Valid @RequestBody Map<String, String> request) throws JsonProcessingException {
        StopWatch watch = new StopWatch("Resend");
        watch.start("Calculando reenvío");

        log.debug("Resend invoice: {}", request);
        watch.start("Registrando request");
        InvoiceRequest record = createInvoiceIssueRequest(request, InvoiceRequestTypeEnum.EMIT);

        Long invoiceId = Long.valueOf(request.getOrDefault("invoiceId", "0"));
        String cuf = request.getOrDefault("cuf", null);
        Boolean forceException = Boolean.parseBoolean(request.getOrDefault("omitirValidacion", "false"));
        Integer changeDocumentType = Integer.parseInt(request.getOrDefault("cambiarTipoDocumento", "0"));

        log.debug("omitir factura: {}", forceException);
        log.debug("Cambiar tipo documento: {}", changeDocumentType);

        Invoice invoice;
        if (Strings.isBlank(cuf)) {
            log.debug("Buscando por invoiceId: {}", invoiceId);
            invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new NotFoundAlertException("Not found", "Not found"));
        } else {
            cuf = cuf.trim();
            log.debug("Buscando por cuf: {}", cuf);
            invoice = invoiceRepository.findByCuf(cuf).orElseThrow(() -> new NotFoundAlertException("Not found", "Not found"));
        }

        SectorDocumentType sector = invoice.getSectorDocumentType();
        Company company = invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany();

        record.setCompany(company);
        record.setInvoice(invoice);
        invoiceRequestRepository.saveAndFlush(record);


        //Recupero el xml en base al json recuperado
        XmlRecoveredDto xmlRecovered = XmlInvoiceAdapter.deduceAndChangeXml(
            invoice.getInvoiceJson(),
            company.getModalitySiat(),
            sector.getSiatId(),
            forceException,
            changeDocumentType
        );
        log.info("Cabecera a interpretar: {}", xmlRecovered.getXml().getCabecera().getClass().toString());

        if (xmlRecovered.getXml() == null) {
            finishInvoiceRequestError(record.getId(), watch, "Tipo de factura no soportada");
            throw new NotFoundAlertException("Tipo de factura no soportada", "Tipo de factura no soportada");
        }

        //Creo mi request, para que se emita la factura
        InvoiceIssueReq input = new InvoiceIssueReq();
        input.setCompanyId(company.getId());
        input.setBusinessCode(xmlRecovered.getBusinessCode());
        input.setBranchOfficeSiat(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getBranchOfficeSiatId());
        input.setPointSaleSiat(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
        input.setDocumentSectorType(xmlRecovered.getDocumentSectorType());
        input.setEmail("");
        input.setUseCurrencyType(null); //IMPORTANTE: esto es nulo para que no recalcule el monto.
        input.setBarcode(xmlRecovered.getBarcode());

        watch.stop();

        try {

            InvoiceIssueRes response = issueService.emit(input, xmlRecovered.getXml(), record.getId(), watch);
            log.debug("Invoice id(old)={}, responseCode={}, responseMsg={}", invoiceId, response.getCode(), response.getMessage());

            if (response.getBody() != null) {
                if (response.getBody() instanceof HashMap) {
                    HashMap<String, Object> theResponse = (HashMap<String, Object>) response.getBody();
                    if (theResponse.containsKey("cuf")) {
                        log.info("RESEND_INVOICE|{}|{}|{}|{}",
                            invoiceId,
                            invoice.getCuf(),
                            theResponse.getOrDefault("id", "none"),
                            theResponse.getOrDefault("cuf", "none")
                        );
                        finishInvoiceRequestSuccess(record.getId(), watch);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }
            }

            finishInvoiceRequestError(record.getId(), watch, "Factura no generada");
            throw new BadRequestAlertException("ERROR_RESEND_INVOICE", "Factura no generada");

        } catch (Exception e) {
            log.error("Error al reenviar factura");
            log.error(e.getMessage());
            finishInvoiceRequestError(record.getId(), watch, e.getMessage());
            throw new BadRequestAlertException("ERROR_RESEND_INVOICE", "Error al reenviar la factura: " + e.getMessage());
        }
    }


    /*@ApiOperation(value = "Método  para la anulación de facturas por fuerza bruta.", response = InvoiceIssueRes.class)
    @PostMapping("/cancellationsforce")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<InvoiceIssueRes> cancellationBruteForce(@Valid @RequestBody InvoiceCancellationForceReq request) {
        log.debug("Anulación de la factura por fuerza bruta : {}", request);
        StopWatch watch = new StopWatch("Anulación de la factura");

        watch.start("Registrando request anulación");
        InvoiceRequest record = createInvoiceIssueRequest(request, InvoiceRequestTypeEnum.CANCELLATION);

        InvoiceIssueRes response = new InvoiceIssueRes();
        watch.stop();

        Optional<Company> optionalCompany = companyRepository.findById(request.getCompanyId());
        if (!optionalCompany.isPresent()) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, request.getCompanyId()));
        }

        //Guardo en el histórico de request
        record.setCompany(optionalCompany.get());
        record.setCuf(request.getCuf());
        invoiceRequestRepository.saveAndFlush(record);

        //Revisar si existe la factura
        //NOTA: No siempre la factura existirá en la BD
        Optional<Invoice> optionalInvoice = invoiceRepository.findByCuf(request.getCuf());

        //Nota: Si la factura no existe en el sistema, supondrá que es de la misma compañía
        if (optionalInvoice.isPresent() && !isSameCompany(optionalInvoice.get(), optionalCompany.get())) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_INVOICE_DOESNT_MATCH, request.getCompanyId()));
        }

        if (optionalInvoice.isPresent() &&
            (optionalInvoice.get().getStatus().equals(InvoiceStatusEnum.CANCELED) ||
                optionalInvoice.get().getStatus().equals(InvoiceStatusEnum.REVERSED))
        ) {
            response = new InvoiceIssueRes(SiatResponseCodes.SUCCESS, "factura previamente anulada");
        } else {
            try {
                response = invoiceOperationService.cancellationBruteForce(request, record, optionalInvoice, watch);
            } catch (Exception e) {
                finishInvoiceRequestError(record.getId(), watch, e.getMessage());
            }
        }

        if (response.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT)) {
            finishInvoiceRequestError(record.getId(), watch, response.getMessage());
        } else {
            finishInvoiceRequestSuccess(record.getId(), watch);
        }

        boolean printCronDetail = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.INVOICE_PRINT_EMIT_CRON_DETAIL));
        if (printCronDetail) {
            StringBuilder builder = new StringBuilder();
            builder.append("\n");
            builder.append("Cancelación factura fuerza bruta: ").append(request.getCuf()).append("\n");
            builder.append("Empresa: ").append(optionalCompany.get().getName()).append("\n");
            builder.append("Tiempo:").append(watch.getTotalTimeMillis()).append("\n");
            builder.append("Code:").append(response.getCode()).append("\n");
            builder.append("Response:").append(response.getMessage()).append("\n");
            builder.append(watch.prettyPrint());
            log.debug(builder.toString());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }*/

    @ApiOperation(value = "Método  para la anulación de facturas que especifica todos los datos.", response = InvoiceIssueRes.class)
    @PostMapping("/cancellationsAllParams")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<InvoiceIssueRes> cancellationsAllParams(@Valid @RequestBody InvoiceCancellationAllParamsReq request) {
        log.debug("Anulación de la factura con todos los parámetros : {}", request);
        StopWatch watch = new StopWatch("Anulación de la factura");

        watch.start("Registrando request anulación");
        InvoiceRequest record = createInvoiceIssueRequest(request, InvoiceRequestTypeEnum.CANCELLATION);

        InvoiceIssueRes response = new InvoiceIssueRes();
        watch.stop();

        Optional<Company> optionalCompany = companyRepository.findById(request.getCompanyId());
        if (!optionalCompany.isPresent()) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, request.getCompanyId()));
        }

        //Guardo en el histórico de request
        record.setCompany(optionalCompany.get());
        record.setCuf(request.getCuf());
        invoiceRequestRepository.saveAndFlush(record);

        //Revisar si existe la factura
        //NOTA: No siempre la factura existirá en la BD
        Optional<Invoice> optionalInvoice = invoiceRepository.findByCuf(request.getCuf());

        //Nota: Si la factura no existe en el sistema, supondrá que es de la misma compañía
        if (optionalInvoice.isPresent() && !isSameCompany(optionalInvoice.get(), optionalCompany.get())) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_INVOICE_DOESNT_MATCH, request.getCompanyId()));
        }

        if (optionalInvoice.isPresent() &&
            (optionalInvoice.get().getStatus().equals(InvoiceStatusEnum.CANCELED) ||
                optionalInvoice.get().getStatus().equals(InvoiceStatusEnum.REVERSED))
        ) {
            response = new InvoiceIssueRes(SiatResponseCodes.SUCCESS, "factura previamente anulada");
        } else {
            try {
                response = invoiceOperationService.cancellationsAllParams(request, record, optionalInvoice, watch);
            } catch (Exception e) {
                finishInvoiceRequestError(record.getId(), watch, e.getMessage());
            }
        }

        if (response.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT)) {
            finishInvoiceRequestError(record.getId(), watch, response.getMessage());
        } else {
            finishInvoiceRequestSuccess(record.getId(), watch);
        }

        boolean printCronDetail = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.INVOICE_PRINT_EMIT_CRON_DETAIL));
        if (printCronDetail) {
            StringBuilder builder = new StringBuilder();
            builder.append("\n");
            builder.append("Cancelación factura todos los parámetros: ").append(request.getCuf()).append("\n");
            builder.append("Empresa: ").append(optionalCompany.get().getName()).append("\n");
            builder.append("Tiempo:").append(watch.getTotalTimeMillis()).append("\n");
            builder.append("Code:").append(response.getCode()).append("\n");
            builder.append("Response:").append(response.getMessage()).append("\n");
            builder.append(watch.prettyPrint());
            log.debug(builder.toString());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    private boolean isSameCompany(Invoice invoice, Company company) {
        return invoice.getCufd()
            .getCuis()
            .getPointSale()
            .getBranchOffice()
            .getCompany()
            .equals(company);
    }


}
