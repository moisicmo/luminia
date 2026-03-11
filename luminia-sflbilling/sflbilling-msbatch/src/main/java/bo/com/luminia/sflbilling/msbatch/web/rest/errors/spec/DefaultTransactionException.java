package bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec;


import bo.com.luminia.sflbilling.msbatch.web.rest.errors.BadRequestAlertException;
import lombok.Getter;

@Getter
public class DefaultTransactionException extends BadRequestAlertException {

    public DefaultTransactionException(String defaultMessage, String entityName, String errorKey) {
        super(errorKey, entityName, defaultMessage);
    }
}
