package bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ActivityCreateReq {

    @NotBlank
    private Integer siatId;

    @NotBlank
    @Size(min = 1, max = 255)
    private String description;

    @NotBlank
    @Size(min = 1, max = 10)
    private String activityType;

    @NotNull
    private Integer companyId;

    @NotNull
    private Boolean active;
}
