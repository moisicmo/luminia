
package bo.gob.impuestos.sfe.operation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cierrePuntoVenta complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cierrePuntoVenta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudCierrePuntoVenta" type="{https://siat.impuestos.gob.bo/}solicitudCierrePuntoVenta"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cierrePuntoVenta", propOrder = {
    "solicitudCierrePuntoVenta"
})
public class CierrePuntoVenta {

    @XmlElement(name = "SolicitudCierrePuntoVenta", required = true)
    protected SolicitudCierrePuntoVenta solicitudCierrePuntoVenta;

    /**
     * Obtiene el valor de la propiedad solicitudCierrePuntoVenta.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudCierrePuntoVenta }
     *     
     */
    public SolicitudCierrePuntoVenta getSolicitudCierrePuntoVenta() {
        return solicitudCierrePuntoVenta;
    }

    /**
     * Define el valor de la propiedad solicitudCierrePuntoVenta.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudCierrePuntoVenta }
     *     
     */
    public void setSolicitudCierrePuntoVenta(SolicitudCierrePuntoVenta value) {
        this.solicitudCierrePuntoVenta = value;
    }

}
