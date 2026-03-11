
package bo.gob.impuestos.sfe.computerizedinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para solicitudAnulacion complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="solicitudAnulacion">
 *   &lt;complexContent>
 *     &lt;extension base="{https://siat.impuestos.gob.bo/}solicitudRecepcion">
 *       &lt;sequence>
 *         &lt;element name="codigoMotivo" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "solicitudAnulacion", propOrder = {
    "codigoMotivo",
    "cuf"
})
public class SolicitudAnulacion
    extends SolicitudRecepcion
{

    protected int codigoMotivo;
    @XmlElement(required = true)
    protected String cuf;

    /**
     * Obtiene el valor de la propiedad codigoMotivo.
     * 
     */
    public int getCodigoMotivo() {
        return codigoMotivo;
    }

    /**
     * Define el valor de la propiedad codigoMotivo.
     * 
     */
    public void setCodigoMotivo(int value) {
        this.codigoMotivo = value;
    }

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
