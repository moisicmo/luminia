package bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msinvoice.service.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.NotFoundAlertException;

public class CuisNotFoundException extends NotFoundAlertException {
    public CuisNotFoundException(String title, String detail) {
        super(title, detail);
    }

    public CuisNotFoundException() {
        super(ErrorKeys.ERR_RECORD_NOT_FOUND, ResponseMessages.ERROR_CUIS_NOT_FOUND);
    }
}
