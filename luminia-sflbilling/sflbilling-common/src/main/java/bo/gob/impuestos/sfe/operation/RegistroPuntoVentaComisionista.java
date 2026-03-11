
package bo.gob.impuestos.sfe.operation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para registroPuntoVentaComisionista complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="registroPuntoVentaComisionista">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudPuntoVentaComisionista" type="{https://siat.impuestos.gob.bo/}solicitudPuntoVentaComisionista"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registroPuntoVentaComisionista", propOrder = {
    "solicitudPuntoVentaComisionista"
})
public class RegistroPuntoVentaComisionista {

    @XmlElement(name = "SolicitudPuntoVentaComisionista", required = true)
    protected SolicitudPuntoVentaComisionista solicitudPuntoVentaComisionista;

    /**
     * Obtiene el valor de la propiedad solicitudPuntoVentaComisionista.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudPuntoVentaComisionista }
     *     
     */
    public SolicitudPuntoVentaComisionista getSolicitudPuntoVentaComisionista() {
        return solicitudPuntoVentaComisionista;
    }

    /**
     * Define el valor de la propiedad solicitudPuntoVentaComisionista.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudPuntoVentaComisionista }
     *     
     */
    public void setSolicitudPuntoVentaComisionista(SolicitudPuntoVentaComisionista value) {
        this.solicitudPuntoVentaComisionista = value;
    }

}
