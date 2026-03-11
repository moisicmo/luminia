
package bo.gob.impuestos.sfe.electronicinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anulacionFactura complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="anulacionFactura">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudServicioAnulacionFactura" type="{https://siat.impuestos.gob.bo/}solicitudAnulacion"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "anulacionFactura", propOrder = {
    "solicitudServicioAnulacionFactura"
})
public class AnulacionFactura {

    @XmlElement(name = "SolicitudServicioAnulacionFactura", required = true)
    protected SolicitudAnulacion solicitudServicioAnulacionFactura;

    /**
     * Obtiene el valor de la propiedad solicitudServicioAnulacionFactura.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudAnulacion }
     *     
     */
    public SolicitudAnulacion getSolicitudServicioAnulacionFactura() {
        return solicitudServicioAnulacionFactura;
    }

    /**
     * Define el valor de la propiedad solicitudServicioAnulacionFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudAnulacion }
     *     
     */
    public void setSolicitudServicioAnulacionFactura(SolicitudAnulacion value) {
        this.solicitudServicioAnulacionFactura = value;
    }

}
