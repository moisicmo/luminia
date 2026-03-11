
package bo.gob.impuestos.sfe.code;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para notificaCertificadoRevocadoResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="notificaCertificadoRevocadoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaNotificaRevocado" type="{https://siat.impuestos.gob.bo/}respuestaNotificaRevocado" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notificaCertificadoRevocadoResponse", propOrder = {
    "respuestaNotificaRevocado"
})
public class NotificaCertificadoRevocadoResponse {

    @XmlElement(name = "RespuestaNotificaRevocado")
    protected RespuestaNotificaRevocado respuestaNotificaRevocado;

    /**
     * Obtiene el valor de la propiedad respuestaNotificaRevocado.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaNotificaRevocado }
     *     
     */
    public RespuestaNotificaRevocado getRespuestaNotificaRevocado() {
        return respuestaNotificaRevocado;
    }

    /**
     * Define el valor de la propiedad respuestaNotificaRevocado.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaNotificaRevocado }
     *     
     */
    public void setRespuestaNotificaRevocado(RespuestaNotificaRevocado value) {
        this.respuestaNotificaRevocado = value;
    }

}
