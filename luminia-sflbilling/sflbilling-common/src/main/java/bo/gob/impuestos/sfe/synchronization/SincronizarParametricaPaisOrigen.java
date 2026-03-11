
package bo.gob.impuestos.sfe.synchronization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para sincronizarParametricaPaisOrigen complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="sincronizarParametricaPaisOrigen">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudSincronizacion" type="{https://siat.impuestos.gob.bo/}solicitudSincronizacion"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sincronizarParametricaPaisOrigen", propOrder = {
    "solicitudSincronizacion"
})
public class SincronizarParametricaPaisOrigen {

    @XmlElement(name = "SolicitudSincronizacion", required = true)
    protected SolicitudSincronizacion solicitudSincronizacion;

    /**
     * Obtiene el valor de la propiedad solicitudSincronizacion.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudSincronizacion }
     *     
     */
    public SolicitudSincronizacion getSolicitudSincronizacion() {
        return solicitudSincronizacion;
    }

    /**
     * Define el valor de la propiedad solicitudSincronizacion.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudSincronizacion }
     *     
     */
    public void setSolicitudSincronizacion(SolicitudSincronizacion value) {
        this.solicitudSincronizacion = value;
    }

}
