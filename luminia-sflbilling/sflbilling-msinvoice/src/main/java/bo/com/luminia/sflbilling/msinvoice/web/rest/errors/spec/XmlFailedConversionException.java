package bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.BadRequestAlertException;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.ErrorKeys;

public class XmlFailedConversionException extends BadRequestAlertException {

    public XmlFailedConversionException(String defaultMessage, String entityName, String errorKey) {
        super(errorKey, entityName, defaultMessage);
    }

    public XmlFailedConversionException(String message) {
        super(ErrorKeys.ERR_XML_EXCEPTION, null, message);
    }
}
