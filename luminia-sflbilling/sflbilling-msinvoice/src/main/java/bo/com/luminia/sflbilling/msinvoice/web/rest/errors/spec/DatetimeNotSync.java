package bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msinvoice.service.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.BadRequestAlertException;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.ErrorKeys;

public class DatetimeNotSync extends BadRequestAlertException {

    public DatetimeNotSync() {
        super(ResponseMessages.ERROR_DATE_SYNC, ErrorEntities.INVOICE, ErrorKeys.ERR_SIAT_EXCEPTION);
    }

    public DatetimeNotSync(String defaultMessage, String entityName, String errorKey) {
        super(errorKey, entityName, defaultMessage);
    }
}
