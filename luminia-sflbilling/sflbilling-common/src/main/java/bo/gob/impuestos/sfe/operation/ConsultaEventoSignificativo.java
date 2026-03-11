
package bo.gob.impuestos.sfe.operation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para consultaEventoSignificativo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="consultaEventoSignificativo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudConsultaEvento" type="{https://siat.impuestos.gob.bo/}solicitudConsultaEvento"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaEventoSignificativo", propOrder = {
    "solicitudConsultaEvento"
})
public class ConsultaEventoSignificativo {

    @XmlElement(name = "SolicitudConsultaEvento", required = true)
    protected SolicitudConsultaEvento solicitudConsultaEvento;

    /**
     * Obtiene el valor de la propiedad solicitudConsultaEvento.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudConsultaEvento }
     *     
     */
    public SolicitudConsultaEvento getSolicitudConsultaEvento() {
        return solicitudConsultaEvento;
    }

    /**
     * Define el valor de la propiedad solicitudConsultaEvento.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudConsultaEvento }
     *     
     */
    public void setSolicitudConsultaEvento(SolicitudConsultaEvento value) {
        this.solicitudConsultaEvento = value;
    }

}
