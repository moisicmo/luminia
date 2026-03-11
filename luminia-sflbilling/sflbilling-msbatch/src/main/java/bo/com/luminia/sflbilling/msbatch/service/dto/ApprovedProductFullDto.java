package bo.com.luminia.sflbilling.msbatch.service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApprovedProductFullDto {

    private String productCode;
    private String description;
    private Integer activityCodeSiat;
    private Integer productCodeSiat;
    private Integer measurementUnitSiat;
}
