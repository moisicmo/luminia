package bo.com.luminia.sflbilling.msbatch.web.rest.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
public class ScheduleSettingUpdateReq {
    @NotNull
    private Long id;
    @NotNull
    private String cronDate;
    @NotNull
    private String description;
    @NotNull
    private Boolean active;
    @NotNull
    private Long companyId;
}
