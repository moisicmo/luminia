package bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PointSaleUpdateReq {
    @NotNull
    private Long id;
    @NotNull
    @Size(max = 50)
    private String name;
    @NotNull
    @Size(max = 255)
    private String description;
    @NotNull
    private Boolean active;
}
