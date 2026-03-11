
package bo.gob.impuestos.sfe.synchronization;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para respuestaListaParametricas complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="respuestaListaParametricas">
 *   &lt;complexContent>
 *     &lt;extension base="{https://siat.impuestos.gob.bo/}respuestaConfiguracion">
 *       &lt;sequence>
 *         &lt;element name="listaCodigos" type="{https://siat.impuestos.gob.bo/}parametricasDto" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respuestaListaParametricas", propOrder = {
    "listaCodigos"
})
public class RespuestaListaParametricas
    extends RespuestaConfiguracion
{

    @XmlElement(nillable = true)
    protected List<ParametricasDto> listaCodigos;

    /**
     * Gets the value of the listaCodigos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listaCodigos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListaCodigos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParametricasDto }
     * 
     * 
     */
    public List<ParametricasDto> getListaCodigos() {
        if (listaCodigos == null) {
            listaCodigos = new ArrayList<ParametricasDto>();
        }
        return this.listaCodigos;
    }

}
