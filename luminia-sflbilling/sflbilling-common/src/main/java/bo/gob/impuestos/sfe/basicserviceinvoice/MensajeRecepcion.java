
package bo.gob.impuestos.sfe.basicserviceinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para mensajeRecepcion complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="mensajeRecepcion">
 *   &lt;complexContent>
 *     &lt;extension base="{https://siat.impuestos.gob.bo/}mensajeServicio">
 *       &lt;sequence>
 *         &lt;element name="advertencia" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="numeroArchivo" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="numeroDetalle" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mensajeRecepcion", propOrder = {
    "advertencia",
    "numeroArchivo",
    "numeroDetalle"
})
public class MensajeRecepcion
    extends MensajeServicio
{

    protected Boolean advertencia;
    protected Integer numeroArchivo;
    protected Integer numeroDetalle;

    /**
     * Obtiene el valor de la propiedad advertencia.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAdvertencia() {
        return advertencia;
    }

    /**
     * Define el valor de la propiedad advertencia.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAdvertencia(Boolean value) {
        this.advertencia = value;
    }

    /**
     * Obtiene el valor de la propiedad numeroArchivo.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumeroArchivo() {
        return numeroArchivo;
    }

    /**
     * Define el valor de la propiedad numeroArchivo.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumeroArchivo(Integer value) {
        this.numeroArchivo = value;
    }

    /**
     * Obtiene el valor de la propiedad numeroDetalle.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumeroDetalle() {
        return numeroDetalle;
    }

    /**
     * Define el valor de la propiedad numeroDetalle.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumeroDetalle(Integer value) {
        this.numeroDetalle = value;
    }

}
