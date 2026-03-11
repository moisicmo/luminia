package bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec;

import bo.com.luminia.sflbilling.msbatch.web.rest.errors.BadRequestAlertException;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorKeys;

public class InvoiceCannotBeProccesed extends BadRequestAlertException {

    public InvoiceCannotBeProccesed(Integer code, String message) {
        super(ErrorKeys.ERR_SIAT_EXCEPTION, message);
        this.code = code;
    }

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
