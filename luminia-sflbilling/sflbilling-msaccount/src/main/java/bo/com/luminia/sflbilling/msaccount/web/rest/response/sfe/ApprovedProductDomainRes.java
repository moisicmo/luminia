package bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe;

import lombok.Data;

@Data
public class ApprovedProductDomainRes {

    private Long id;
    private String productCode;
    private String description;
    private Integer productService;
    private String productServiceName;
    private Integer measurementUnit;
    private String measurementUnitName;
}
