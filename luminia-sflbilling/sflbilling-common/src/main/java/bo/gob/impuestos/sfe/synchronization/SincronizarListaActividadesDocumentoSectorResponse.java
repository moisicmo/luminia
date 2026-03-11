
package bo.gob.impuestos.sfe.synchronization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para sincronizarListaActividadesDocumentoSectorResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="sincronizarListaActividadesDocumentoSectorResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespuestaListaActividadesDocumentoSector" type="{https://siat.impuestos.gob.bo/}respuestaListaActividadesDocumentoSector" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sincronizarListaActividadesDocumentoSectorResponse", propOrder = {
    "respuestaListaActividadesDocumentoSector"
})
public class SincronizarListaActividadesDocumentoSectorResponse {

    @XmlElement(name = "RespuestaListaActividadesDocumentoSector")
    protected RespuestaListaActividadesDocumentoSector respuestaListaActividadesDocumentoSector;

    /**
     * Obtiene el valor de la propiedad respuestaListaActividadesDocumentoSector.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaListaActividadesDocumentoSector }
     *     
     */
    public RespuestaListaActividadesDocumentoSector getRespuestaListaActividadesDocumentoSector() {
        return respuestaListaActividadesDocumentoSector;
    }

    /**
     * Define el valor de la propiedad respuestaListaActividadesDocumentoSector.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaListaActividadesDocumentoSector }
     *     
     */
    public void setRespuestaListaActividadesDocumentoSector(RespuestaListaActividadesDocumentoSector value) {
        this.respuestaListaActividadesDocumentoSector = value;
    }

}
