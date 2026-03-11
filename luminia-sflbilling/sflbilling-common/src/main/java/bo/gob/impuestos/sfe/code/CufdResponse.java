
package bo.gob.impuestos.sfe.code;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cufdResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cufdResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaCufd" type="{https://siat.impuestos.gob.bo/}respuestaCufd" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cufdResponse", propOrder = {
    "respuestaCufd"
})
public class CufdResponse {

    @XmlElement(name = "RespuestaCufd")
    protected RespuestaCufd respuestaCufd;

    /**
     * Obtiene el valor de la propiedad respuestaCufd.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaCufd }
     *     
     */
    public RespuestaCufd getRespuestaCufd() {
        return respuestaCufd;
    }

    /**
     * Define el valor de la propiedad respuestaCufd.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaCufd }
     *     
     */
    public void setRespuestaCufd(RespuestaCufd value) {
        this.respuestaCufd = value;
    }

}
