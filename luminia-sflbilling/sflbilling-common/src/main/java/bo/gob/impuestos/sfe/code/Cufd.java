
package bo.gob.impuestos.sfe.code;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cufd complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cufd">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudCufd" type="{https://siat.impuestos.gob.bo/}solicitudCufd"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cufd", propOrder = {
    "solicitudCufd"
})
public class Cufd {

    @XmlElement(name = "SolicitudCufd", required = true)
    protected SolicitudCufd solicitudCufd;

    /**
     * Obtiene el valor de la propiedad solicitudCufd.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudCufd }
     *     
     */
    public SolicitudCufd getSolicitudCufd() {
        return solicitudCufd;
    }

    /**
     * Define el valor de la propiedad solicitudCufd.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudCufd }
     *     
     */
    public void setSolicitudCufd(SolicitudCufd value) {
        this.solicitudCufd = value;
    }

}
