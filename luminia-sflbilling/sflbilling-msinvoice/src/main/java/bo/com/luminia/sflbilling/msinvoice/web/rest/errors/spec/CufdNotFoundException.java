package bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msinvoice.service.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.NotFoundAlertException;

public class CufdNotFoundException extends NotFoundAlertException {
    public CufdNotFoundException(String title, String detail) {
        super(title, detail);
    }

    public CufdNotFoundException() {
        super(ErrorKeys.ERR_RECORD_NOT_FOUND, ResponseMessages.ERROR_CUFD_NOT_FOUND);
    }
}
