package bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Setter
@Getter
public class InvoiceBatchReq {
    @NotNull
    private String businessCode;
    private List<InvoiceIssueReq> invoices;
}
