
package bo.gob.impuestos.sfe.synchronization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para sincronizarListaLeyendasFacturaResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="sincronizarListaLeyendasFacturaResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaListaParametricasLeyendas" type="{https://siat.impuestos.gob.bo/}respuestaListaParametricasLeyendas" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sincronizarListaLeyendasFacturaResponse", propOrder = {
    "respuestaListaParametricasLeyendas"
})
public class SincronizarListaLeyendasFacturaResponse {

    @XmlElement(name = "RespuestaListaParametricasLeyendas")
    protected RespuestaListaParametricasLeyendas respuestaListaParametricasLeyendas;

    /**
     * Obtiene el valor de la propiedad respuestaListaParametricasLeyendas.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaListaParametricasLeyendas }
     *     
     */
    public RespuestaListaParametricasLeyendas getRespuestaListaParametricasLeyendas() {
        return respuestaListaParametricasLeyendas;
    }

    /**
     * Define el valor de la propiedad respuestaListaParametricasLeyendas.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaListaParametricasLeyendas }
     *     
     */
    public void setRespuestaListaParametricasLeyendas(RespuestaListaParametricasLeyendas value) {
        this.respuestaListaParametricasLeyendas = value;
    }

}
