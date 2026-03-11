
package bo.gob.impuestos.sfe.operation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cierreOperacionesSistema complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cierreOperacionesSistema">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudOperaciones" type="{https://siat.impuestos.gob.bo/}solicitudOperaciones"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cierreOperacionesSistema", propOrder = {
    "solicitudOperaciones"
})
public class CierreOperacionesSistema {

    @XmlElement(name = "SolicitudOperaciones", required = true)
    protected SolicitudOperaciones solicitudOperaciones;

    /**
     * Obtiene el valor de la propiedad solicitudOperaciones.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudOperaciones }
     *     
     */
    public SolicitudOperaciones getSolicitudOperaciones() {
        return solicitudOperaciones;
    }

    /**
     * Define el valor de la propiedad solicitudOperaciones.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudOperaciones }
     *     
     */
    public void setSolicitudOperaciones(SolicitudOperaciones value) {
        this.solicitudOperaciones = value;
    }

}
