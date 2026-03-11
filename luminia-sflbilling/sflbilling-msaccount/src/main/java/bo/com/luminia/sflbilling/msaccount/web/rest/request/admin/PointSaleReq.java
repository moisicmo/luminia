package bo.com.luminia.sflbilling.msaccount.web.rest.request.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PointSaleReq {
    private Long id;
    @NotNull
    @Size(max = 50)
    private String name;
    @NotNull
    @Size(max = 255)
    private String description;
    private Integer pointSaleSiatId;
    private Long pointSaleTypeId;
    @NotNull
    private Boolean active;
    @NotNull
    private Long branchOfficeId;
}
