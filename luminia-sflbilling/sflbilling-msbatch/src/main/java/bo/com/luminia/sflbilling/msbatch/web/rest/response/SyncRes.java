package bo.com.luminia.sflbilling.msbatch.web.rest.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SyncRes {

    @ApiModelProperty(notes = "Respuesta de la sincronización")
    private Boolean response;
    @ApiModelProperty(notes = "Mensaje del resultado de sincronización")
    private String message;

    public SyncRes(Boolean response, String message) {
        this.response = response;
        this.message = message;
    }
}
