package bo.com.luminia.sflbilling.msbatch.web.rest.response;

import bo.com.luminia.sflbilling.domain.ScheduleSetting;
import lombok.Data;

@Data
public class ScheduleSettingRes {

    private Long id;
    private String cronDate;
    private String description;
    private Boolean active;
    private Long companyId;

    public ScheduleSettingRes() {
    }

    public ScheduleSettingRes(ScheduleSetting entity) {
        this.id = entity.getId();
        this.cronDate = entity.getCronDate();
        this.description = entity.getDescription();
        this.active = entity.getActive();
    }
}
