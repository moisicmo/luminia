
package bo.gob.impuestos.sfe.operation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para consultaPuntoVenta complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="consultaPuntoVenta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudConsultaPuntoVenta" type="{https://siat.impuestos.gob.bo/}solicitudConsultaPuntoVenta"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaPuntoVenta", propOrder = {
    "solicitudConsultaPuntoVenta"
})
public class ConsultaPuntoVenta {

    @XmlElement(name = "SolicitudConsultaPuntoVenta", required = true)
    protected SolicitudConsultaPuntoVenta solicitudConsultaPuntoVenta;

    /**
     * Obtiene el valor de la propiedad solicitudConsultaPuntoVenta.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudConsultaPuntoVenta }
     *     
     */
    public SolicitudConsultaPuntoVenta getSolicitudConsultaPuntoVenta() {
        return solicitudConsultaPuntoVenta;
    }

    /**
     * Define el valor de la propiedad solicitudConsultaPuntoVenta.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudConsultaPuntoVenta }
     *     
     */
    public void setSolicitudConsultaPuntoVenta(SolicitudConsultaPuntoVenta value) {
        this.solicitudConsultaPuntoVenta = value;
    }

}
