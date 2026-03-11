package bo.com.luminia.sflbilling.msbatch.web.rest.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
public class SyncParameterReq {
    @NotNull
    @ApiModelProperty(notes = "ID de la empresa")
    private Long companyId;
    @NotNull
    @ApiModelProperty(notes = "ID de la sucursal del SIAT")
    private Integer branchOfficeSiat;
    @NotNull
    @ApiModelProperty(notes = "ID del punto de venta del SIAT")
    private Integer pointSaleSiat;
}
