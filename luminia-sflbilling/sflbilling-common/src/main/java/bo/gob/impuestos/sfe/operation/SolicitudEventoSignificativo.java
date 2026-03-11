
package bo.gob.impuestos.sfe.operation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para solicitudEventoSignificativo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="solicitudEventoSignificativo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoAmbiente" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="codigoMotivoEvento" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="codigoPuntoVenta" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="codigoSistema" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoSucursal" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cufd" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cufdEvento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cuis" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descripcion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fechaHoraFinEvento" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="fechaHoraInicioEvento" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
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
@XmlType(name = "solicitudEventoSignificativo", propOrder = {
    "codigoAmbiente",
    "codigoMotivoEvento",
    "codigoPuntoVenta",
    "codigoSistema",
    "codigoSucursal",
    "cufd",
    "cufdEvento",
    "cuis",
    "descripcion",
    "fechaHoraFinEvento",
    "fechaHoraInicioEvento",
    "nit"
})
public class SolicitudEventoSignificativo {

    protected int codigoAmbiente;
    protected int codigoMotivoEvento;
    @XmlElementRef(name = "codigoPuntoVenta", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> codigoPuntoVenta;
    @XmlElement(required = true)
    protected String codigoSistema;
    protected int codigoSucursal;
    @XmlElement(required = true)
    protected String cufd;
    @XmlElement(required = true)
    protected String cufdEvento;
    @XmlElement(required = true)
    protected String cuis;
    @XmlElement(required = true)
    protected String descripcion;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaHoraFinEvento;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaHoraInicioEvento;
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
     * Obtiene el valor de la propiedad codigoMotivoEvento.
     * 
     */
    public int getCodigoMotivoEvento() {
        return codigoMotivoEvento;
    }

    /**
     * Define el valor de la propiedad codigoMotivoEvento.
     * 
     */
    public void setCodigoMotivoEvento(int value) {
        this.codigoMotivoEvento = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoPuntoVenta.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getCodigoPuntoVenta() {
        return codigoPuntoVenta;
    }

    /**
     * Define el valor de la propiedad codigoPuntoVenta.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setCodigoPuntoVenta(JAXBElement<Integer> value) {
        this.codigoPuntoVenta = value;
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
     * Obtiene el valor de la propiedad codigoSucursal.
     * 
     */
    public int getCodigoSucursal() {
        return codigoSucursal;
    }

    /**
     * Define el valor de la propiedad codigoSucursal.
     * 
     */
    public void setCodigoSucursal(int value) {
        this.codigoSucursal = value;
    }

    /**
     * Obtiene el valor de la propiedad cufd.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCufd() {
        return cufd;
    }

    /**
     * Define el valor de la propiedad cufd.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCufd(String value) {
        this.cufd = value;
    }

    /**
     * Obtiene el valor de la propiedad cufdEvento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCufdEvento() {
        return cufdEvento;
    }

    /**
     * Define el valor de la propiedad cufdEvento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCufdEvento(String value) {
        this.cufdEvento = value;
    }

    /**
     * Obtiene el valor de la propiedad cuis.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCuis() {
        return cuis;
    }

    /**
     * Define el valor de la propiedad cuis.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCuis(String value) {
        this.cuis = value;
    }

    /**
     * Obtiene el valor de la propiedad descripcion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Define el valor de la propiedad descripcion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcion(String value) {
        this.descripcion = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaHoraFinEvento.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaHoraFinEvento() {
        return fechaHoraFinEvento;
    }

    /**
     * Define el valor de la propiedad fechaHoraFinEvento.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaHoraFinEvento(XMLGregorianCalendar value) {
        this.fechaHoraFinEvento = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaHoraInicioEvento.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaHoraInicioEvento() {
        return fechaHoraInicioEvento;
    }

    /**
     * Define el valor de la propiedad fechaHoraInicioEvento.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaHoraInicioEvento(XMLGregorianCalendar value) {
        this.fechaHoraInicioEvento = value;
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
