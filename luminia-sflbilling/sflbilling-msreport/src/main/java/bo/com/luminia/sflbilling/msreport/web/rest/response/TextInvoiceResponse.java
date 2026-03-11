package bo.com.luminia.sflbilling.msreport.web.rest.response;

import bo.com.luminia.sflbilling.domain.response.BaseResponseSiat;

public class TextInvoiceResponse extends BaseResponseSiat {

    public TextInvoiceResponse(Integer code, String message) {
        super(code, message);
    }

    public TextInvoiceResponse(Integer code, String message, String body) {
        super(code, message);
        this.setBody(body);
    }

    public TextInvoiceResponse() {
    }
}
