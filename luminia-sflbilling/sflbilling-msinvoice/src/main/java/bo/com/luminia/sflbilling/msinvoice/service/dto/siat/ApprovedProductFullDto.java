package bo.com.luminia.sflbilling.msinvoice.service.dto.siat;

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
