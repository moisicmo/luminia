package bo.com.luminia.sflbilling.msaccount.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    @Value("${siat.soap-service}")
    private String siatSoapService;
    public static final String SIAT_SOAP_SERVICE = "siat.soap-service";
}
