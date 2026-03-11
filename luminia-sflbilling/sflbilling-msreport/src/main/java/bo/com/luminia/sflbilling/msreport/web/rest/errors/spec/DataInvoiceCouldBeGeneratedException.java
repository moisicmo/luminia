package bo.com.luminia.sflbilling.msreport.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msreport.web.rest.errors.BadRequestAlertException;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.ErrorKeys;

public class DataInvoiceCouldBeGeneratedException extends BadRequestAlertException {

    public DataInvoiceCouldBeGeneratedException(String message) {
        super(ErrorKeys.REPORT_DATA_GENERATION, "DataReports", message);
    }
}
