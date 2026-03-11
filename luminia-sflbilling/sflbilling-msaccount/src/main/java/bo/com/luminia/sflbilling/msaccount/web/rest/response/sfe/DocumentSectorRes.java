package bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe;

import bo.com.luminia.sflbilling.domain.DocumentSector;
import lombok.Data;

@Data
public class DocumentSectorRes {
    private Long id;
    private Integer siatId;
    private Integer activityCode;
    private String documentSectorType;
    private Integer companyId;
    private Boolean active;

    public DocumentSectorRes(DocumentSector entity) {
        this.id = entity.getId();
        this.active = entity.getActive();
        this.activityCode = entity.getActivityCode();
        this.companyId = entity.getCompanyId();
        this.documentSectorType = entity.getDocumentSectorType();
        this.siatId = entity.getSiatId();
    }
}
