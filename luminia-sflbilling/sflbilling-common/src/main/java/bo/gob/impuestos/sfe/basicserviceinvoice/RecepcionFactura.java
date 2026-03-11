
package bo.gob.impuestos.sfe.basicserviceinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para recepcionFactura complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="recepcionFactura">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudServicioRecepcionFactura" type="{https://siat.impuestos.gob.bo/}solicitudRecepcionFactura"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recepcionFactura", propOrder = {
    "solicitudServicioRecepcionFactura"
})
public class RecepcionFactura {

    @XmlElement(name = "SolicitudServicioRecepcionFactura", required = true)
    protected SolicitudRecepcionFactura solicitudServicioRecepcionFactura;

    /**
     * Obtiene el valor de la propiedad solicitudServicioRecepcionFactura.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudRecepcionFactura }
     *     
     */
    public SolicitudRecepcionFactura getSolicitudServicioRecepcionFactura() {
        return solicitudServicioRecepcionFactura;
    }

    /**
     * Define el valor de la propiedad solicitudServicioRecepcionFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudRecepcionFactura }
     *     
     */
    public void setSolicitudServicioRecepcionFactura(SolicitudRecepcionFactura value) {
        this.solicitudServicioRecepcionFactura = value;
    }

}
