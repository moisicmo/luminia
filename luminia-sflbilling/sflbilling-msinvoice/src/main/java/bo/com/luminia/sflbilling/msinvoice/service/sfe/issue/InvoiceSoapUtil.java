package bo.com.luminia.sflbilling.msinvoice.service.sfe.issue;

import bo.com.luminia.sflbilling.msinvoice.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msinvoice.service.sfe.jaxlogging.JaxWsHandlerResolver;
import bo.gob.impuestos.sfe.code.ServicioFacturacionCodigos;
import bo.gob.impuestos.sfe.synchronization.ServicioFacturacionSincronizacion;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import java.net.URL;
import java.util.Collections;

@Slf4j
@AllArgsConstructor
@org.springframework.stereotype.Service
public class InvoiceSoapUtil {

    private final Environment environment;

    private final static String BUY_SELL_SERVICE = "ServicioFacturacionCompraVenta?wsdl";
    private final static QName BUY_SELL_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionCompraVenta", "ServicioFacturacion");

    private final static String CREDIT_DEBIT_SERVICE = "ServicioFacturacionDocumentoAjuste?wsdl";
    private final static QName CREDIT_DEBIT_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionDocumentoAjuste", "ServicioFacturacion");

    private final static String COMPUTERIZED_SERVICE = "ServicioFacturacionComputarizada?wsdl";
    private final static QName COMPUTERIZED_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionComputarizada", "ServicioFacturacion");

    private final static String ELECTRONIC_SERVICE = "ServicioFacturacionElectronica?wsdl";
    private final static QName ELECTRONIC_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionElectronica", "ServicioFacturacion");

    private final static String BASIC_SERVICE = "ServicioFacturacionServicioBasico?wsdl";
    private final static QName BASIC_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionServicioBasico", "ServicioFacturacion");

    private final static String FINANCIAL_SERVICE = "ServicioFacturacionEntidadFinanciera?wsdl";
    private final static QName FINANCIAL_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionEntidadFinanciera", "ServicioFacturacion");

    private final static String TELECOMUNICACIONES_SERVICE = "ServicioFacturacionTelecomunicaciones?wsdl";
    private final static QName TELECOMUNICACIONES_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionTelecomunicaciones", "ServicioFacturacion");

    private final static String SINCRONIZACION_SERVICE = "FacturacionSincronizacion?wsdl";
    private final static QName SINCRONIZACION_QNAME = new QName("https://siat.impuestos.gob.bo/FacturacionSincronizacion", "ServicioFacturacionSincronizacion");

    private final static String AIR_TICKET_SERVICE = "ServicioFacturacionBoletoAereo?wsdl";
    private final static QName AIR_TICKET_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionBoletoAereo", "ServicioFacturacion");

    private final static String CODE_SERVICE = "FacturacionCodigos?wsdl";
    private final static QName CODE_SERVICE_QNAME = new QName("https://siat.impuestos.gob.bo/FacturacionCodigos", "ServicioFacturacionCodigos");


    @Cacheable(value="compraVentaService", key="#token")
    public bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion getCompraVentaService(String token) {
        log.info("WS Client CompraVentaService para: " + token);

        URL url = null;
        boolean enableHAndlerInvoice = false;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + BUY_SELL_SERVICE);
            enableHAndlerInvoice = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.SIAT_SOAP_ENABLE_HANDLER_INVOICE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, BUY_SELL_QNAME);
        //Handler writes a lot, enable only for massive debug information
        if (enableHAndlerInvoice) {
            invoiceService.setHandlerResolver(new JaxWsHandlerResolver());
        }else{
            log.info("Loghandler deshabilitado");
        }
        bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }

    @Cacheable(value="creditoDebitoService", key="#token")
    public bo.gob.impuestos.sfe.creditdebitnoteinvoice.ServicioFacturacion getCreditoDebitoService(String token) {
        log.info("WS Client CreditoDebitoService para: " + token);

        URL url = null;
        boolean enableHAndlerInvoice = false;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + CREDIT_DEBIT_SERVICE);
            enableHAndlerInvoice = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.SIAT_SOAP_ENABLE_HANDLER_INVOICE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, CREDIT_DEBIT_QNAME);
        //Handler writes a lot, enable only for massive debug information
        if (enableHAndlerInvoice) {
            invoiceService.setHandlerResolver(new JaxWsHandlerResolver());
        }else{
            log.info("Loghandler deshabilitado");
        }
        bo.gob.impuestos.sfe.creditdebitnoteinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.creditdebitnoteinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }

    @Cacheable(value="computarizadaInvoice", key="#token")
    public bo.gob.impuestos.sfe.computerizedinvoice.ServicioFacturacion getComputarizadaInvoice(String token) {
        log.info("WS Client ComputarizadaInvoice para: " + token);

        URL url = null;
        boolean enableHAndlerInvoice = false;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + COMPUTERIZED_SERVICE);
            enableHAndlerInvoice = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.SIAT_SOAP_ENABLE_HANDLER_INVOICE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, COMPUTERIZED_QNAME);
        //Handler writes a lot, enable only for massive debug information
        if (enableHAndlerInvoice) {
            invoiceService.setHandlerResolver(new JaxWsHandlerResolver());
        }else{
            log.info("Loghandler deshabilitado");
        }
        bo.gob.impuestos.sfe.computerizedinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.computerizedinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;

    }

    @Cacheable(value="electronicaInvoice", key="#token")
    public bo.gob.impuestos.sfe.electronicinvoice.ServicioFacturacion getElectronicaInvoice(String token) {
        log.info("WS Client ElectronicaInvoice para: " + token);

        URL url = null;
        boolean enableHAndlerInvoice = false;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + ELECTRONIC_SERVICE);
            enableHAndlerInvoice = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.SIAT_SOAP_ENABLE_HANDLER_INVOICE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, ELECTRONIC_QNAME);
        //Handler writes a lot, enable only for massive debug information
        if (enableHAndlerInvoice) {
            invoiceService.setHandlerResolver(new JaxWsHandlerResolver());
        }else{
            log.info("Loghandler deshabilitado");
        }
        bo.gob.impuestos.sfe.electronicinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.electronicinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }

    @Cacheable(value="serviciosBasicos", key="#token")
    public bo.gob.impuestos.sfe.basicserviceinvoice.ServicioFacturacion getServiciosBasicos(String token) {
        log.info("WS Client ServiciosBasicos para: " + token);

        URL url = null;
        boolean enableHAndlerInvoice = false;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + BASIC_SERVICE);
            enableHAndlerInvoice = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.SIAT_SOAP_ENABLE_HANDLER_INVOICE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, BASIC_QNAME);
        //Handler writes a lot, enable only for massive debug information
        if (enableHAndlerInvoice) {
            invoiceService.setHandlerResolver(new JaxWsHandlerResolver());
        }else{
            log.info("Loghandler deshabilitado");
        }
        bo.gob.impuestos.sfe.basicserviceinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.basicserviceinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }

    @Cacheable(value="entidadesFinancieras", key="#token")
    public bo.gob.impuestos.sfe.financialinvoice.ServicioFacturacion getEntidadesFinancieras(String token) {
        log.info("WS Client EntidadesFinancieras para: " + token);

        URL url = null;
        boolean enableHAndlerInvoice = false;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + FINANCIAL_SERVICE);
            enableHAndlerInvoice = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.SIAT_SOAP_ENABLE_HANDLER_INVOICE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, FINANCIAL_QNAME);
        //Handler writes a lot, enable only for massive debug information
        if (enableHAndlerInvoice) {
            invoiceService.setHandlerResolver(new JaxWsHandlerResolver());
        }else{
            log.info("Loghandler deshabilitado");
        }
        bo.gob.impuestos.sfe.financialinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.financialinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }

    @Cacheable(value="telecomunicacionesInvoice", key="#token")
    public bo.gob.impuestos.sfe.telecommunicationinvoice.ServicioFacturacion getTelecomunicacionesInvoice(String token) {
        log.info("WS Client TelecomunicacionesInvoice para: " + token);

        URL url = null;
        boolean enableHAndlerInvoice = false;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + TELECOMUNICACIONES_SERVICE);
            enableHAndlerInvoice = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.SIAT_SOAP_ENABLE_HANDLER_INVOICE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, TELECOMUNICACIONES_QNAME);
        //Handler writes a lot, enable only for massive debug information
        if (enableHAndlerInvoice) {
            invoiceService.setHandlerResolver(new JaxWsHandlerResolver());
        }else{
            log.info("Loghandler deshabilitado");
        }
        bo.gob.impuestos.sfe.telecommunicationinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.telecommunicationinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }

    @Cacheable(value="boletoAereo", key="#token")
    public bo.gob.impuestos.sfe.airticketinvoice.ServicioFacturacion getBoletoAereo(String token) {
        log.info("WS Client BoletoAereo para: " + token);

        URL url = null;
        boolean enableHAndlerInvoice = false;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + AIR_TICKET_SERVICE);
            enableHAndlerInvoice = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.SIAT_SOAP_ENABLE_HANDLER_INVOICE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, AIR_TICKET_QNAME);
        //Handler writes a lot, enable only for massive debug information
        if (enableHAndlerInvoice) {
            invoiceService.setHandlerResolver(new JaxWsHandlerResolver());
        }else{
            log.info("Loghandler deshabilitado");
        }
        bo.gob.impuestos.sfe.airticketinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.airticketinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }

    @Cacheable(value="sincronizacion", key="#token")
    public ServicioFacturacionSincronizacion getSincronizacion(String token) {
        log.info("WS Client Sincronizacion para: " + token);

        URL url = null;
        boolean enableHAndlerInvoice = false;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + SINCRONIZACION_SERVICE);
            enableHAndlerInvoice = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.SIAT_SOAP_ENABLE_HANDLER_INVOICE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, SINCRONIZACION_QNAME);
        //Handler writes a lot, enable only for massive debug information
        if (enableHAndlerInvoice) {
            invoiceService.setHandlerResolver(new JaxWsHandlerResolver());
        }else{
            log.info("Loghandler deshabilitado");
        }
        ServicioFacturacionSincronizacion service = invoiceService.getPort(ServicioFacturacionSincronizacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }


    @Cacheable(value="codeService", key="#token")
    public ServicioFacturacionCodigos getCodeService(String token) {
        log.info("WS Client CodeService para: " + token);

        URL url = null;
        boolean enableHAndlerInvoice = false;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + CODE_SERVICE);
            enableHAndlerInvoice = Boolean.parseBoolean(environment.getProperty(ApplicationProperties.SIAT_SOAP_ENABLE_HANDLER_INVOICE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Service codeService = Service.create(url, CODE_SERVICE_QNAME);
        //Handler writes a lot, enable only for massive debug information
        if (enableHAndlerInvoice) {
            codeService.setHandlerResolver(new JaxWsHandlerResolver());
        }else{
            log.info("Loghandler deshabilitado");
        }
        ServicioFacturacionCodigos service = codeService.getPort(ServicioFacturacionCodigos.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apikey", Collections.singletonList("TokenApi " + token)));

        return service;
    }
}
