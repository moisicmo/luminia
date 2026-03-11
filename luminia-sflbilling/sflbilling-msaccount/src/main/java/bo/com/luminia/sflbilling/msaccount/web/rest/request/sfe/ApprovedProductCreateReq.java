package bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe;

import lombok.Data;

@Data
public class ApprovedProductCreateReq {
    private String productCode;
    private String description;
    private Long companyId;
    private Long productServiceId;
    private Long measurementUnitId;
}
