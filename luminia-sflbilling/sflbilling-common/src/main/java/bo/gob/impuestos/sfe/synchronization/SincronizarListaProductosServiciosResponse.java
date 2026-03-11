
package bo.gob.impuestos.sfe.synchronization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para sincronizarListaProductosServiciosResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="sincronizarListaProductosServiciosResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaListaProductos" type="{https://siat.impuestos.gob.bo/}respuestaListaProductos" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sincronizarListaProductosServiciosResponse", propOrder = {
    "respuestaListaProductos"
})
public class SincronizarListaProductosServiciosResponse {

    @XmlElement(name = "RespuestaListaProductos")
    protected RespuestaListaProductos respuestaListaProductos;

    /**
     * Obtiene el valor de la propiedad respuestaListaProductos.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaListaProductos }
     *     
     */
    public RespuestaListaProductos getRespuestaListaProductos() {
        return respuestaListaProductos;
    }

    /**
     * Define el valor de la propiedad respuestaListaProductos.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaListaProductos }
     *     
     */
    public void setRespuestaListaProductos(RespuestaListaProductos value) {
        this.respuestaListaProductos = value;
    }

}
