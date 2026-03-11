
package bo.gob.impuestos.sfe.code;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cuis complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cuis">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudCuis" type="{https://siat.impuestos.gob.bo/}solicitudCuis"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cuis", propOrder = {
    "solicitudCuis"
})
public class Cuis {

    @XmlElement(name = "SolicitudCuis", required = true)
    protected SolicitudCuis solicitudCuis;

    /**
     * Obtiene el valor de la propiedad solicitudCuis.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudCuis }
     *     
     */
    public SolicitudCuis getSolicitudCuis() {
        return solicitudCuis;
    }

    /**
     * Define el valor de la propiedad solicitudCuis.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudCuis }
     *     
     */
    public void setSolicitudCuis(SolicitudCuis value) {
        this.solicitudCuis = value;
    }

}
