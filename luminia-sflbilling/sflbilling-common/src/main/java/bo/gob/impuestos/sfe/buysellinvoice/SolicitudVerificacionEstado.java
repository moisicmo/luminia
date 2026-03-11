
package bo.gob.impuestos.sfe.buysellinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para solicitudVerificacionEstado complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="solicitudVerificacionEstado">
 *   &lt;complexContent>
 *     &lt;extension base="{https://siat.impuestos.gob.bo/}solicitudRecepcion">
 *       &lt;sequence>
 *         &lt;element name="cuf" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "solicitudVerificacionEstado", propOrder = {
    "cuf"
})
public class SolicitudVerificacionEstado
    extends SolicitudRecepcion
{

    @XmlElement(required = true)
    protected String cuf;

    /**
     * Obtiene el valor de la propiedad cuf.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCuf() {
        return cuf;
    }

    /**
     * Define el valor de la propiedad cuf.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCuf(String value) {
        this.cuf = value;
    }

}
