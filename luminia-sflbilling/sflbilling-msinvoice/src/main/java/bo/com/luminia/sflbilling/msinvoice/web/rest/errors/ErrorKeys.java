package bo.com.luminia.sflbilling.msinvoice.web.rest.errors;

import java.net.URI;

public final class ErrorKeys {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_SIAT_EXCEPTION = "SIAT_EXCEPTION";
    public static final String ERR_VALIDATIONS = "VALIDATIONS";
    public static final String ERR_XML_EXCEPTION = "XML_EXCEPTION";
    public static final String ERR_EVENT_NOT_FOUND = "EVENT_NOT_FOUND";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String ERR_ON_SAVE = "ERROR_ON_SAVE";
    public static final String ERR_RECORD_NOT_FOUND = "ERROR_RECORD_NOT_FOUND";
    public static final String ERR_MUST_SPECIFY_ID = "MUST_SPECIFY_ID";
    public static final String ERR_RECORD_ALREADY_EXISTS = "ERROR_RECORD_ALREADY_EXISTS";
    public static final String ERR_RECORD_NOT_SAVED = "ERROR_RECORD_NOT_SAVED";
    public static final String ERR_REQUIRED_VALUE = "ERROR_REQUIRED_VALUE";
    public static final String ERR_SIAT_NO_CONNECTION = "SIAT_NO_CONNECTION";
    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");

    private ErrorKeys() {
    }
}
