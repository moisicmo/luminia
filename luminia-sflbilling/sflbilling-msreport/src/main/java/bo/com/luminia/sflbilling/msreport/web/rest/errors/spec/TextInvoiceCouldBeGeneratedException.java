package bo.com.luminia.sflbilling.msreport.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msreport.web.rest.errors.BadRequestAlertException;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.ErrorKeys;

public class TextInvoiceCouldBeGeneratedException extends BadRequestAlertException {

    public TextInvoiceCouldBeGeneratedException(String message) {
        super(ErrorKeys.REPORT_PDF_GENERATION, "TextReports", message);
    }
}
