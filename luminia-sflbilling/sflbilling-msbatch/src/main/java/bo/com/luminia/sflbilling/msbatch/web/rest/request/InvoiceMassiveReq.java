package bo.com.luminia.sflbilling.msbatch.web.rest.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InvoiceMassiveReq {
    @NotNull
    private Long companyId;

    private Long scheduleSettingId;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getScheduleSettingId() {
        return scheduleSettingId;
    }

    public void setScheduleSettingId(Long scheduleSettingId) {
        this.scheduleSettingId = scheduleSettingId;
    }
}
