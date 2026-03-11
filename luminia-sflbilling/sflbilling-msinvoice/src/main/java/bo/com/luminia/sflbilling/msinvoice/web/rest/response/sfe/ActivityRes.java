package bo.com.luminia.sflbilling.msinvoice.web.rest.response.sfe;

import bo.com.luminia.sflbilling.domain.Activity;
import lombok.Data;

@Data
public class ActivityRes {
    private Long id;
    private Integer siatId;
    private String description;
    private String activityType;
    private Integer companyId;
    private Boolean active;

    public ActivityRes(Activity entity) {
        this.id = entity.getId();
        this.active = entity.getActive();
        this.activityType = entity.getActivityType();
        this.companyId = entity.getCompanyId();
        this.description = entity.getDescription();
        this.siatId = entity.getSiatId();
    }
}
