
package bo.gob.impuestos.sfe.creditdebitnoteinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para recepcionDocumentoAjuste complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="recepcionDocumentoAjuste">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudServicioRecepcionDocumentoAjuste" type="{https://siat.impuestos.gob.bo/}solicitudRecepcionFactura"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recepcionDocumentoAjuste", propOrder = {
    "solicitudServicioRecepcionDocumentoAjuste"
})
public class RecepcionDocumentoAjuste {

    @XmlElement(name = "SolicitudServicioRecepcionDocumentoAjuste", required = true)
    protected SolicitudRecepcionFactura solicitudServicioRecepcionDocumentoAjuste;

    /**
     * Obtiene el valor de la propiedad solicitudServicioRecepcionDocumentoAjuste.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudRecepcionFactura }
     *     
     */
    public SolicitudRecepcionFactura getSolicitudServicioRecepcionDocumentoAjuste() {
        return solicitudServicioRecepcionDocumentoAjuste;
    }

    /**
     * Define el valor de la propiedad solicitudServicioRecepcionDocumentoAjuste.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudRecepcionFactura }
     *     
     */
    public void setSolicitudServicioRecepcionDocumentoAjuste(SolicitudRecepcionFactura value) {
        this.solicitudServicioRecepcionDocumentoAjuste = value;
    }

}
