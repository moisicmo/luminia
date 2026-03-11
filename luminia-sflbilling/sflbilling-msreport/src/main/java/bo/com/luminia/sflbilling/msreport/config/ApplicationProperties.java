package bo.com.luminia.sflbilling.msreport.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Getter
@Setter
public class ApplicationProperties {
    @Value("${siat.url}")
    private String siatUrl;

    @Value("${notification.endpoint}")
    private String notificationEndpoint;

    @Value("${notification.client-id}")
    private String notificationClientId;

    @Value("${notification.api-key}")
    private String notificationApiKey;

    public static final String NOTIFICATION_ENDPOINT = "notification.endpoint";
}
