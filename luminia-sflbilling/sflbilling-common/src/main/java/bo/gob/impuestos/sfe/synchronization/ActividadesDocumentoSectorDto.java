
package bo.gob.impuestos.sfe.synchronization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para actividadesDocumentoSectorDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="actividadesDocumentoSectorDto">
 *   &lt;complexContent>
 *     &lt;extension base="{https://siat.impuestos.gob.bo/}modelDto">
 *       &lt;sequence>
 *         &lt;element name="codigoActividad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoDocumentoSector" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="tipoDocumentoSector" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "actividadesDocumentoSectorDto", propOrder = {
    "codigoActividad",
    "codigoDocumentoSector",
    "tipoDocumentoSector"
})
public class ActividadesDocumentoSectorDto
    extends ModelDto
{

    protected String codigoActividad;
    protected Integer codigoDocumentoSector;
    protected String tipoDocumentoSector;

    /**
     * Obtiene el valor de la propiedad codigoActividad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoActividad() {
        return codigoActividad;
    }

    /**
     * Define el valor de la propiedad codigoActividad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoActividad(String value) {
        this.codigoActividad = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoDocumentoSector.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCodigoDocumentoSector() {
        return codigoDocumentoSector;
    }

    /**
     * Define el valor de la propiedad codigoDocumentoSector.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCodigoDocumentoSector(Integer value) {
        this.codigoDocumentoSector = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoDocumentoSector.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoDocumentoSector() {
        return tipoDocumentoSector;
    }

    /**
     * Define el valor de la propiedad tipoDocumentoSector.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoDocumentoSector(String value) {
        this.tipoDocumentoSector = value;
    }

}
