package bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msbatch.web.rest.errors.BadRequestAlertException;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.ResponseMessages;

public class DatetimeNotSync extends BadRequestAlertException {

    public DatetimeNotSync() {
        super(ResponseMessages.ERROR_DATE_SYNC, ErrorEntities.INVOICE, ErrorKeys.ERR_SIAT_EXCEPTION);
    }

    public DatetimeNotSync(String defaultMessage, String entityName, String errorKey) {
        super(errorKey, entityName, defaultMessage);
    }
}
