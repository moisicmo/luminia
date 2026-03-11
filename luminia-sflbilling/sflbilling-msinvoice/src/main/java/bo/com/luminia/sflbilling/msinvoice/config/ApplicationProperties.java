package bo.com.luminia.sflbilling.msinvoice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    @Value("${notification.endpoint}")
    private String notificationEndpoint;
    public static final String NOTIFICATION_ENDPOINT = "notification.endpoint";

    @Value("${notification.endpoint-email}")
    private String notificationEndpointEmail;
    public static final String NOTIFICATION_ENDPOINT_EMAIL = "notification.endpoint-email";

    @Value("${notification.client-id}")
    private String notificationClientId;
    public static final String NOTIFICATION_CLIENT_ID = "notification.client-id";

    @Value("${notification.api-key}")
    private String notificationApiKey;
    public static final String NOTIFICATION_API_KEY = "notification.api-key";

    @Value("${cancellation.term-days}")
    private String cancellationTermDays;
    public static final String CANCELLATION_TERM_DAYS = "cancellation.term-days";

    @Value("${cancellation.term-days-comercial-exportacion")
    private String cancellationTermDaysComercialExportacion;
    public static final String CANCELLATION_TERM_DAYS_COMERICIAL_EXPORTACION = "cancellation.term-days-comercial-exportacion";

    @Value("${invoice.number-limit-batch}")
    private String invoiceNumberLimitBatch;
    public static final String INVOICE_NUMBER_LIMIT_BATCH = "invoice.number-limit-batch";

    @Value("${siat.soap-service}")
    private String siatSoapService;
    public static final String SIAT_SOAP_SERVICE = "siat.soap-service";

    @Value("${siat.skip-check-events}")
    private String siatSkipCheckEvents;
    public static final String SIAT_SKIP_CHECK_EVENTS = "siat.skip-check-events";

    @Value("${siat.soap-enable-handler-invoice}")
    private String siatSoapEnableHandlerInvoice;
    public static final String SIAT_SOAP_ENABLE_HANDLER_INVOICE = "siat.soap-enable-handler-invoice";

    @Value("${invoice.print-emit-cron-detail}")
    private String invoicePrintEmitCronDetail;
    public static final String INVOICE_PRINT_EMIT_CRON_DETAIL = "invoice.print-emit-cron-detail";


    @Value("${siat.use-saved-identity-document-types}")
    private String getSiatUseSavedIdentityDocumentTypes;
    public static final String SIAT_USE_SAVED_IDENTITY_DOCUMENT_TYPES = "siat.use-saved-identity-document-types";


    //MODIFICACION - ABEL
    @Value("${invoice.time-send-siat}")
    private String invoiceTimeSendSiat;
    public static final String INVOICE_TIME_SEND_SIAT = "invoice.time-send-siat";


}
