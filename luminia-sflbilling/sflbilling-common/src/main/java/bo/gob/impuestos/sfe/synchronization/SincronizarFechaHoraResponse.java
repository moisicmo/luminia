
package bo.gob.impuestos.sfe.synchronization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para sincronizarFechaHoraResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="sincronizarFechaHoraResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaFechaHora" type="{https://siat.impuestos.gob.bo/}respuestaFechaHora" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sincronizarFechaHoraResponse", propOrder = {
    "respuestaFechaHora"
})
public class SincronizarFechaHoraResponse {

    @XmlElement(name = "RespuestaFechaHora")
    protected RespuestaFechaHora respuestaFechaHora;

    /**
     * Obtiene el valor de la propiedad respuestaFechaHora.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaFechaHora }
     *     
     */
    public RespuestaFechaHora getRespuestaFechaHora() {
        return respuestaFechaHora;
    }

    /**
     * Define el valor de la propiedad respuestaFechaHora.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaFechaHora }
     *     
     */
    public void setRespuestaFechaHora(RespuestaFechaHora value) {
        this.respuestaFechaHora = value;
    }

}
