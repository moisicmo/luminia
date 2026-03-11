package bo.com.luminia.sflbilling.msbatch.web.rest.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
public class InvoiceOnlineManualReq {
    @NotNull
    private String businessCode;
}
