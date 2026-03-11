package bo.com.luminia.sflbilling.msbatch.web.rest.util;

import bo.com.luminia.sflbilling.msbatch.config.ApplicationProperties;
import bo.gob.impuestos.sfe.code.ServicioFacturacionCodigos;
import bo.gob.impuestos.sfe.operation.ServicioFacturacionOperaciones;
import bo.gob.impuestos.sfe.synchronization.ServicioFacturacionSincronizacion;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

@AllArgsConstructor
@org.springframework.stereotype.Service
public class SoapUtil {

    private final Environment environment;

    private final static String CODE_SERVICE = "FacturacionCodigos?wsdl";
    private final static QName CODE_SERVICE_QNAME = new QName("https://siat.impuestos.gob.bo/FacturacionCodigos", "ServicioFacturacionCodigos");

    private final static String PARAMETER_SERVICE = "FacturacionSincronizacion?wsdl";
    private final static QName PARAMETER_SERVICE_QNAME = new QName("https://siat.impuestos.gob.bo/FacturacionSincronizacion", "ServicioFacturacionSincronizacion");

    private final static String OPERATION_SERVICE = "FacturacionOperaciones?wsdl";
    private final static QName OPERATION_SERVICE_QNAME = new QName("https://siat.impuestos.gob.bo/FacturacionOperaciones", "ServicioFacturacionOperaciones");

    private final static String BUY_SELL_SERVICE = "ServicioFacturacionCompraVenta?wsdl";
    private final static QName BUY_SELL_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionCompraVenta", "ServicioFacturacion");

    private final static String ELECTRONIC_SERVICE = "ServicioFacturacionElectronica?wsdl";
    private final static QName ELECTRONIC_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionElectronica", "ServicioFacturacion");

    private final static String COMPUTERIZED_SERVICE = "ServicioFacturacionComputarizada?wsdl";
    private final static QName COMPUTERIZED_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionComputarizada", "ServicioFacturacion");

    private final static String BASIC_SERVICE = "ServicioFacturacionServicioBasico?wsdl";
    private final static QName BASIC_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionServicioBasico", "ServicioFacturacion");

    private final static String FINANCIAL_SERVICE = "ServicioFacturacionEntidadFinanciera?wsdl";
    private final static QName FINANCIAL_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionEntidadFinanciera", "ServicioFacturacion");

    private final static String TELECOMMUNICATIONS_SERVICE = "ServicioFacturacionTelecomunicaciones?wsdl";
    private final static QName TELECOMMUNICATIONS_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionTelecomunicaciones", "ServicioFacturacion");

    private final static String AIR_TICKET_SERVICE = "ServicioFacturacionBoletoAereo?wsdl";
    private final static QName AIR_TICKET_QNAME = new QName("https://siat.impuestos.gob.bo/ServicioFacturacionBoletoAereo", "ServicioFacturacion");

    public ServicioFacturacionCodigos getCodeService(String token) {
        URL url = null;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + CODE_SERVICE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service codeService = Service.create(url, CODE_SERVICE_QNAME);
        ServicioFacturacionCodigos service = codeService.getPort(ServicioFacturacionCodigos.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apikey", Collections.singletonList("TokenApi " + token)));

        return service;
    }

    public ServicioFacturacionSincronizacion getSyncService(String token) {
        URL url = null;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + PARAMETER_SERVICE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service codeService = Service.create(url, PARAMETER_SERVICE_QNAME);
        ServicioFacturacionSincronizacion service = codeService.getPort(ServicioFacturacionSincronizacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apikey", Collections.singletonList("TokenApi " + token)));

        assignBatchTimeout((BindingProvider)service);

        return service;
    }

    public ServicioFacturacionOperaciones getOperationService(String token) {
        URL url = null;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + OPERATION_SERVICE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service codeService = Service.create(url, OPERATION_SERVICE_QNAME);
        ServicioFacturacionOperaciones service = codeService.getPort(ServicioFacturacionOperaciones.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apikey", Collections.singletonList("TokenApi " + token)));

        return service;
    }

    public bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion getBuySellService(String token) {
        URL url = null;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + BUY_SELL_SERVICE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, BUY_SELL_QNAME);
        bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }

    public bo.gob.impuestos.sfe.electronicinvoice.ServicioFacturacion getElectronicService(String token) {
        URL url = null;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + ELECTRONIC_SERVICE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, ELECTRONIC_QNAME);
        bo.gob.impuestos.sfe.electronicinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.electronicinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }

    public bo.gob.impuestos.sfe.computerizedinvoice.ServicioFacturacion getComputerizedService(String token) {
        URL url = null;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + COMPUTERIZED_SERVICE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, COMPUTERIZED_QNAME);
        bo.gob.impuestos.sfe.computerizedinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.computerizedinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }

    public bo.gob.impuestos.sfe.basicserviceinvoice.ServicioFacturacion getBasicService(String token) {
        URL url = null;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + BASIC_SERVICE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, BASIC_QNAME);
        bo.gob.impuestos.sfe.basicserviceinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.basicserviceinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }

    public bo.gob.impuestos.sfe.financialinvoice.ServicioFacturacion getFinancialService(String token) {
        URL url = null;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + FINANCIAL_SERVICE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, FINANCIAL_QNAME);
        bo.gob.impuestos.sfe.financialinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.financialinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }

    public bo.gob.impuestos.sfe.telecommunicationinvoice.ServicioFacturacion getTelecommunicationsService(String token) {
        URL url = null;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + TELECOMMUNICATIONS_SERVICE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, TELECOMMUNICATIONS_QNAME);
        bo.gob.impuestos.sfe.telecommunicationinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.telecommunicationinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }

    public bo.gob.impuestos.sfe.airticketinvoice.ServicioFacturacion getAirTicketService(String token) {
        URL url = null;
        try {
            url = new URL(environment.getProperty(ApplicationProperties.SIAT_SOAP_SERVICE) + AIR_TICKET_SERVICE);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Service invoiceService = Service.create(url, AIR_TICKET_QNAME);
        bo.gob.impuestos.sfe.airticketinvoice.ServicioFacturacion service = invoiceService.getPort(bo.gob.impuestos.sfe.airticketinvoice.ServicioFacturacion.class);
        ((BindingProvider) service).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
            Collections.singletonMap("apiKey", Collections.singletonList("TokenApi " + token)));
        return service;
    }

    private void assignBatchTimeout(BindingProvider service) {
        int timeoutConnect = 300;
        int timeoutRequest = 15000;

        try {

            timeoutConnect = Integer.parseInt(environment.getProperty(ApplicationProperties.BATCH_SIAT_SOAP_TIMEOUT_CONNECT));
            timeoutRequest = Integer.parseInt(environment.getProperty(ApplicationProperties.BATCH_SIAT_SOAP_TIMEOUT_REQUEST));

        } catch (Exception e) {
            e.printStackTrace();
        }

        //timeout for try to connect to service (tip: should be a low value)
        //in milliseconds
        service.getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", timeoutConnect);

        //timeout for try to receive the request/response.
        //in milliseconds
        service.getRequestContext().put("com.sun.xml.internal.ws.request.timeout", timeoutRequest);
    }


}
