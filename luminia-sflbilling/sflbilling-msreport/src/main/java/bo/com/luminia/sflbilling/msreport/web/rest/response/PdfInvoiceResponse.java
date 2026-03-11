package bo.com.luminia.sflbilling.msreport.web.rest.response;

import bo.com.luminia.sflbilling.domain.response.BaseResponseSiat;

public class PdfInvoiceResponse extends BaseResponseSiat {

    public PdfInvoiceResponse(Integer code, String message) {
        super(code, message);
    }

    public PdfInvoiceResponse(Integer code, String message, String body) {
        super(code, message);
        this.setBody(body);
    }

    public PdfInvoiceResponse() {
    }
}
