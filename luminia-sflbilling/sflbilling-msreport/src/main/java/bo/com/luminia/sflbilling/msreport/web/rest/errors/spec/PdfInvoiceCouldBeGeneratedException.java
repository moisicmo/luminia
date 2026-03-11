package bo.com.luminia.sflbilling.msreport.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msreport.web.rest.errors.BadRequestAlertException;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.ErrorKeys;

public class PdfInvoiceCouldBeGeneratedException extends BadRequestAlertException {

    public PdfInvoiceCouldBeGeneratedException(String message) {
        super(ErrorKeys.REPORT_PDF_GENERATION, "PdfReports", message);
    }
}
