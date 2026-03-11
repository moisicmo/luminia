package bo.com.luminia.sflbilling.msbatch.web.rest.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
public class ScheduleSettingCreateReq {
    @NotNull
    private String cronDate;
    @NotNull
    private String description;
    @NotNull
    private Long companyId;
}
