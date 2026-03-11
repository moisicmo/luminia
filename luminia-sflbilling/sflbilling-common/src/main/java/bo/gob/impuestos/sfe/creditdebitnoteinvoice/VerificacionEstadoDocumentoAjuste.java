
package bo.gob.impuestos.sfe.creditdebitnoteinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para verificacionEstadoDocumentoAjuste complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="verificacionEstadoDocumentoAjuste">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudServicioVerificacionEstadoDocumentoAjuste" type="{https://siat.impuestos.gob.bo/}solicitudVerificacionEstado"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verificacionEstadoDocumentoAjuste", propOrder = {
    "solicitudServicioVerificacionEstadoDocumentoAjuste"
})
public class VerificacionEstadoDocumentoAjuste {

    @XmlElement(name = "SolicitudServicioVerificacionEstadoDocumentoAjuste", required = true)
    protected SolicitudVerificacionEstado solicitudServicioVerificacionEstadoDocumentoAjuste;

    /**
     * Obtiene el valor de la propiedad solicitudServicioVerificacionEstadoDocumentoAjuste.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudVerificacionEstado }
     *     
     */
    public SolicitudVerificacionEstado getSolicitudServicioVerificacionEstadoDocumentoAjuste() {
        return solicitudServicioVerificacionEstadoDocumentoAjuste;
    }

    /**
     * Define el valor de la propiedad solicitudServicioVerificacionEstadoDocumentoAjuste.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudVerificacionEstado }
     *     
     */
    public void setSolicitudServicioVerificacionEstadoDocumentoAjuste(SolicitudVerificacionEstado value) {
        this.solicitudServicioVerificacionEstadoDocumentoAjuste = value;
    }

}
