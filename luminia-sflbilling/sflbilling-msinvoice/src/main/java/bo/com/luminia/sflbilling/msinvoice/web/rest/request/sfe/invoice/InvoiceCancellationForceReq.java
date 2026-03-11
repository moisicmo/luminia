package bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Getter
@Setter
@Deprecated
public class InvoiceCancellationForceReq {
    @NotNull
    private String cuf;
    @NotNull
    private Integer cancellationReasonSiat;
    private String email;
    @NotNull
    private Long companyId;
}
