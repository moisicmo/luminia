
package bo.gob.impuestos.sfe.buysellinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para solicitudRecepcionMasiva complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="solicitudRecepcionMasiva">
 *   &lt;complexContent>
 *     &lt;extension base="{https://siat.impuestos.gob.bo/}solicitudRecepcionFactura">
 *       &lt;sequence>
 *         &lt;element name="cantidadFacturas" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "solicitudRecepcionMasiva", propOrder = {
    "cantidadFacturas"
})
public class SolicitudRecepcionMasiva
    extends SolicitudRecepcionFactura
{

    protected int cantidadFacturas;

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

}
