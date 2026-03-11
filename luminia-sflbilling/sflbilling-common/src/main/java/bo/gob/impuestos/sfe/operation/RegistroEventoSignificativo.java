
package bo.gob.impuestos.sfe.operation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para registroEventoSignificativo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="registroEventoSignificativo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudEventoSignificativo" type="{https://siat.impuestos.gob.bo/}solicitudEventoSignificativo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registroEventoSignificativo", propOrder = {
    "solicitudEventoSignificativo"
})
public class RegistroEventoSignificativo {

    @XmlElement(name = "SolicitudEventoSignificativo", required = true)
    protected SolicitudEventoSignificativo solicitudEventoSignificativo;

    /**
     * Obtiene el valor de la propiedad solicitudEventoSignificativo.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudEventoSignificativo }
     *     
     */
    public SolicitudEventoSignificativo getSolicitudEventoSignificativo() {
        return solicitudEventoSignificativo;
    }

    /**
     * Define el valor de la propiedad solicitudEventoSignificativo.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudEventoSignificativo }
     *     
     */
    public void setSolicitudEventoSignificativo(SolicitudEventoSignificativo value) {
        this.solicitudEventoSignificativo = value;
    }

}
