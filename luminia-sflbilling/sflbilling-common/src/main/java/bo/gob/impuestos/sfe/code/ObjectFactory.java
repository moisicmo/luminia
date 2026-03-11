
package bo.gob.impuestos.sfe.code;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the bo.gob.impuestos.sfe.code package. 
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

    private final static QName _VerificarNit_QNAME = new QName("https://siat.impuestos.gob.bo/", "verificarNit");
    private final static QName _CufdMasivoResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "cufdMasivoResponse");
    private final static QName _CuisResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "cuisResponse");
    private final static QName _VerificarComunicacion_QNAME = new QName("https://siat.impuestos.gob.bo/", "verificarComunicacion");
    private final static QName _VerificarComunicacionResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "verificarComunicacionResponse");
    private final static QName _CufdResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "cufdResponse");
    private final static QName _CuisMasivo_QNAME = new QName("https://siat.impuestos.gob.bo/", "cuisMasivo");
    private final static QName _CuisMasivoResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "cuisMasivoResponse");
    private final static QName _Cufd_QNAME = new QName("https://siat.impuestos.gob.bo/", "cufd");
    private final static QName _NotificaCertificadoRevocadoResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "notificaCertificadoRevocadoResponse");
    private final static QName _Cuis_QNAME = new QName("https://siat.impuestos.gob.bo/", "cuis");
    private final static QName _NotificaCertificadoRevocado_QNAME = new QName("https://siat.impuestos.gob.bo/", "notificaCertificadoRevocado");
    private final static QName _VerificarNitResponse_QNAME = new QName("https://siat.impuestos.gob.bo/", "verificarNitResponse");
    private final static QName _CufdMasivo_QNAME = new QName("https://siat.impuestos.gob.bo/", "cufdMasivo");
    private final static QName _SolicitudCufdCodigoPuntoVenta_QNAME = new QName("", "codigoPuntoVenta");
    private final static QName _SolicitudNotifcaRevocadoFechaRevocacion_QNAME = new QName("", "fechaRevocacion");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: bo.gob.impuestos.sfe.code
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link VerificarNit }
     * 
     */
    public VerificarNit createVerificarNit() {
        return new VerificarNit();
    }

    /**
     * Create an instance of {@link Cuis }
     * 
     */
    public Cuis createCuis() {
        return new Cuis();
    }

    /**
     * Create an instance of {@link NotificaCertificadoRevocado }
     * 
     */
    public NotificaCertificadoRevocado createNotificaCertificadoRevocado() {
        return new NotificaCertificadoRevocado();
    }

    /**
     * Create an instance of {@link CufdMasivo }
     * 
     */
    public CufdMasivo createCufdMasivo() {
        return new CufdMasivo();
    }

    /**
     * Create an instance of {@link VerificarNitResponse }
     * 
     */
    public VerificarNitResponse createVerificarNitResponse() {
        return new VerificarNitResponse();
    }

    /**
     * Create an instance of {@link CuisMasivo }
     * 
     */
    public CuisMasivo createCuisMasivo() {
        return new CuisMasivo();
    }

    /**
     * Create an instance of {@link CufdResponse }
     * 
     */
    public CufdResponse createCufdResponse() {
        return new CufdResponse();
    }

    /**
     * Create an instance of {@link CufdMasivoResponse }
     * 
     */
    public CufdMasivoResponse createCufdMasivoResponse() {
        return new CufdMasivoResponse();
    }

    /**
     * Create an instance of {@link CuisResponse }
     * 
     */
    public CuisResponse createCuisResponse() {
        return new CuisResponse();
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
     * Create an instance of {@link Cufd }
     * 
     */
    public Cufd createCufd() {
        return new Cufd();
    }

    /**
     * Create an instance of {@link NotificaCertificadoRevocadoResponse }
     * 
     */
    public NotificaCertificadoRevocadoResponse createNotificaCertificadoRevocadoResponse() {
        return new NotificaCertificadoRevocadoResponse();
    }

    /**
     * Create an instance of {@link CuisMasivoResponse }
     * 
     */
    public CuisMasivoResponse createCuisMasivoResponse() {
        return new CuisMasivoResponse();
    }

    /**
     * Create an instance of {@link RespuestaComunicacion }
     * 
     */
    public RespuestaComunicacion createRespuestaComunicacion() {
        return new RespuestaComunicacion();
    }

    /**
     * Create an instance of {@link RespuestaListaRegistroCufdSoapDto }
     * 
     */
    public RespuestaListaRegistroCufdSoapDto createRespuestaListaRegistroCufdSoapDto() {
        return new RespuestaListaRegistroCufdSoapDto();
    }

    /**
     * Create an instance of {@link SolicitudCuis }
     * 
     */
    public SolicitudCuis createSolicitudCuis() {
        return new SolicitudCuis();
    }

    /**
     * Create an instance of {@link RespuestaCufd }
     * 
     */
    public RespuestaCufd createRespuestaCufd() {
        return new RespuestaCufd();
    }

    /**
     * Create an instance of {@link RespuestaConfiguracion }
     * 
     */
    public RespuestaConfiguracion createRespuestaConfiguracion() {
        return new RespuestaConfiguracion();
    }

    /**
     * Create an instance of {@link SolicitudCufd }
     * 
     */
    public SolicitudCufd createSolicitudCufd() {
        return new SolicitudCufd();
    }

    /**
     * Create an instance of {@link RespuestaListaRegistroCuisSoapDto }
     * 
     */
    public RespuestaListaRegistroCuisSoapDto createRespuestaListaRegistroCuisSoapDto() {
        return new RespuestaListaRegistroCuisSoapDto();
    }

    /**
     * Create an instance of {@link SolicitudCuisMasivoSistemas }
     * 
     */
    public SolicitudCuisMasivoSistemas createSolicitudCuisMasivoSistemas() {
        return new SolicitudCuisMasivoSistemas();
    }

    /**
     * Create an instance of {@link SolicitudNotifcaRevocado }
     * 
     */
    public SolicitudNotifcaRevocado createSolicitudNotifcaRevocado() {
        return new SolicitudNotifcaRevocado();
    }

    /**
     * Create an instance of {@link RespuestaCufdMasivo }
     * 
     */
    public RespuestaCufdMasivo createRespuestaCufdMasivo() {
        return new RespuestaCufdMasivo();
    }

    /**
     * Create an instance of {@link RespuestaVerificarNit }
     * 
     */
    public RespuestaVerificarNit createRespuestaVerificarNit() {
        return new RespuestaVerificarNit();
    }

    /**
     * Create an instance of {@link RespuestaNotificaRevocado }
     * 
     */
    public RespuestaNotificaRevocado createRespuestaNotificaRevocado() {
        return new RespuestaNotificaRevocado();
    }

    /**
     * Create an instance of {@link RespuestaCuisMasivo }
     * 
     */
    public RespuestaCuisMasivo createRespuestaCuisMasivo() {
        return new RespuestaCuisMasivo();
    }

    /**
     * Create an instance of {@link SolicitudCufdMasivo }
     * 
     */
    public SolicitudCufdMasivo createSolicitudCufdMasivo() {
        return new SolicitudCufdMasivo();
    }

    /**
     * Create an instance of {@link MensajeServicio }
     * 
     */
    public MensajeServicio createMensajeServicio() {
        return new MensajeServicio();
    }

    /**
     * Create an instance of {@link SolicitudListaCuisDto }
     * 
     */
    public SolicitudListaCuisDto createSolicitudListaCuisDto() {
        return new SolicitudListaCuisDto();
    }

    /**
     * Create an instance of {@link SolicitudListaCufdDto }
     * 
     */
    public SolicitudListaCufdDto createSolicitudListaCufdDto() {
        return new SolicitudListaCufdDto();
    }

    /**
     * Create an instance of {@link SolicitudVerificarNit }
     * 
     */
    public SolicitudVerificarNit createSolicitudVerificarNit() {
        return new SolicitudVerificarNit();
    }

    /**
     * Create an instance of {@link RespuestaCuis }
     * 
     */
    public RespuestaCuis createRespuestaCuis() {
        return new RespuestaCuis();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerificarNit }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "verificarNit")
    public JAXBElement<VerificarNit> createVerificarNit(VerificarNit value) {
        return new JAXBElement<VerificarNit>(_VerificarNit_QNAME, VerificarNit.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CufdMasivoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "cufdMasivoResponse")
    public JAXBElement<CufdMasivoResponse> createCufdMasivoResponse(CufdMasivoResponse value) {
        return new JAXBElement<CufdMasivoResponse>(_CufdMasivoResponse_QNAME, CufdMasivoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CuisResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "cuisResponse")
    public JAXBElement<CuisResponse> createCuisResponse(CuisResponse value) {
        return new JAXBElement<CuisResponse>(_CuisResponse_QNAME, CuisResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link CufdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "cufdResponse")
    public JAXBElement<CufdResponse> createCufdResponse(CufdResponse value) {
        return new JAXBElement<CufdResponse>(_CufdResponse_QNAME, CufdResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CuisMasivo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "cuisMasivo")
    public JAXBElement<CuisMasivo> createCuisMasivo(CuisMasivo value) {
        return new JAXBElement<CuisMasivo>(_CuisMasivo_QNAME, CuisMasivo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CuisMasivoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "cuisMasivoResponse")
    public JAXBElement<CuisMasivoResponse> createCuisMasivoResponse(CuisMasivoResponse value) {
        return new JAXBElement<CuisMasivoResponse>(_CuisMasivoResponse_QNAME, CuisMasivoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Cufd }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "cufd")
    public JAXBElement<Cufd> createCufd(Cufd value) {
        return new JAXBElement<Cufd>(_Cufd_QNAME, Cufd.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificaCertificadoRevocadoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "notificaCertificadoRevocadoResponse")
    public JAXBElement<NotificaCertificadoRevocadoResponse> createNotificaCertificadoRevocadoResponse(NotificaCertificadoRevocadoResponse value) {
        return new JAXBElement<NotificaCertificadoRevocadoResponse>(_NotificaCertificadoRevocadoResponse_QNAME, NotificaCertificadoRevocadoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Cuis }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "cuis")
    public JAXBElement<Cuis> createCuis(Cuis value) {
        return new JAXBElement<Cuis>(_Cuis_QNAME, Cuis.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificaCertificadoRevocado }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "notificaCertificadoRevocado")
    public JAXBElement<NotificaCertificadoRevocado> createNotificaCertificadoRevocado(NotificaCertificadoRevocado value) {
        return new JAXBElement<NotificaCertificadoRevocado>(_NotificaCertificadoRevocado_QNAME, NotificaCertificadoRevocado.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerificarNitResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "verificarNitResponse")
    public JAXBElement<VerificarNitResponse> createVerificarNitResponse(VerificarNitResponse value) {
        return new JAXBElement<VerificarNitResponse>(_VerificarNitResponse_QNAME, VerificarNitResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CufdMasivo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://siat.impuestos.gob.bo/", name = "cufdMasivo")
    public JAXBElement<CufdMasivo> createCufdMasivo(CufdMasivo value) {
        return new JAXBElement<CufdMasivo>(_CufdMasivo_QNAME, CufdMasivo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "codigoPuntoVenta", scope = SolicitudCufd.class)
    public JAXBElement<Integer> createSolicitudCufdCodigoPuntoVenta(Integer value) {
        return new JAXBElement<Integer>(_SolicitudCufdCodigoPuntoVenta_QNAME, Integer.class, SolicitudCufd.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "codigoPuntoVenta", scope = SolicitudCuis.class)
    public JAXBElement<Integer> createSolicitudCuisCodigoPuntoVenta(Integer value) {
        return new JAXBElement<Integer>(_SolicitudCufdCodigoPuntoVenta_QNAME, Integer.class, SolicitudCuis.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "fechaRevocacion", scope = SolicitudNotifcaRevocado.class)
    public JAXBElement<XMLGregorianCalendar> createSolicitudNotifcaRevocadoFechaRevocacion(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_SolicitudNotifcaRevocadoFechaRevocacion_QNAME, XMLGregorianCalendar.class, SolicitudNotifcaRevocado.class, value);
    }

}
