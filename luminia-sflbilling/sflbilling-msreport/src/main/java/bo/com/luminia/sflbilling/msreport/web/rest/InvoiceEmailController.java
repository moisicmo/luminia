package bo.com.luminia.sflbilling.msreport.web.rest;

import bo.com.luminia.sflbilling.domain.Invoice;
import bo.com.luminia.sflbilling.msreport.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msreport.repository.InvoiceRepository;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoice;
import bo.com.luminia.sflbilling.msreport.siat.pdf.service.PdfGeneratorService;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.spec.PdfInvoiceCouldBeGeneratedException;
import bo.com.luminia.sflbilling.msreport.web.rest.request.InvoiceEmailReq;
import bo.com.luminia.sflbilling.msreport.web.rest.response.InvoiceEmailRes;
import bo.com.luminia.sflbilling.msreport.web.rest.util.ResponseMessages;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
public class InvoiceEmailController {

    private final InvoiceRepository invoiceRepository;
    private final PdfGeneratorService generatorService;

    private final ApplicationProperties applicationProperties;

    @PostMapping("/send")
    public ResponseEntity<InvoiceEmailRes> send(@RequestBody InvoiceEmailReq request) {
        byte[] bytes = null;
        InvoiceEmailRes response = new InvoiceEmailRes();

        try {
            bytes = generatorService.exportReportPdf(request.getCuf(), PdfInvoice.PAPER_A4);
            if (bytes == null) {
                response.setCode(500);
                response.setMessage("La factura no ha podido generarse");
                throw new PdfInvoiceCouldBeGeneratedException(ResponseMessages.ERROR_FACTURA_GENERARSE);
            } else {
                Optional<Invoice> invoiceCurrent = invoiceRepository.findInvoiceByCuf(request.getCuf());
                String pdfBase64 = Base64.getEncoder().encodeToString(bytes);
                String xmlBase64 = Base64.getEncoder().encodeToString(invoiceCurrent.get().getInvoiceXml().getBytes());

                String emailUrl = applicationProperties.getNotificationEndpoint();
                String clientId = applicationProperties.getNotificationClientId();
                String apiKey = applicationProperties.getNotificationApiKey();
                String htmlContent = "<p>Estimado(a) contribuyente:</p>" +
                    "<p>Se ha emitido un documento fiscal con Código Único de Factura (CUF): “" + request.getCuf() + "”. Se adjunta la representación gráfica y el archivo XML correspondiente.</p>" +
                    "<p>Saludos cordiales.</p>";
                String subject = "Emisión de documento fiscal";

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("X-Client-ID", clientId);
                headers.set("X-API-Key", apiKey);

                JSONObject content = new JSONObject();
                content.put("type", "direct");
                content.put("to", request.getEmail());
                content.put("subject", subject);
                content.put("html", htmlContent);

                JSONArray attachments = new JSONArray();

                JSONObject pdfAttachment = new JSONObject();
                pdfAttachment.put("filename", "factura.pdf");
                pdfAttachment.put("content", pdfBase64);
                pdfAttachment.put("contentType", "application/pdf");
                attachments.put(pdfAttachment);

                JSONObject xmlAttachment = new JSONObject();
                xmlAttachment.put("filename", "documentoFiscal.xml");
                xmlAttachment.put("content", xmlBase64);
                xmlAttachment.put("contentType", "application/xml");
                attachments.put(xmlAttachment);

                content.put("attachments", attachments);

                HttpEntity<String> requestEmail = new HttpEntity<>(content.toString(), headers);
                log.debug("Request notificador, url: {}, request length: {}", emailUrl, requestEmail.toString().length());
                ResponseEntity<String> resultEmail = restTemplate.postForEntity(emailUrl, requestEmail, String.class);
                log.debug("Response notificador: {}", resultEmail);

                if (resultEmail.getStatusCode().is2xxSuccessful()) {
                    log.info("Envio exitoso al correo electrónico: {}", request.getEmail());
                    response.setCode(200);
                    response.setMessage("La factura se envío correctamente al correo electrónico");
                } else {
                    response.setCode(400);
                    response.setMessage("Error al enviar el correo:" + resultEmail.getBody());
                }
            }
        } catch (PdfInvoiceCouldBeGeneratedException e) {
            log.error("PdfInvoiceCouldBeGeneratedException, error al realizar request al notificador: {}", e.getMessage(), e);
            throw new PdfInvoiceCouldBeGeneratedException(ResponseMessages.ERROR_INVOICE_EMAIL_SEND);
        } catch (Exception e) {
            log.error("Exception error al enviar el correo: {}", e.getMessage(), e);
            throw new PdfInvoiceCouldBeGeneratedException(ResponseMessages.ERROR_INVOICE_EMAIL_SEND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
