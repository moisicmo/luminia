
package bo.gob.impuestos.sfe.code;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para solicitudCuisMasivoSistemas complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="solicitudCuisMasivoSistemas">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoAmbiente" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="codigoModalidad" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="codigoSistema" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="datosSolicitud" type="{https://siat.impuestos.gob.bo/}solicitudListaCuisDto" maxOccurs="unbounded"/>
 *         &lt;element name="nit" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "solicitudCuisMasivoSistemas", propOrder = {
    "codigoAmbiente",
    "codigoModalidad",
    "codigoSistema",
    "datosSolicitud",
    "nit"
})
@ToString
public class SolicitudCuisMasivoSistemas {

    protected int codigoAmbiente;
    protected int codigoModalidad;
    @XmlElement(required = true)
    protected String codigoSistema;
    @XmlElement(required = true)
    protected List<SolicitudListaCuisDto> datosSolicitud;
    protected long nit;

    /**
     * Obtiene el valor de la propiedad codigoAmbiente.
     * 
     */
    public int getCodigoAmbiente() {
        return codigoAmbiente;
    }

    /**
     * Define el valor de la propiedad codigoAmbiente.
     * 
     */
    public void setCodigoAmbiente(int value) {
        this.codigoAmbiente = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoModalidad.
     * 
     */
    public int getCodigoModalidad() {
        return codigoModalidad;
    }

    /**
     * Define el valor de la propiedad codigoModalidad.
     * 
     */
    public void setCodigoModalidad(int value) {
        this.codigoModalidad = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoSistema.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoSistema() {
        return codigoSistema;
    }

    /**
     * Define el valor de la propiedad codigoSistema.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoSistema(String value) {
        this.codigoSistema = value;
    }

    /**
     * Gets the value of the datosSolicitud property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datosSolicitud property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatosSolicitud().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SolicitudListaCuisDto }
     * 
     * 
     */
    public List<SolicitudListaCuisDto> getDatosSolicitud() {
        if (datosSolicitud == null) {
            datosSolicitud = new ArrayList<SolicitudListaCuisDto>();
        }
        return this.datosSolicitud;
    }

    /**
     * Obtiene el valor de la propiedad nit.
     * 
     */
    public long getNit() {
        return nit;
    }

    /**
     * Define el valor de la propiedad nit.
     * 
     */
    public void setNit(long value) {
        this.nit = value;
    }

}
