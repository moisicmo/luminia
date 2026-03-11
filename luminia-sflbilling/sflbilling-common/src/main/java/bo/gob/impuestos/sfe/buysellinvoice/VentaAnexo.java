
package bo.gob.impuestos.sfe.buysellinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ventaAnexo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ventaAnexo">
 *   &lt;complexContent>
 *     &lt;extension base="{https://siat.impuestos.gob.bo/}modelDto">
 *       &lt;sequence>
 *         &lt;element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoProducto" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoProductoSin" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="tipoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ventaAnexo", propOrder = {
    "codigo",
    "codigoProducto",
    "codigoProductoSin",
    "tipoCodigo"
})
public class VentaAnexo
    extends ModelDto
{

    @XmlElement(required = true)
    protected String codigo;
    @XmlElement(required = true)
    protected String codigoProducto;
    protected long codigoProductoSin;
    @XmlElement(required = true)
    protected String tipoCodigo;

    /**
     * Obtiene el valor de la propiedad codigo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Define el valor de la propiedad codigo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigo(String value) {
        this.codigo = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoProducto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoProducto() {
        return codigoProducto;
    }

    /**
     * Define el valor de la propiedad codigoProducto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoProducto(String value) {
        this.codigoProducto = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoProductoSin.
     * 
     */
    public long getCodigoProductoSin() {
        return codigoProductoSin;
    }

    /**
     * Define el valor de la propiedad codigoProductoSin.
     * 
     */
    public void setCodigoProductoSin(long value) {
        this.codigoProductoSin = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoCodigo() {
        return tipoCodigo;
    }

    /**
     * Define el valor de la propiedad tipoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoCodigo(String value) {
        this.tipoCodigo = value;
    }

}
