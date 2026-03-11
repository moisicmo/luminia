package bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec;


import bo.com.luminia.sflbilling.msbatch.web.rest.errors.BadRequestAlertException;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorKeys;
import lombok.Getter;

@Getter
public class SiatException extends BadRequestAlertException {

    public SiatException(String defaultMessage, String entityName, String errorKey) {
        super(errorKey, entityName, defaultMessage);
    }

    public SiatException(String message) {
        super(ErrorKeys.ERR_SIAT_EXCEPTION, ErrorEntities.INVOICE, message);
    }
}
