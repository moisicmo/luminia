package bo.com.luminia.sflbilling.msinvoice.service.sfe.issue;

import bo.com.luminia.sflbilling.domain.Batch;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.InvoiceBatch;
import bo.com.luminia.sflbilling.domain.enumeration.BatchStatusEnum;
import bo.com.luminia.sflbilling.msinvoice.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msinvoice.repository.BatchRepository;
import bo.com.luminia.sflbilling.msinvoice.repository.InvoiceBatchRepository;
import bo.com.luminia.sflbilling.msinvoice.repository.SectorDocumentTypeRepository;
import bo.com.luminia.sflbilling.msinvoice.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msinvoice.service.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec.SiatException;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceBatchReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceIssueReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.response.sfe.invoice.InvoiceIssueRes;
import bo.gob.impuestos.sfe.invoice.xml.XmlTags;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.TimeZone;

@Service
@AllArgsConstructor
@Slf4j
public class InvoiceBatchService {

    private final Environment environment;
    private final BatchRepository batchRepository;
    private final SectorDocumentTypeRepository sectorDocumentTypeRepository;
    private final InvoiceBatchRepository invoiceBatchRepository;

    /**
     * Método que realiza la recepción de las facturas por lotes.
     *
     * @param company Empresa.
     * @param request Solicitud.
     * @return
     */
    public InvoiceIssueRes reception(Company company, InvoiceBatchReq request) {
        // Verifica si la empresa puede emitir facturas masivas.
        if (!company.getPackageSend()) {
            throw new SiatException(
                ResponseMessages.ERROR_INVOICE_VALIDATION_PACKAGE_SEND,
                ErrorEntities.COMPANY,
                ErrorKeys.ERR_SIAT_EXCEPTION);
        }
        // Verifica el limite de facturas a enviar.
        Integer limitBatch = Integer.parseInt(environment.getProperty(ApplicationProperties.INVOICE_NUMBER_LIMIT_BATCH));
        if (request.getInvoices().size() > limitBatch) {
            throw new SiatException(
                String.format(ResponseMessages.ERROR_INVOICE_VALIDATION_LIMIT_PACKAGE_SEND, limitBatch, request.getInvoices().size()),
                ErrorEntities.COMPANY,
                ErrorKeys.ERR_SIAT_EXCEPTION);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setTimeZone(TimeZone.getTimeZone("America/La_Paz"));
            // Obtiene fecha y hora actual.
            ZonedDateTime currentDateTime = ZonedDateTime.now();
            // Obtiene el codigo de recepcion.
            String receptionCode = this.receptionCode();

            Batch batch = new Batch();
            batch.setReceptionCode(receptionCode);
            batch.setDate(currentDateTime);
            batch.setStatus(BatchStatusEnum.PENDING);
            batch.setCompany(company);
            Batch batchNew = batchRepository.save(batch);

            for (InvoiceIssueReq invoice : request.getInvoices()) {

                // Obtiene la factura en formato JSON.
                String jsonInvoice = mapper.writeValueAsString(invoice);

                InvoiceBatch invoiceBatch = new InvoiceBatch();
                invoiceBatch.setInvoiceNumber(new Long((String) invoice.getHeader().get(XmlTags.HEADER_NUMERO_FACTURA)));
                invoiceBatch.setBroadcastDate(currentDateTime);
                invoiceBatch.setInvoiceJson(jsonInvoice);
                invoiceBatch.setResponseMessage(null);
                invoiceBatch.setInvoice(null);
                invoiceBatch.setBatch(batchNew);
                invoiceBatch.setSectorDocumentType(sectorDocumentTypeRepository.getBySiatIdAndCompanyIdAndActiveIsTrue(invoice.getDocumentSectorType(), company.getId().intValue()));

                invoiceBatchRepository.save(invoiceBatch);
            }

            log.debug("Lote de facturas recepcionado correctamente");
            HashMap<String, Object> body = new HashMap<>();
            body.put("receptionCode", receptionCode);

            return new InvoiceIssueRes(SiatResponseCodes.SUCCESS, "Lote de facturas recepcionado correctamente ", body);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SiatException(
                e.getMessage(),
                ErrorEntities.INVOICE,
                ErrorKeys.ERR_SIAT_EXCEPTION);
        }
    }

    private String receptionCode() {
        return RandomStringUtils.randomAlphanumeric(10).toUpperCase();
    }
}
