
package bo.gob.impuestos.sfe.code;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para notificaCertificadoRevocado complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="notificaCertificadoRevocado">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudNotificaRevocado" type="{https://siat.impuestos.gob.bo/}solicitudNotifcaRevocado"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notificaCertificadoRevocado", propOrder = {
    "solicitudNotificaRevocado"
})
public class NotificaCertificadoRevocado {

    @XmlElement(name = "SolicitudNotificaRevocado", required = true)
    protected SolicitudNotifcaRevocado solicitudNotificaRevocado;

    /**
     * Obtiene el valor de la propiedad solicitudNotificaRevocado.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudNotifcaRevocado }
     *     
     */
    public SolicitudNotifcaRevocado getSolicitudNotificaRevocado() {
        return solicitudNotificaRevocado;
    }

    /**
     * Define el valor de la propiedad solicitudNotificaRevocado.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudNotifcaRevocado }
     *     
     */
    public void setSolicitudNotificaRevocado(SolicitudNotifcaRevocado value) {
        this.solicitudNotificaRevocado = value;
    }

}
