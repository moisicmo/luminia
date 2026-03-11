package bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe;

import lombok.Data;

@Data
public class ApprovedProductCreateExternalReq {
    private String productCode;
    private String description;
    private Integer productService;
    private Integer measurementUnit;
}
