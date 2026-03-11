
package bo.gob.impuestos.sfe.creditdebitnoteinvoice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the bo.gob.impuestos.sfe.creditdebitnoteinvoice package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AnulacionDocumentoAjuste_QNAME = new QName("https://siat.impuestos.gob.bo/", "anulacionDocumentoAjuste");
    private final static QName _VerificarComunicacion_QNAME = new QName("https://siat.impuestos.gob.bo/", "verificarComunicacion");
    private final static QName _VerificarComunicacionResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "verificarComunicacionResponse");
    private final static QName _VerificacionEstadoDocumentoAjusteResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "verificacionEstadoDocumentoAjusteResponse");
    private final static QName _RecepcionDocumentoAjuste_QNAME = new QName("https://siat.impuestos.gob.bo/", "recepcionDocumentoAjuste");
    private final static QName _VerificacionEstadoDocumentoAjuste_QNAME = new QName("https://siat.impuestos.gob.bo/", "verificacionEstadoDocumentoAjuste");
    private final static QName _AnulacionDocumentoAjusteResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "anulacionDocumentoAjusteResponse");
    private final static QName _RecepcionDocumentoAjusteResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "recepcionDocumentoAjusteResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: bo.gob.impuestos.sfe.creditdebitnoteinvoice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AnulacionDocumentoAjusteResponse }
     * 
     */
    public AnulacionDocumentoAjusteResponse createAnulacionDocumentoAjusteResponse() {
        return new AnulacionDocumentoAjusteResponse();
    }

    /**
     * Create an instance of {@link RecepcionDocumentoAjusteResponse }
     * 
     */
    public RecepcionDocumentoAjusteResponse createRecepcionDocumentoAjusteResponse() {
        return new RecepcionDocumentoAjusteResponse();
    }

    /**
     * Create an instance of {@link VerificacionEstadoDocumentoAjusteResponse }
     * 
     */
    public VerificacionEstadoDocumentoAjusteResponse createVerificacionEstadoDocumentoAjusteResponse() {
        return new VerificacionEstadoDocumentoAjusteResponse();
    }

    /**
     * Create an instance of {@link VerificarComunicacion }
     * 
     */
    public VerificarComunicacion createVerificarComunicacion() {
        return new VerificarComunicacion();
    }

    /**
     * Create an instance of {@link VerificarComunicacionResponse }
     * 
     */
    public VerificarComunicacionResponse createVerificarComunicacionResponse() {
        return new VerificarComunicacionResponse();
    }

    /**
     * Create an instance of {@link AnulacionDocumentoAjuste }
     * 
     */
    public AnulacionDocumentoAjuste createAnulacionDocumentoAjuste() {
        return new AnulacionDocumentoAjuste();
    }

    /**
     * Create an instance of {@link VerificacionEstadoDocumentoAjuste }
     * 
     */
    public VerificacionEstadoDocumentoAjuste createVerificacionEstadoDocumentoAjuste() {
        return new VerificacionEstadoDocumentoAjuste();
    }

    /**
     * Create an instance of {@link RecepcionDocumentoAjuste }
     * 
     */
    public RecepcionDocumentoAjuste createRecepcionDocumentoAjuste() {
        return new RecepcionDocumentoAjuste();
    }

    /**
     * Create an instance of {@link RespuestaComunicacion }
     * 
     */
    public RespuestaComunicacion createRespuestaComunicacion() {
        return new RespuestaComunicacion();
    }

    /**
     * Create an instance of {@link SolicitudAnulacion }
     * 
     */
    public SolicitudAnulacion createSolicitudAnulacion() {
        return new SolicitudAnulacion();
    }

    /**
     * Create an instance of {@link MensajeRecepcion }
     * 
     */
    public MensajeRecepcion createMensajeRecepcion() {
        return new MensajeRecepcion();
    }

    /**
     * Create an instance of {@link RespuestaRecepcion }
     * 
     */
    public RespuestaRecepcion createRespuestaRecepcion() {
        return new RespuestaRecepcion();
    }

    /**
     * Create an instance of {@link SolicitudRecepcion }
     * 
     */
    public SolicitudRecepcion createSolicitudRecepcion() {
        return new SolicitudRecepcion();
    }

    /**
     * Create an instance of {@link SolicitudVerificacionEstado }
     * 
     */
    public SolicitudVerificacionEstado createSolicitudVerificacionEstado() {
        return new SolicitudVerificacionEstado();
    }

    /**
     * Create an instance of {@link MensajeServicio }
     * 
     */
    public MensajeServicio createMensajeServicio() {
        return new MensajeServicio();
    }

    /**
     * Create an instance of {@link SolicitudRecepcionFactura }
     * 
     */
    public SolicitudRecepcionFactura createSolicitudRecepcionFactura() {
        return new SolicitudRecepcionFactura();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AnulacionDocumentoAjuste }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "anulacionDocumentoAjuste")
    public JAXBElement<AnulacionDocumentoAjuste> createAnulacionDocumentoAjuste(AnulacionDocumentoAjuste value) {
        return new JAXBElement<AnulacionDocumentoAjuste>(_AnulacionDocumentoAjuste_QNAME, AnulacionDocumentoAjuste.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerificarComunicacion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "verificarComunicacion")
    public JAXBElement<VerificarComunicacion> createVerificarComunicacion(VerificarComunicacion value) {
        return new JAXBElement<VerificarComunicacion>(_VerificarComunicacion_QNAME, VerificarComunicacion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerificarComunicacionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "verificarComunicacionResponse")
    public JAXBElement<VerificarComunicacionResponse> createVerificarComunicacionResponse(VerificarComunicacionResponse value) {
        return new JAXBElement<VerificarComunicacionResponse>(_VerificarComunicacionResponse_QNAME, VerificarComunicacionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerificacionEstadoDocumentoAjusteResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "verificacionEstadoDocumentoAjusteResponse")
    public JAXBElement<VerificacionEstadoDocumentoAjusteResponse> createVerificacionEstadoDocumentoAjusteResponse(VerificacionEstadoDocumentoAjusteResponse value) {
        return new JAXBElement<VerificacionEstadoDocumentoAjusteResponse>(_VerificacionEstadoDocumentoAjusteResponse_QNAME, VerificacionEstadoDocumentoAjusteResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecepcionDocumentoAjuste }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "recepcionDocumentoAjuste")
    public JAXBElement<RecepcionDocumentoAjuste> createRecepcionDocumentoAjuste(RecepcionDocumentoAjuste value) {
        return new JAXBElement<RecepcionDocumentoAjuste>(_RecepcionDocumentoAjuste_QNAME, RecepcionDocumentoAjuste.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerificacionEstadoDocumentoAjuste }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "verificacionEstadoDocumentoAjuste")
    public JAXBElement<VerificacionEstadoDocumentoAjuste> createVerificacionEstadoDocumentoAjuste(VerificacionEstadoDocumentoAjuste value) {
        return new JAXBElement<VerificacionEstadoDocumentoAjuste>(_VerificacionEstadoDocumentoAjuste_QNAME, VerificacionEstadoDocumentoAjuste.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AnulacionDocumentoAjusteResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "anulacionDocumentoAjusteResponse")
    public JAXBElement<AnulacionDocumentoAjusteResponse> createAnulacionDocumentoAjusteResponse(AnulacionDocumentoAjusteResponse value) {
        return new JAXBElement<AnulacionDocumentoAjusteResponse>(_AnulacionDocumentoAjusteResponse_QNAME, AnulacionDocumentoAjusteResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecepcionDocumentoAjusteResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "recepcionDocumentoAjusteResponse")
    public JAXBElement<RecepcionDocumentoAjusteResponse> createRecepcionDocumentoAjusteResponse(RecepcionDocumentoAjusteResponse value) {
        return new JAXBElement<RecepcionDocumentoAjusteResponse>(_RecepcionDocumentoAjusteResponse_QNAME, RecepcionDocumentoAjusteResponse.class, null, value);
    }

}
