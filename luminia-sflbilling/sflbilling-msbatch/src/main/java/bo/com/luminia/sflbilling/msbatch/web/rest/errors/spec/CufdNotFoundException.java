package bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.NotFoundAlertException;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.ResponseMessages;

public class CufdNotFoundException extends NotFoundAlertException {
    public CufdNotFoundException(String title, String detail) {
        super(title, detail);
    }

    public CufdNotFoundException() {
        super(ErrorKeys.ERR_CUFD_NOT_FOUND, ResponseMessages.ERROR_CUFD_NOT_FOUND);
    }
}
