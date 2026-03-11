
package bo.gob.impuestos.sfe.buysellinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para reversionAnulacionFactura complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="reversionAnulacionFactura">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SolicitudServicioReversionAnulacionFactura" type="{https://siat.impuestos.gob.bo/}solicitudReversionAnulacion"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reversionAnulacionFactura", propOrder = {
    "solicitudServicioReversionAnulacionFactura"
})
public class ReversionAnulacionFactura {

    @XmlElement(name = "SolicitudServicioReversionAnulacionFactura", required = true)
    protected SolicitudReversionAnulacion solicitudServicioReversionAnulacionFactura;

    /**
     * Obtiene el valor de la propiedad solicitudServicioReversionAnulacionFactura.
     * 
     * @return
     *     possible object is
     *     {@link SolicitudReversionAnulacion }
     *     
     */
    public SolicitudReversionAnulacion getSolicitudServicioReversionAnulacionFactura() {
        return solicitudServicioReversionAnulacionFactura;
    }

    /**
     * Define el valor de la propiedad solicitudServicioReversionAnulacionFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link SolicitudReversionAnulacion }
     *     
     */
    public void setSolicitudServicioReversionAnulacionFactura(SolicitudReversionAnulacion value) {
        this.solicitudServicioReversionAnulacionFactura = value;
    }

}
