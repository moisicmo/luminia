package bo.com.luminia.sflbilling.msbatch.web.rest.response.siat;

import bo.gob.impuestos.sfe.buysellinvoice.MensajeRecepcion;
import lombok.Data;

@Data
public class SiatMessage {

    private String description;

    private Integer code;

    public SiatMessage(MensajeRecepcion entity) {
        this.description = entity.getDescripcion();
        this.code = entity.getCodigo();
    }
}
