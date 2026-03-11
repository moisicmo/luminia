package bo.com.luminia.sflbilling.msbatch.service.constants;

public class SiatResponseCodes {
    public static final Integer SUCCESS = 200;

    public static final Integer ERROR_CUIS_NOT_FOUND = 301;
    public static final Integer ERROR_CUFD_NOT_FOUND = 302;
    public static final Integer ERROR_APPROVED_PRODUCT_NOT_FOUND = 303;
    public static final Integer ERROR_POINT_SALES_NOT_FOUND = 304;
    public static final Integer ERROR_EVENT_NOT_FOUND = 305;
    public static final Integer ERROR_BATCH_NOT_FOUND = 306;

    public static final Integer ERROR_MESSAGE_FROM_SIAT = 403;
    public static final Integer ERROR_BATCH_VALIDATE = 404;

    public static final Integer SIAT_HAS_CONNECTIVITY = 926;
    public static final Integer SIAT_WRAPPER_VALIDATED = 908;
    public static final Integer SIAT_WRAPPER_PENDING = 901;
    public static final Integer SIAT_WRAPPER_OBSERVED = 904;

    public static final Integer ERROR_OFFLINE_NOT_FOUND = 801;
    public static final Integer ERROR_OFFLINE_ACTIVATE = 802;
}
