
package bo.gob.impuestos.sfe.synchronization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para sincronizarActividadesResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="sincronizarActividadesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaListaActividades" type="{https://siat.impuestos.gob.bo/}respuestaListaActividades" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sincronizarActividadesResponse", propOrder = {
    "respuestaListaActividades"
})
public class SincronizarActividadesResponse {

    @XmlElement(name = "RespuestaListaActividades")
    protected RespuestaListaActividades respuestaListaActividades;

    /**
     * Obtiene el valor de la propiedad respuestaListaActividades.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaListaActividades }
     *     
     */
    public RespuestaListaActividades getRespuestaListaActividades() {
        return respuestaListaActividades;
    }

    /**
     * Define el valor de la propiedad respuestaListaActividades.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaListaActividades }
     *     
     */
    public void setRespuestaListaActividades(RespuestaListaActividades value) {
        this.respuestaListaActividades = value;
    }

}
