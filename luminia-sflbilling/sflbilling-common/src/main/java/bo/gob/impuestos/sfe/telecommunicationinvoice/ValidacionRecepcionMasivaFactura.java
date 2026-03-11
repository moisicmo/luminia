
package bo.gob.impuestos.sfe.telecommunicationinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para validacionRecepcionMasivaFactura complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="validacionRecepcionMasivaFactura">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudServicioValidacionRecepcionMasiva" type="{https://siat.impuestos.gob.bo/}solicitudValidacionRecepcion"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validacionRecepcionMasivaFactura", propOrder = {
    "solicitudServicioValidacionRecepcionMasiva"
})
public class ValidacionRecepcionMasivaFactura {

    @XmlElement(name = "SolicitudServicioValidacionRecepcionMasiva", required = true)
    protected SolicitudValidacionRecepcion solicitudServicioValidacionRecepcionMasiva;

    /**
     * Obtiene el valor de la propiedad solicitudServicioValidacionRecepcionMasiva.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudValidacionRecepcion }
     *     
     */
    public SolicitudValidacionRecepcion getSolicitudServicioValidacionRecepcionMasiva() {
        return solicitudServicioValidacionRecepcionMasiva;
    }

    /**
     * Define el valor de la propiedad solicitudServicioValidacionRecepcionMasiva.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudValidacionRecepcion }
     *     
     */
    public void setSolicitudServicioValidacionRecepcionMasiva(SolicitudValidacionRecepcion value) {
        this.solicitudServicioValidacionRecepcionMasiva = value;
    }

}
