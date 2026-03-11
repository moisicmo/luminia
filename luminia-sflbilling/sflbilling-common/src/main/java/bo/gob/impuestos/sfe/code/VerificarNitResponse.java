
package bo.gob.impuestos.sfe.code;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para verificarNitResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="verificarNitResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaVerificarNit" type="{https://siat.impuestos.gob.bo/}respuestaVerificarNit" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verificarNitResponse", propOrder = {
    "respuestaVerificarNit"
})
public class VerificarNitResponse {

    @XmlElement(name = "RespuestaVerificarNit")
    protected RespuestaVerificarNit respuestaVerificarNit;

    /**
     * Obtiene el valor de la propiedad respuestaVerificarNit.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaVerificarNit }
     *     
     */
    public RespuestaVerificarNit getRespuestaVerificarNit() {
        return respuestaVerificarNit;
    }

    /**
     * Define el valor de la propiedad respuestaVerificarNit.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaVerificarNit }
     *     
     */
    public void setRespuestaVerificarNit(RespuestaVerificarNit value) {
        this.respuestaVerificarNit = value;
    }

}
