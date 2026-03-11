
package bo.gob.impuestos.sfe.code;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cufdMasivoResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cufdMasivoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaCufdMasivo" type="{https://siat.impuestos.gob.bo/}respuestaCufdMasivo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cufdMasivoResponse", propOrder = {
    "respuestaCufdMasivo"
})
public class CufdMasivoResponse {

    @XmlElement(name = "RespuestaCufdMasivo")
    protected RespuestaCufdMasivo respuestaCufdMasivo;

    /**
     * Obtiene el valor de la propiedad respuestaCufdMasivo.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaCufdMasivo }
     *     
     */
    public RespuestaCufdMasivo getRespuestaCufdMasivo() {
        return respuestaCufdMasivo;
    }

    /**
     * Define el valor de la propiedad respuestaCufdMasivo.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaCufdMasivo }
     *     
     */
    public void setRespuestaCufdMasivo(RespuestaCufdMasivo value) {
        this.respuestaCufdMasivo = value;
    }

}
