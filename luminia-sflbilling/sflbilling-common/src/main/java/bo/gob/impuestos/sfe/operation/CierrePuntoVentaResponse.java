
package bo.gob.impuestos.sfe.operation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cierrePuntoVentaResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cierrePuntoVentaResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaCierrePuntoVenta" type="{https://siat.impuestos.gob.bo/}respuestaCierrePuntoVenta" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cierrePuntoVentaResponse", propOrder = {
    "respuestaCierrePuntoVenta"
})
public class CierrePuntoVentaResponse {

    @XmlElement(name = "RespuestaCierrePuntoVenta")
    protected RespuestaCierrePuntoVenta respuestaCierrePuntoVenta;

    /**
     * Obtiene el valor de la propiedad respuestaCierrePuntoVenta.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaCierrePuntoVenta }
     *     
     */
    public RespuestaCierrePuntoVenta getRespuestaCierrePuntoVenta() {
        return respuestaCierrePuntoVenta;
    }

    /**
     * Define el valor de la propiedad respuestaCierrePuntoVenta.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaCierrePuntoVenta }
     *     
     */
    public void setRespuestaCierrePuntoVenta(RespuestaCierrePuntoVenta value) {
        this.respuestaCierrePuntoVenta = value;
    }

}
