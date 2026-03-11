package bo.com.luminia.sflbilling.msinvoice.domain.sync.siat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestSync {
    private Integer companyId;
    private Long nit;
    private String systemCode;
    private Integer environmentSiat;
    private Integer pointSaleSiat;
    private Integer branchOfficeSiat;
    private String cuis;
}
