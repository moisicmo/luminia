package bo.com.luminia.sflbilling.msbatch.web.rest.response.siat;

import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.gob.impuestos.sfe.buysellinvoice.RespuestaRecepcion;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class SiatBuySellInvoiceStatusVerificationRes {

    private Integer code;

    //private Integer statusCode;

    private String description;

    private Boolean transaction;

    private List<SiatMessage> messages;

    public SiatBuySellInvoiceStatusVerificationRes(RespuestaRecepcion respuestaRecepcion, boolean isSiatOffline) {
        if (respuestaRecepcion == null) {
            this.code = 500;
            //this.statusCode = 500;
            this.description = "No se pudo consultar estado de la factura al SIAT.";
            if (isSiatOffline)
                this.description += " Sistema fuera de línea";
            this.transaction = false;
            this.messages = null;

        } else {
            this.code = SiatResponseCodes.SUCCESS;
            //this.statusCode = respuestaRecepcion.getCodigoEstado();
            this.description = respuestaRecepcion.getCodigoDescripcion();
            this.transaction = respuestaRecepcion.isTransaccion();
            this.messages = respuestaRecepcion.getMensajesList().stream().map(SiatMessage::new).collect(Collectors.toList());
        }
    }
}
