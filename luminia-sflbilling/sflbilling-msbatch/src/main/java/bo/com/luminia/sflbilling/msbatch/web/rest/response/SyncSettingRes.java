package bo.com.luminia.sflbilling.msbatch.web.rest.response;

import bo.com.luminia.sflbilling.domain.SyncSetting;
import lombok.Data;

@Data
public class SyncSettingRes {

    private Long id;
    private Integer syncType;
    private String cronDate;
    private String description;
    private Boolean active;
    private Long companyId;

    public SyncSettingRes() {
    }

    public SyncSettingRes(SyncSetting entity) {
        this.id = entity.getId();
        this.syncType = entity.getSyncType();
        this.cronDate = entity.getCronDate();
        this.description = entity.getDescription();
        this.active = entity.getActive();
    }
}
