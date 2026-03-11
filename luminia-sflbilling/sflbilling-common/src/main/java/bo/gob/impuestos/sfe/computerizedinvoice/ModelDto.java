
package bo.gob.impuestos.sfe.computerizedinvoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para modelDto complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="modelDto">
 *   &lt;complexContent>
 *     &lt;extension base="{https://siat.impuestos.gob.bo/}model">
 *       &lt;sequence>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modelDto")
@XmlSeeAlso({
    RespuestaComunicacion.class,
    RespuestaRecepcion.class,
    SolicitudRecepcion.class,
    MensajeServicio.class
})
public abstract class ModelDto
    extends Model
{


}
