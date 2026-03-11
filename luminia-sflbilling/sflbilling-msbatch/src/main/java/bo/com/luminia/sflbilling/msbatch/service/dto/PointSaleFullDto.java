package bo.com.luminia.sflbilling.msbatch.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointSaleFullDto {
    private Integer branchOfficeSiatId;
    private String nameBranchOffice;
    private Integer pointSaleSiatId;
    private String namePointSale;
}
