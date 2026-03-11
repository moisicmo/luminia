
package bo.gob.impuestos.sfe.synchronization;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para respuestaListaParametricasLeyendas complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="respuestaListaParametricasLeyendas">
 *   &lt;complexContent>
 *     &lt;extension base="{https://siat.impuestos.gob.bo/}respuestaConfiguracion">
 *       &lt;sequence>
 *         &lt;element name="listaLeyendas" type="{https://siat.impuestos.gob.bo/}parametricaLeyendasDto" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respuestaListaParametricasLeyendas", propOrder = {
    "listaLeyendas"
})
public class RespuestaListaParametricasLeyendas
    extends RespuestaConfiguracion
{

    @XmlElement(nillable = true)
    protected List<ParametricaLeyendasDto> listaLeyendas;

    /**
     * Gets the value of the listaLeyendas property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listaLeyendas property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListaLeyendas().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParametricaLeyendasDto }
     * 
     * 
     */
    public List<ParametricaLeyendasDto> getListaLeyendas() {
        if (listaLeyendas == null) {
            listaLeyendas = new ArrayList<ParametricaLeyendasDto>();
        }
        return this.listaLeyendas;
    }

}
