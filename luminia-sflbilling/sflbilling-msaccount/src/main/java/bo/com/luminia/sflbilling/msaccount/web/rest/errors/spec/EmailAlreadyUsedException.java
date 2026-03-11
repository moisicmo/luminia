package bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msaccount.web.rest.errors.BadRequestAlertException;

public class EmailAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyUsedException() {
        super("Email is already in use!", "userManagement", "emailexists");
    }
}
