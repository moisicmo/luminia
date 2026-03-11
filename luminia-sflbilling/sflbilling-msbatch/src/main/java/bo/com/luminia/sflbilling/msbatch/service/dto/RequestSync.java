package bo.com.luminia.sflbilling.msbatch.service.dto;

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
    private String token;
}
