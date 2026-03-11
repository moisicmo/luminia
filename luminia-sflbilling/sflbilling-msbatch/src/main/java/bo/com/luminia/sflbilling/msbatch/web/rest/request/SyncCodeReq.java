package bo.com.luminia.sflbilling.msbatch.web.rest.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Getter
@Setter
public class SyncCodeReq {
    @NotNull
    @ApiModelProperty(notes = "ID de la empresa")
    private Long companyId;
}
