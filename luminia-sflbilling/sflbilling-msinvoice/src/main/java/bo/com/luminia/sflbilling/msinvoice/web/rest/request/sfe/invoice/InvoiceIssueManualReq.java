package bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class InvoiceIssueManualReq extends InvoiceIssueReq {
    @NotNull
    private String cafc;
}
