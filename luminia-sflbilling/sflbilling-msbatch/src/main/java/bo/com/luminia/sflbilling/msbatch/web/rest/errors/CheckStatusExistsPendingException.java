package bo.com.luminia.sflbilling.msbatch.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class CheckStatusExistsPendingException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public CheckStatusExistsPendingException(String message) {
        super(null,
            ErrorKeys.ERR_CHECK_INVOICE_STILL_SYNC,
            Status.FORBIDDEN,
            message
        );
    }

}
