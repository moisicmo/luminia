
package bo.gob.impuestos.sfe.operation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para consultaPuntoVentaResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="consultaPuntoVentaResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaConsultaPuntoVenta" type="{https://siat.impuestos.gob.bo/}respuestaConsultaPuntoVenta" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaPuntoVentaResponse", propOrder = {
    "respuestaConsultaPuntoVenta"
})
public class ConsultaPuntoVentaResponse {

    @XmlElement(name = "RespuestaConsultaPuntoVenta")
    protected RespuestaConsultaPuntoVenta respuestaConsultaPuntoVenta;

    /**
     * Obtiene el valor de la propiedad respuestaConsultaPuntoVenta.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaConsultaPuntoVenta }
     *     
     */
    public RespuestaConsultaPuntoVenta getRespuestaConsultaPuntoVenta() {
        return respuestaConsultaPuntoVenta;
    }

    /**
     * Define el valor de la propiedad respuestaConsultaPuntoVenta.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaConsultaPuntoVenta }
     *     
     */
    public void setRespuestaConsultaPuntoVenta(RespuestaConsultaPuntoVenta value) {
        this.respuestaConsultaPuntoVenta = value;
    }

}
