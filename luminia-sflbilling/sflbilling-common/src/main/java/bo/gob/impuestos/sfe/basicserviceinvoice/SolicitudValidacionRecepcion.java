
package bo.gob.impuestos.sfe.basicserviceinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para solicitudValidacionRecepcion complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="solicitudValidacionRecepcion">
 *   &lt;complexContent>
 *     &lt;extension base="{https://siat.impuestos.gob.bo/}solicitudRecepcion">
 *       &lt;sequence>
 *         &lt;element name="codigoRecepcion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "solicitudValidacionRecepcion", propOrder = {
    "codigoRecepcion"
})
public class SolicitudValidacionRecepcion
    extends SolicitudRecepcion
{

    @XmlElement(required = true)
    protected String codigoRecepcion;

    /**
     * Obtiene el valor de la propiedad codigoRecepcion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoRecepcion() {
        return codigoRecepcion;
    }

    /**
     * Define el valor de la propiedad codigoRecepcion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoRecepcion(String value) {
        this.codigoRecepcion = value;
    }

}
