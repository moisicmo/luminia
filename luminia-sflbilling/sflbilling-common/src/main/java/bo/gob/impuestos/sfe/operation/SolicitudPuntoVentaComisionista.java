
package bo.gob.impuestos.sfe.operation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para solicitudPuntoVentaComisionista complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="solicitudPuntoVentaComisionista">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoAmbiente" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="codigoModalidad" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="codigoSistema" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoSucursal" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cuis" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descripcion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fechaFin" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="fechaInicio" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="nit" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="nitComisionista" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="nombrePuntoVenta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numeroContrato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "solicitudPuntoVentaComisionista", propOrder = {
    "codigoAmbiente",
    "codigoModalidad",
    "codigoSistema",
    "codigoSucursal",
    "cuis",
    "descripcion",
    "fechaFin",
    "fechaInicio",
    "nit",
    "nitComisionista",
    "nombrePuntoVenta",
    "numeroContrato"
})
public class SolicitudPuntoVentaComisionista {

    protected int codigoAmbiente;
    protected int codigoModalidad;
    @XmlElement(required = true)
    protected String codigoSistema;
    protected int codigoSucursal;
    @XmlElement(required = true)
    protected String cuis;
    @XmlElement(required = true)
    protected String descripcion;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaFin;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaInicio;
    protected long nit;
    protected long nitComisionista;
    @XmlElement(required = true)
    protected String nombrePuntoVenta;
    @XmlElement(required = true)
    protected String numeroContrato;

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
     * Obtiene el valor de la propiedad fechaFin.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaFin() {
        return fechaFin;
    }

    /**
     * Define el valor de la propiedad fechaFin.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaFin(XMLGregorianCalendar value) {
        this.fechaFin = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaInicio.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Define el valor de la propiedad fechaInicio.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaInicio(XMLGregorianCalendar value) {
        this.fechaInicio = value;
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

    /**
     * Obtiene el valor de la propiedad nitComisionista.
     * 
     */
    public long getNitComisionista() {
        return nitComisionista;
    }

    /**
     * Define el valor de la propiedad nitComisionista.
     * 
     */
    public void setNitComisionista(long value) {
        this.nitComisionista = value;
    }

    /**
     * Obtiene el valor de la propiedad nombrePuntoVenta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombrePuntoVenta() {
        return nombrePuntoVenta;
    }

    /**
     * Define el valor de la propiedad nombrePuntoVenta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombrePuntoVenta(String value) {
        this.nombrePuntoVenta = value;
    }

    /**
     * Obtiene el valor de la propiedad numeroContrato.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroContrato() {
        return numeroContrato;
    }

    /**
     * Define el valor de la propiedad numeroContrato.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroContrato(String value) {
        this.numeroContrato = value;
    }

}
