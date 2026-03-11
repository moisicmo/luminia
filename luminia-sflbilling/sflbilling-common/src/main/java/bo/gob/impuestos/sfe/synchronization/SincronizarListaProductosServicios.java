
package bo.gob.impuestos.sfe.synchronization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para sincronizarListaProductosServicios complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="sincronizarListaProductosServicios">
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
@XmlType(name = "sincronizarListaProductosServicios", propOrder = {
    "solicitudSincronizacion"
})
public class SincronizarListaProductosServicios {

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
