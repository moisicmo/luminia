
package bo.gob.impuestos.sfe.basicserviceinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para verificacionEstadoFactura complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="verificacionEstadoFactura">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudServicioVerificacionEstadoFactura" type="{https://siat.impuestos.gob.bo/}solicitudVerificacionEstado"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verificacionEstadoFactura", propOrder = {
    "solicitudServicioVerificacionEstadoFactura"
})
public class VerificacionEstadoFactura {

    @XmlElement(name = "SolicitudServicioVerificacionEstadoFactura", required = true)
    protected SolicitudVerificacionEstado solicitudServicioVerificacionEstadoFactura;

    /**
     * Obtiene el valor de la propiedad solicitudServicioVerificacionEstadoFactura.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudVerificacionEstado }
     *     
     */
    public SolicitudVerificacionEstado getSolicitudServicioVerificacionEstadoFactura() {
        return solicitudServicioVerificacionEstadoFactura;
    }

    /**
     * Define el valor de la propiedad solicitudServicioVerificacionEstadoFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudVerificacionEstado }
     *     
     */
    public void setSolicitudServicioVerificacionEstadoFactura(SolicitudVerificacionEstado value) {
        this.solicitudServicioVerificacionEstadoFactura = value;
    }

}
