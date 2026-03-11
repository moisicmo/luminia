package bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PointSaleCloseReq {
    @NotNull
    private Long id;
}
