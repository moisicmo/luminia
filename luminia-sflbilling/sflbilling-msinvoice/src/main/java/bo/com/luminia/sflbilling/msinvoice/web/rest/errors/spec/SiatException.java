package bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec;


import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.BadRequestAlertException;
import lombok.Getter;

@Getter
public class SiatException extends BadRequestAlertException {

    public SiatException(String defaultMessage, String entityName, String errorKey) {
        super(errorKey, entityName, defaultMessage);
    }
}
