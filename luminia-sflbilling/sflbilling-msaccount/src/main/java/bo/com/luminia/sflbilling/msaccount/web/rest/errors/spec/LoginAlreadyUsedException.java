package bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msaccount.web.rest.errors.BadRequestAlertException;

public class LoginAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public LoginAlreadyUsedException() {
        super("Login name already used!", "userManagement", "userexists");
    }
}
