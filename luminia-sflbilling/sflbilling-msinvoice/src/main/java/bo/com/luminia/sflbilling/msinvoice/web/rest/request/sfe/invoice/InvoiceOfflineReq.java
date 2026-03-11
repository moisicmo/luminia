package bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
public class InvoiceOfflineReq {
    @NotNull
    private Integer significantEventSiatId;
    @NotNull
    private String description;
}
