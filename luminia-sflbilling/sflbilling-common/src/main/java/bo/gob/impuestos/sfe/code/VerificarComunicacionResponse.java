
package bo.gob.impuestos.sfe.code;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para verificarComunicacionResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="verificarComunicacionResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaComunicacion" type="{https://siat.impuestos.gob.bo/}respuestaComunicacion" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verificarComunicacionResponse", propOrder = {
    "respuestaComunicacion"
})
public class VerificarComunicacionResponse {

    @XmlElement(name = "RespuestaComunicacion")
    protected RespuestaComunicacion respuestaComunicacion;

    /**
     * Obtiene el valor de la propiedad respuestaComunicacion.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaComunicacion }
     *     
     */
    public RespuestaComunicacion getRespuestaComunicacion() {
        return respuestaComunicacion;
    }

    /**
     * Define el valor de la propiedad respuestaComunicacion.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaComunicacion }
     *     
     */
    public void setRespuestaComunicacion(RespuestaComunicacion value) {
        this.respuestaComunicacion = value;
    }

}
