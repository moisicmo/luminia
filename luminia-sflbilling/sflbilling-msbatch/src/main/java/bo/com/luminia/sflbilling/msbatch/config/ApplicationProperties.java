package bo.com.luminia.sflbilling.msbatch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    @Value("${expiration.days}")
    private String expirationDays;
    public static final String EXPIRATION_DAYS = "expiration.days";

    @Value("${notification.endpoint}")
    private String notificationEndpoint;
    public static final String NOTIFICATION_ENDPOINT = "notification.endpoint";

    @Value("${invoice.path-temp-folder}")
    private String invoicePathTempFolder;
    public static final String INVOICE_PATH_TEMP_FOLDER = "invoice.path-temp-folder";

    @Value("${invoice.number-massive}")
    private String invoiceNumberMassive;
    public static final String INVOICE_NUMBER_MASSIVE = "invoice.number-massive";

    @Value("${invoice.number-pack}")
    private String invoiceNumberPack;
    public static final String INVOICE_NUMBER_PACK = "invoice.number-pack";

    @Value("${offline.apiKey}")
    private String apiKey;
    public static final String OFFLINE_APIKEY = "offline.apiKey";

    @Value("${offline.try-number}")
    private String tryNumber;
    public static final String OFFLINE_TRY_NUMBER = "offline.try-number";

    @Value("${offline.try-time-connection}")
    private String tryTimeConnection;
    public static final String OFFLINE_TRY_TIME_CONNECTION = "offline.try-time-connection";

    @Value("${siat.soap-service}")
    private String siatSoapService;
    public static final String SIAT_SOAP_SERVICE = "siat.soap-service";

    @Value("${siat.soap-timeout-connect}")
    private String soapTimeoutConnect;
    public static final String BATCH_SIAT_SOAP_TIMEOUT_CONNECT = "siat.soap-timeout-connect";

    @Value("${siat.soap-timeout-request}")
    private String soapTimeoutRequest;
    public static final String BATCH_SIAT_SOAP_TIMEOUT_REQUEST = "siat.soap-timeout-request";

    @Value("${connectivity.check-method}")
    private String getConnectivityCheckMethod;
    public static final String CONNECTIVITY_CHECK_METHOD = "connectivity.check-method";

    @Value("${connectivity.host}")
    private String getConnectivityHost;
    public static final String CONNECTIVITY_HOST = "connectivity.host";

    @Value("${connectivity.port}")
    private String getConnectivityPort;
    public static final String CONNECTIVITY_PORT = "connectivity.port";

    @Value("${connectivity.timeout}")
    private String getConnectivityTimeout;
    public static final String CONNECTIVITY_TIMEOUT = "connectivity.timeout";

    @Value("${notification.online-offline-stage}")
    private String notificationOnlineOfflineStage;
    public static final String NOTIFICATION_ONLINE_OFFLINE_STAGE = "notification.online-offline-stage";

    @Value("${notification.online-offline-email}")
    private String notificationOnlineOfflineEmail;
    public static final String NOTIFICATION_ONLINE_OFFLINE_EMAIL = "notification.online-offline-email";

    @Value("${batch-revalidate-invoices.quantity}")
    private String getBatchRevalidateInvoicesQuantity;
    public static final String BATCH_REVALIDATE_INVOICES_QUANTITY = "batch-revalidate-invoices.quantity";

    @Value("${batch-revalidate-invoices.url-qr}")
    private String getBatchRevalidateInvoicesUrlQr;
    public static final String BATCH_REVALIDATE_INVOICES_URL_QR = "batch-revalidate-invoices.url-qr";

    @Value("${batch-revalidate-invoices.seconds-from-emitted}")
    private String getBatchRevalidateInvoicesSecondsFromEmitted;
    public static final String BATCH_REVALIDATE_INVOICES_SECONDS_FROM_EMITTED = "batch-revalidate-invoices.seconds-from-emitted";

    @Value("${batch-revalidate-invoices.quantity-failed}")
    private String getBatchRevalidateInvoicesQuantityFailed;
    public static final String BATCH_REVALIDATE_INVOICES_QUANTITY_FAILED = "batch-revalidate-invoices.quantity-failed";

    @Value("${sfl-integration.url-login}")
    private String getSflIntegrationUrlLogin;
    public static final String SFL_INTEGRATION_URL_LOGIN = "sfl-integration.url-login";

    @Value("${sfl-integration.user}")
    private String getSflIntegrationUser;
    public static final String SFL_INTEGRATION_USER = "sfl-integration.user";

    @Value("${sfl-integration.password}")
    private String getSflIntegrationPassword;
    public static final String SFL_INTEGRATION_PASSWORD = "sfl-integration.password";

    @Value("${sfl-integration.url-revert-force}")
    private String getSflIntegrationUrlRevertForce;
    public static final String SFL_INTEGRATION_URL_REVERT_FORCE = "sfl-integration.url-revert-force";

    @Value("${check-token-certificate.days-to-notify}")
    private String geCheckTokenCertificatedDaysToNotify;
    public static final String CHECK_TOKEN_CERTIFICATE_DAYS_TO_NOTIFY = "check-token-certificate.days-to-notify";


}
