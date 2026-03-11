
package bo.gob.impuestos.sfe.basicserviceinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para recepcionPaqueteFactura complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="recepcionPaqueteFactura">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudServicioRecepcionPaquete" type="{https://siat.impuestos.gob.bo/}solicitudRecepcionPaquete"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recepcionPaqueteFactura", propOrder = {
    "solicitudServicioRecepcionPaquete"
})
public class RecepcionPaqueteFactura {

    @XmlElement(name = "SolicitudServicioRecepcionPaquete", required = true)
    protected SolicitudRecepcionPaquete solicitudServicioRecepcionPaquete;

    /**
     * Obtiene el valor de la propiedad solicitudServicioRecepcionPaquete.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudRecepcionPaquete }
     *     
     */
    public SolicitudRecepcionPaquete getSolicitudServicioRecepcionPaquete() {
        return solicitudServicioRecepcionPaquete;
    }

    /**
     * Define el valor de la propiedad solicitudServicioRecepcionPaquete.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudRecepcionPaquete }
     *     
     */
    public void setSolicitudServicioRecepcionPaquete(SolicitudRecepcionPaquete value) {
        this.solicitudServicioRecepcionPaquete = value;
    }

}
