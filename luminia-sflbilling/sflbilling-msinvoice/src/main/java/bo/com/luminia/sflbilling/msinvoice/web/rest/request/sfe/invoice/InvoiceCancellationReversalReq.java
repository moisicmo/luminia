package bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Getter
@Setter
public class InvoiceCancellationReversalReq {
    @NotNull
    private String cuf;
    private String email;
}
