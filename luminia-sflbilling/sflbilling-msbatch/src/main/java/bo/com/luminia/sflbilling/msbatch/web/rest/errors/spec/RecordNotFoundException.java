package bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorKeys;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class RecordNotFoundException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public RecordNotFoundException() {
        super(null, ErrorKeys.ERR_RECORD_NOT_FOUND, Status.NOT_FOUND, "Registro no encontrado.");
    }

    public RecordNotFoundException(String message) {
        super(null, ErrorKeys.ERR_RECORD_NOT_FOUND, Status.NOT_FOUND, message);
    }
}
