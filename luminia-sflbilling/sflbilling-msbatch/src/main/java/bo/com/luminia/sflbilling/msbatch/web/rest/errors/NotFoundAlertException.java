package bo.com.luminia.sflbilling.msbatch.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class NotFoundAlertException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public NotFoundAlertException(String title, String detail) {
        super(null, title, Status.NOT_FOUND, detail);
    }

}
