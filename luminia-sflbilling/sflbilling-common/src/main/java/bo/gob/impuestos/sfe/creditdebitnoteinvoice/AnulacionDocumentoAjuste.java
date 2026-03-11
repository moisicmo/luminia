
package bo.gob.impuestos.sfe.creditdebitnoteinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anulacionDocumentoAjuste complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="anulacionDocumentoAjuste">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudServicioAnulacionDocumentoAjuste" type="{https://siat.impuestos.gob.bo/}solicitudAnulacion"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "anulacionDocumentoAjuste", propOrder = {
    "solicitudServicioAnulacionDocumentoAjuste"
})
public class AnulacionDocumentoAjuste {

    @XmlElement(name = "SolicitudServicioAnulacionDocumentoAjuste", required = true)
    protected SolicitudAnulacion solicitudServicioAnulacionDocumentoAjuste;

    /**
     * Obtiene el valor de la propiedad solicitudServicioAnulacionDocumentoAjuste.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudAnulacion }
     *     
     */
    public SolicitudAnulacion getSolicitudServicioAnulacionDocumentoAjuste() {
        return solicitudServicioAnulacionDocumentoAjuste;
    }

    /**
     * Define el valor de la propiedad solicitudServicioAnulacionDocumentoAjuste.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudAnulacion }
     *     
     */
    public void setSolicitudServicioAnulacionDocumentoAjuste(SolicitudAnulacion value) {
        this.solicitudServicioAnulacionDocumentoAjuste = value;
    }

}
