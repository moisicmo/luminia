
package bo.gob.impuestos.sfe.buysellinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para recepcionAnexos complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="recepcionAnexos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudRecepcionAnexos" type="{https://siat.impuestos.gob.bo/}solicitudRecepcionAnexos"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recepcionAnexos", propOrder = {
    "solicitudRecepcionAnexos"
})
public class RecepcionAnexos {

    @XmlElement(name = "SolicitudRecepcionAnexos", required = true)
    protected SolicitudRecepcionAnexos solicitudRecepcionAnexos;

    /**
     * Obtiene el valor de la propiedad solicitudRecepcionAnexos.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudRecepcionAnexos }
     *     
     */
    public SolicitudRecepcionAnexos getSolicitudRecepcionAnexos() {
        return solicitudRecepcionAnexos;
    }

    /**
     * Define el valor de la propiedad solicitudRecepcionAnexos.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudRecepcionAnexos }
     *     
     */
    public void setSolicitudRecepcionAnexos(SolicitudRecepcionAnexos value) {
        this.solicitudRecepcionAnexos = value;
    }

}
