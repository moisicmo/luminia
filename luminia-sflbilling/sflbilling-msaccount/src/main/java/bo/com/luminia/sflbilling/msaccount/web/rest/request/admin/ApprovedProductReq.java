package bo.com.luminia.sflbilling.msaccount.web.rest.request.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ApprovedProductReq {
    private Long id;
    @NotNull
    @Size(max = 20)
    private String productCode;
    @NotNull
    @Size(max = 255)
    private String description;
    @NotNull
    private Long companyId;
    @NotNull
    private Long productServiceId;
    @NotNull
    private Long measurementUnitId;
}
