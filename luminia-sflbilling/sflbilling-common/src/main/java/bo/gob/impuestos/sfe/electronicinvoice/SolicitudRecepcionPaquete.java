
package bo.gob.impuestos.sfe.electronicinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para solicitudRecepcionPaquete complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="solicitudRecepcionPaquete">
 *   &lt;complexContent>
 *     &lt;extension base="{https://siat.impuestos.gob.bo/}solicitudRecepcionFactura">
 *       &lt;sequence>
 *         &lt;element name="cafc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cantidadFacturas" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="codigoEvento" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "solicitudRecepcionPaquete", propOrder = {
    "cafc",
    "cantidadFacturas",
    "codigoEvento"
})
public class SolicitudRecepcionPaquete
    extends SolicitudRecepcionFactura
{

    protected String cafc;
    protected int cantidadFacturas;
    protected long codigoEvento;

    /**
     * Obtiene el valor de la propiedad cafc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCafc() {
        return cafc;
    }

    /**
     * Define el valor de la propiedad cafc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCafc(String value) {
        this.cafc = value;
    }

    /**
     * Obtiene el valor de la propiedad cantidadFacturas.
     * 
     */
    public int getCantidadFacturas() {
        return cantidadFacturas;
    }

    /**
     * Define el valor de la propiedad cantidadFacturas.
     * 
     */
    public void setCantidadFacturas(int value) {
        this.cantidadFacturas = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoEvento.
     * 
     */
    public long getCodigoEvento() {
        return codigoEvento;
    }

    /**
     * Define el valor de la propiedad codigoEvento.
     * 
     */
    public void setCodigoEvento(long value) {
        this.codigoEvento = value;
    }

}
