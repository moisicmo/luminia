package bo.com.luminia.sflbilling.msreport.web.rest.response;

import bo.com.luminia.sflbilling.domain.response.BaseResponseSiat;

public class InvoiceEmailRes extends BaseResponseSiat {

    public InvoiceEmailRes(Integer code, String message) {
        super(code, message);
    }

    public InvoiceEmailRes(Integer code, String message, String body) {
        super(code, message);
        this.setBody(body);
    }

    public InvoiceEmailRes() {
    }
}
