package bo.com.luminia.sflbilling.msinvoice.web.rest.response.sfe.invoice;

import bo.com.luminia.sflbilling.domain.response.BaseResponseSiat;

public class InvoiceIssueRes extends BaseResponseSiat {


    public InvoiceIssueRes(Integer code, String message) {
        super(code, message);
    }

    public InvoiceIssueRes(Integer code, String message, String body) {
        super(code, message);
        this.setBody(body);
    }

    public InvoiceIssueRes(Integer code, String message, Object body) {
        super(code, message);
        this.setBody(body);
    }

    public InvoiceIssueRes() {
    }
}
