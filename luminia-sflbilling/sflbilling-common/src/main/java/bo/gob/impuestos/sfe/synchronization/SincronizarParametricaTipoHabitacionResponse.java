
package bo.gob.impuestos.sfe.synchronization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para sincronizarParametricaTipoHabitacionResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="sincronizarParametricaTipoHabitacionResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaListaParametricas" type="{https://siat.impuestos.gob.bo/}respuestaListaParametricas" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sincronizarParametricaTipoHabitacionResponse", propOrder = {
    "respuestaListaParametricas"
})
public class SincronizarParametricaTipoHabitacionResponse {

    @XmlElement(name = "RespuestaListaParametricas")
    protected RespuestaListaParametricas respuestaListaParametricas;

    /**
     * Obtiene el valor de la propiedad respuestaListaParametricas.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaListaParametricas }
     *     
     */
    public RespuestaListaParametricas getRespuestaListaParametricas() {
        return respuestaListaParametricas;
    }

    /**
     * Define el valor de la propiedad respuestaListaParametricas.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaListaParametricas }
     *     
     */
    public void setRespuestaListaParametricas(RespuestaListaParametricas value) {
        this.respuestaListaParametricas = value;
    }

}
