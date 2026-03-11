package bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.NotFoundAlertException;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.ResponseMessages;

public class CuisNotFoundException extends NotFoundAlertException {

    public CuisNotFoundException(String title, String detail) {
        super(title, detail);
    }

    public CuisNotFoundException() {
        super(ErrorKeys.ERR_CUIS_NOT_FOUND, ResponseMessages.ERROR_CUIS_NOT_FOUND);
    }

    public CuisNotFoundException(String message) {
        super(ErrorKeys.ERR_CUIS_NOT_FOUND, message);
    }
}
