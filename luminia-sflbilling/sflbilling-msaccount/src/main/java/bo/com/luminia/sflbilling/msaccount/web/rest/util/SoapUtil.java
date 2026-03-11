package bo.com.luminia.sflbilling.msaccount.web.rest.util;

import bo.com.luminia.sflbilling.msaccount.config.ApplicationProperties;
import bo.gob.impuestos.sfe.operation.ServicioFacturacionOperaciones;
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

    private final static String OPERATION_SERVICE = "FacturacionOperaciones?wsdl";
    private final static QName OPERATION_SERVICE_QNAME = new QName("https://siat.impuestos.gob.bo/FacturacionOperaciones", "ServicioFacturacionOperaciones");

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
}
