
package bo.gob.impuestos.sfe.telecommunicationinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para verificacionEstadoFacturaResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="verificacionEstadoFacturaResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaServicioFacturacion" type="{https://siat.impuestos.gob.bo/}respuestaRecepcion" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verificacionEstadoFacturaResponse", propOrder = {
    "respuestaServicioFacturacion"
})
public class VerificacionEstadoFacturaResponse {

    @XmlElement(name = "RespuestaServicioFacturacion")
    protected RespuestaRecepcion respuestaServicioFacturacion;

    /**
     * Obtiene el valor de la propiedad respuestaServicioFacturacion.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaRecepcion }
     *     
     */
    public RespuestaRecepcion getRespuestaServicioFacturacion() {
        return respuestaServicioFacturacion;
    }

    /**
     * Define el valor de la propiedad respuestaServicioFacturacion.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaRecepcion }
     *     
     */
    public void setRespuestaServicioFacturacion(RespuestaRecepcion value) {
        this.respuestaServicioFacturacion = value;
    }

}
