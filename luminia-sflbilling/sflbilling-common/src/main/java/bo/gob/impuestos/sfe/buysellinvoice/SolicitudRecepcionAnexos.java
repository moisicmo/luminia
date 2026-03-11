
package bo.gob.impuestos.sfe.buysellinvoice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para solicitudRecepcionAnexos complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="solicitudRecepcionAnexos">
 *   &lt;complexContent>
 *     &lt;extension base="{https://siat.impuestos.gob.bo/}solicitudRecepcion">
 *       &lt;sequence>
 *         &lt;element name="anexosList" type="{https://siat.impuestos.gob.bo/}ventaAnexo" maxOccurs="unbounded"/>
 *         &lt;element name="cuf" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "solicitudRecepcionAnexos", propOrder = {
    "anexosList",
    "cuf"
})
public class SolicitudRecepcionAnexos
    extends SolicitudRecepcion
{

    @XmlElement(required = true)
    protected List<VentaAnexo> anexosList;
    @XmlElement(required = true)
    protected String cuf;

    /**
     * Gets the value of the anexosList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the anexosList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnexosList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VentaAnexo }
     * 
     * 
     */
    public List<VentaAnexo> getAnexosList() {
        if (anexosList == null) {
            anexosList = new ArrayList<VentaAnexo>();
        }
        return this.anexosList;
    }

    /**
     * Obtiene el valor de la propiedad cuf.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCuf() {
        return cuf;
    }

    /**
     * Define el valor de la propiedad cuf.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCuf(String value) {
        this.cuf = value;
    }

}
