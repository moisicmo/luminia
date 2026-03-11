package bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe;

import lombok.Data;

@Data
public class BranchOfficeRes {
    private Long id;
    private Integer branchOfficeSiatId;
    private String name;
    private String description;
    private Boolean active;
    private Long companyId;
}
