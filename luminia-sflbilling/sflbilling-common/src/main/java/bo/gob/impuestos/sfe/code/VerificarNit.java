
package bo.gob.impuestos.sfe.code;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para verificarNit complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="verificarNit">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudVerificarNit" type="{https://siat.impuestos.gob.bo/}solicitudVerificarNit"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verificarNit", propOrder = {
    "solicitudVerificarNit"
})
public class VerificarNit {

    @XmlElement(name = "SolicitudVerificarNit", required = true)
    protected SolicitudVerificarNit solicitudVerificarNit;

    /**
     * Obtiene el valor de la propiedad solicitudVerificarNit.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudVerificarNit }
     *     
     */
    public SolicitudVerificarNit getSolicitudVerificarNit() {
        return solicitudVerificarNit;
    }

    /**
     * Define el valor de la propiedad solicitudVerificarNit.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudVerificarNit }
     *     
     */
    public void setSolicitudVerificarNit(SolicitudVerificarNit value) {
        this.solicitudVerificarNit = value;
    }

}
