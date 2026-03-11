
package bo.gob.impuestos.sfe.code;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para respuestaCuisMasivo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="respuestaCuisMasivo">
 *   &lt;complexContent>
 *     &lt;extension base="{https://siat.impuestos.gob.bo/}modelDto">
 *       &lt;sequence>
 *         &lt;element name="listaRespuestasCuis" type="{https://siat.impuestos.gob.bo/}respuestaListaRegistroCuisSoapDto" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mensajesList" type="{https://siat.impuestos.gob.bo/}mensajeServicio" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "respuestaCuisMasivo", propOrder = {
    "listaRespuestasCuis",
    "mensajesList",
    "transaccion"
})
@ToString
public class RespuestaCuisMasivo
    extends ModelDto
{

    @XmlElement(nillable = true)
    protected List<RespuestaListaRegistroCuisSoapDto> listaRespuestasCuis;
    @XmlElement(nillable = true)
    protected List<MensajeServicio> mensajesList;
    protected Boolean transaccion;

    /**
     * Gets the value of the listaRespuestasCuis property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listaRespuestasCuis property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListaRespuestasCuis().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RespuestaListaRegistroCuisSoapDto }
     * 
     * 
     */
    public List<RespuestaListaRegistroCuisSoapDto> getListaRespuestasCuis() {
        if (listaRespuestasCuis == null) {
            listaRespuestasCuis = new ArrayList<RespuestaListaRegistroCuisSoapDto>();
        }
        return this.listaRespuestasCuis;
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
     * {@link MensajeServicio }
     * 
     * 
     */
    public List<MensajeServicio> getMensajesList() {
        if (mensajesList == null) {
            mensajesList = new ArrayList<MensajeServicio>();
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
