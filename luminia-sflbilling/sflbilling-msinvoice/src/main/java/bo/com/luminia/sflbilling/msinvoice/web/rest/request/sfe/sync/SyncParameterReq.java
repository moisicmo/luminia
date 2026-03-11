package bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.sync;

import lombok.Data;

@Data
public class SyncParameterReq {

    private Long companyId;
    private Integer branchOfficeSiat;
    private Integer pointSaleSiat;
}
