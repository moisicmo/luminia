package bo.com.luminia.sflbilling.msbatch.web.rest.certification.request;

import javax.validation.constraints.NotNull;

public class InvoiceIssueManualReq extends InvoiceIssueReq {
    @NotNull
    private String cafc;

    public String getCafc() {
        return cafc;
    }

    public void setCafc(String cafc) {
        this.cafc = cafc;
    }
}
