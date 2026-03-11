
package bo.gob.impuestos.sfe.operation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cierreOperacionesSistemaResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cierreOperacionesSistemaResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaCierreSistemas" type="{https://siat.impuestos.gob.bo/}respuestaCierreSistemas" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cierreOperacionesSistemaResponse", propOrder = {
    "respuestaCierreSistemas"
})
public class CierreOperacionesSistemaResponse {

    @XmlElement(name = "RespuestaCierreSistemas")
    protected RespuestaCierreSistemas respuestaCierreSistemas;

    /**
     * Obtiene el valor de la propiedad respuestaCierreSistemas.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaCierreSistemas }
     *     
     */
    public RespuestaCierreSistemas getRespuestaCierreSistemas() {
        return respuestaCierreSistemas;
    }

    /**
     * Define el valor de la propiedad respuestaCierreSistemas.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaCierreSistemas }
     *     
     */
    public void setRespuestaCierreSistemas(RespuestaCierreSistemas value) {
        this.respuestaCierreSistemas = value;
    }

}
