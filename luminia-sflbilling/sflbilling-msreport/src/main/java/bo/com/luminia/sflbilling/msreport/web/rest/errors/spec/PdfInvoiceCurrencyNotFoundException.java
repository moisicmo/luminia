package bo.com.luminia.sflbilling.msreport.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msreport.web.rest.errors.BadRequestAlertException;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.ErrorKeys;

public class PdfInvoiceCurrencyNotFoundException extends BadRequestAlertException {

    public PdfInvoiceCurrencyNotFoundException(String message) {
        super(ErrorKeys.REPORT_PDF_GENERATION, "Reports", message);
    }
}
