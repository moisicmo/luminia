
package bo.gob.impuestos.sfe.operation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para registroEventoSignificativoResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="registroEventoSignificativoResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaListaEventos" type="{https://siat.impuestos.gob.bo/}respuestaListaEventos" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registroEventoSignificativoResponse", propOrder = {
    "respuestaListaEventos"
})
public class RegistroEventoSignificativoResponse {

    @XmlElement(name = "RespuestaListaEventos")
    protected RespuestaListaEventos respuestaListaEventos;

    /**
     * Obtiene el valor de la propiedad respuestaListaEventos.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaListaEventos }
     *     
     */
    public RespuestaListaEventos getRespuestaListaEventos() {
        return respuestaListaEventos;
    }

    /**
     * Define el valor de la propiedad respuestaListaEventos.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaListaEventos }
     *     
     */
    public void setRespuestaListaEventos(RespuestaListaEventos value) {
        this.respuestaListaEventos = value;
    }

}
