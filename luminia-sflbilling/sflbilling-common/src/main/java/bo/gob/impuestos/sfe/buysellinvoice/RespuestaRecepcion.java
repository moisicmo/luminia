
package bo.gob.impuestos.sfe.buysellinvoice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para respuestaRecepcion complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="respuestaRecepcion">
 *   &lt;complexContent>
 *     &lt;extension base="{https://siat.impuestos.gob.bo/}modelDto">
 *       &lt;sequence>
 *         &lt;element name="codigoDescripcion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoEstado" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="codigoRecepcion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mensajesList" type="{https://siat.impuestos.gob.bo/}mensajeRecepcion" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="transaccion" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respuestaRecepcion", propOrder = {
    "codigoDescripcion",
    "codigoEstado",
    "codigoRecepcion",
    "mensajesList",
    "transaccion"
})
public class RespuestaRecepcion
    extends ModelDto
{

    protected String codigoDescripcion;
    protected Integer codigoEstado;
    protected String codigoRecepcion;
    @XmlElement(nillable = true)
    protected List<MensajeRecepcion> mensajesList;
    protected Boolean transaccion;

    /**
     * Obtiene el valor de la propiedad codigoDescripcion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoDescripcion() {
        return codigoDescripcion;
    }

    /**
     * Define el valor de la propiedad codigoDescripcion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoDescripcion(String value) {
        this.codigoDescripcion = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoEstado.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCodigoEstado() {
        return codigoEstado;
    }

    /**
     * Define el valor de la propiedad codigoEstado.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCodigoEstado(Integer value) {
        this.codigoEstado = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoRecepcion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoRecepcion() {
        return codigoRecepcion;
    }

    /**
     * Define el valor de la propiedad codigoRecepcion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoRecepcion(String value) {
        this.codigoRecepcion = value;
    }

    /**
     * Gets the value of the mensajesList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mensajesList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMensajesList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MensajeRecepcion }
     * 
     * 
     */
    public List<MensajeRecepcion> getMensajesList() {
        if (mensajesList == null) {
            mensajesList = new ArrayList<MensajeRecepcion>();
        }
        return this.mensajesList;
    }

    /**
     * Obtiene el valor de la propiedad transaccion.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTransaccion() {
        return transaccion;
    }

    /**
     * Define el valor de la propiedad transaccion.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTransaccion(Boolean value) {
        this.transaccion = value;
    }

}
