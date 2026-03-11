
package bo.gob.impuestos.sfe.operation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para registroPuntoVenta complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="registroPuntoVenta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudRegistroPuntoVenta" type="{https://siat.impuestos.gob.bo/}solicitudRegistroPuntoVenta"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registroPuntoVenta", propOrder = {
    "solicitudRegistroPuntoVenta"
})
public class RegistroPuntoVenta {

    @XmlElement(name = "SolicitudRegistroPuntoVenta", required = true)
    protected SolicitudRegistroPuntoVenta solicitudRegistroPuntoVenta;

    /**
     * Obtiene el valor de la propiedad solicitudRegistroPuntoVenta.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudRegistroPuntoVenta }
     *     
     */
    public SolicitudRegistroPuntoVenta getSolicitudRegistroPuntoVenta() {
        return solicitudRegistroPuntoVenta;
    }

    /**
     * Define el valor de la propiedad solicitudRegistroPuntoVenta.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudRegistroPuntoVenta }
     *     
     */
    public void setSolicitudRegistroPuntoVenta(SolicitudRegistroPuntoVenta value) {
        this.solicitudRegistroPuntoVenta = value;
    }

}
