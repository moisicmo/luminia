
package bo.gob.impuestos.sfe.code;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cuisMasivo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="cuisMasivo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudCuisMasivoSistemas" type="{https://siat.impuestos.gob.bo/}solicitudCuisMasivoSistemas"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cuisMasivo", propOrder = {
    "solicitudCuisMasivoSistemas"
})
public class CuisMasivo {

    @XmlElement(name = "SolicitudCuisMasivoSistemas", required = true)
    protected SolicitudCuisMasivoSistemas solicitudCuisMasivoSistemas;

    /**
     * Obtiene el valor de la propiedad solicitudCuisMasivoSistemas.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudCuisMasivoSistemas }
     *     
     */
    public SolicitudCuisMasivoSistemas getSolicitudCuisMasivoSistemas() {
        return solicitudCuisMasivoSistemas;
    }

    /**
     * Define el valor de la propiedad solicitudCuisMasivoSistemas.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudCuisMasivoSistemas }
     *     
     */
    public void setSolicitudCuisMasivoSistemas(SolicitudCuisMasivoSistemas value) {
        this.solicitudCuisMasivoSistemas = value;
    }

}
