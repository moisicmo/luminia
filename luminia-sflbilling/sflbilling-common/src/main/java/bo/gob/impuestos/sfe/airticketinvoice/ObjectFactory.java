
package bo.gob.impuestos.sfe.airticketinvoice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the bo.gob.impuestos.sfe.airticketinvoice package. 
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

    private final static QName _VerificarComunicacion_QNAME = new QName("https://siat.impuestos.gob.bo/", "verificarComunicacion");
    private final static QName _VerificarComunicacionResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "verificarComunicacionResponse");
    private final static QName _RecepcionMasivaFacturaResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "recepcionMasivaFacturaResponse");
    private final static QName _ValidacionRecepcionMasivaFactura_QNAME = new QName("https://siat.impuestos.gob.bo/", "validacionRecepcionMasivaFactura");
    private final static QName _RecepcionMasivaFactura_QNAME = new QName("https://siat.impuestos.gob.bo/", "recepcionMasivaFactura");
    private final static QName _VerificacionEstadoFactura_QNAME = new QName("https://siat.impuestos.gob.bo/", "verificacionEstadoFactura");
    private final static QName _VerificacionEstadoFacturaResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "verificacionEstadoFacturaResponse");
    private final static QName _ValidacionRecepcionMasivaFacturaResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "validacionRecepcionMasivaFacturaResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: bo.gob.impuestos.sfe.airticketinvoice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link VerificacionEstadoFactura }
     * 
     */
    public VerificacionEstadoFactura createVerificacionEstadoFactura() {
        return new VerificacionEstadoFactura();
    }

    /**
     * Create an instance of {@link RecepcionMasivaFactura }
     * 
     */
    public RecepcionMasivaFactura createRecepcionMasivaFactura() {
        return new RecepcionMasivaFactura();
    }

    /**
     * Create an instance of {@link ValidacionRecepcionMasivaFactura }
     * 
     */
    public ValidacionRecepcionMasivaFactura createValidacionRecepcionMasivaFactura() {
        return new ValidacionRecepcionMasivaFactura();
    }

    /**
     * Create an instance of {@link ValidacionRecepcionMasivaFacturaResponse }
     * 
     */
    public ValidacionRecepcionMasivaFacturaResponse createValidacionRecepcionMasivaFacturaResponse() {
        return new ValidacionRecepcionMasivaFacturaResponse();
    }

    /**
     * Create an instance of {@link VerificacionEstadoFacturaResponse }
     * 
     */
    public VerificacionEstadoFacturaResponse createVerificacionEstadoFacturaResponse() {
        return new VerificacionEstadoFacturaResponse();
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
     * Create an instance of {@link RecepcionMasivaFacturaResponse }
     * 
     */
    public RecepcionMasivaFacturaResponse createRecepcionMasivaFacturaResponse() {
        return new RecepcionMasivaFacturaResponse();
    }

    /**
     * Create an instance of {@link SolicitudValidacionRecepcion }
     * 
     */
    public SolicitudValidacionRecepcion createSolicitudValidacionRecepcion() {
        return new SolicitudValidacionRecepcion();
    }

    /**
     * Create an instance of {@link RespuestaComunicacion }
     * 
     */
    public RespuestaComunicacion createRespuestaComunicacion() {
        return new RespuestaComunicacion();
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
     * Create an instance of {@link SolicitudRecepcionMasiva }
     * 
     */
    public SolicitudRecepcionMasiva createSolicitudRecepcionMasiva() {
        return new SolicitudRecepcionMasiva();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link RecepcionMasivaFacturaResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "recepcionMasivaFacturaResponse")
    public JAXBElement<RecepcionMasivaFacturaResponse> createRecepcionMasivaFacturaResponse(RecepcionMasivaFacturaResponse value) {
        return new JAXBElement<RecepcionMasivaFacturaResponse>(_RecepcionMasivaFacturaResponse_QNAME, RecepcionMasivaFacturaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidacionRecepcionMasivaFactura }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "validacionRecepcionMasivaFactura")
    public JAXBElement<ValidacionRecepcionMasivaFactura> createValidacionRecepcionMasivaFactura(ValidacionRecepcionMasivaFactura value) {
        return new JAXBElement<ValidacionRecepcionMasivaFactura>(_ValidacionRecepcionMasivaFactura_QNAME, ValidacionRecepcionMasivaFactura.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecepcionMasivaFactura }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "recepcionMasivaFactura")
    public JAXBElement<RecepcionMasivaFactura> createRecepcionMasivaFactura(RecepcionMasivaFactura value) {
        return new JAXBElement<RecepcionMasivaFactura>(_RecepcionMasivaFactura_QNAME, RecepcionMasivaFactura.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerificacionEstadoFactura }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "verificacionEstadoFactura")
    public JAXBElement<VerificacionEstadoFactura> createVerificacionEstadoFactura(VerificacionEstadoFactura value) {
        return new JAXBElement<VerificacionEstadoFactura>(_VerificacionEstadoFactura_QNAME, VerificacionEstadoFactura.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerificacionEstadoFacturaResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "verificacionEstadoFacturaResponse")
    public JAXBElement<VerificacionEstadoFacturaResponse> createVerificacionEstadoFacturaResponse(VerificacionEstadoFacturaResponse value) {
        return new JAXBElement<VerificacionEstadoFacturaResponse>(_VerificacionEstadoFacturaResponse_QNAME, VerificacionEstadoFacturaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidacionRecepcionMasivaFacturaResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "validacionRecepcionMasivaFacturaResponse")
    public JAXBElement<ValidacionRecepcionMasivaFacturaResponse> createValidacionRecepcionMasivaFacturaResponse(ValidacionRecepcionMasivaFacturaResponse value) {
        return new JAXBElement<ValidacionRecepcionMasivaFacturaResponse>(_ValidacionRecepcionMasivaFacturaResponse_QNAME, ValidacionRecepcionMasivaFacturaResponse.class, null, value);
    }

}
