package bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe;

import lombok.Data;

@Data
public class ApprovedProductExternalRes {
    private Long id;
    private String productCode;
    private String description;
    private Integer productService;
    private Integer measurementUnit;

    public ApprovedProductExternalRes(Long id, String productCode, String description, Integer productService, Integer measurementUnit) {
        this.id = id;
        this.productCode = productCode;
        this.description = description;
        this.productService = productService;
        this.measurementUnit = measurementUnit;
    }
}
