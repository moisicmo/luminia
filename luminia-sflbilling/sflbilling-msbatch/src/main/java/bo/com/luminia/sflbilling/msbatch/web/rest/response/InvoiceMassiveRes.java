package bo.com.luminia.sflbilling.msbatch.web.rest.response;

import bo.com.luminia.sflbilling.domain.response.BaseResponseSiat;

public class InvoiceMassiveRes extends BaseResponseSiat {

    public InvoiceMassiveRes(Integer code, String message, Object body) {
        super(code, message);
        this.body = body;
    }

    public InvoiceMassiveRes(Integer code, String message) {
        super(code, message);
    }

    public InvoiceMassiveRes() {
    }
}
