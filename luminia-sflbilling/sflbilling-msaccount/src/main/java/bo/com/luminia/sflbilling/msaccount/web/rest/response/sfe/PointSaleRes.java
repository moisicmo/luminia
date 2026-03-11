package bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe;

import lombok.Data;

@Data
public class PointSaleRes {
    private Long id;
    private Integer pointSaleSiatId;
    private String name;
    private String description;
    private Long pointSaleTypeId;
    private String pointSaleType;
    private Boolean active;
    private Long BranchOfficeId;
}
