package bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msbatch.web.rest.errors.NotFoundAlertException;

public class UserCompanyNotFoundException extends NotFoundAlertException {

    public UserCompanyNotFoundException() {
        super("User company not found", "User company not found");
    }
}
