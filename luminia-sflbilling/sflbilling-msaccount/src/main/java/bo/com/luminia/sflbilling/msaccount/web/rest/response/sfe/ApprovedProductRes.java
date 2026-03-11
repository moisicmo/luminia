package bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe;

import lombok.Data;

@Data
public class ApprovedProductRes {

    private Long id;
    private String productCode;
    private String description;
    private Long company;
    private Long productService;
    private String productServiceName;
    private Long measurementUnit;
    private String measurementUnitName;
}
