package bo.com.luminia.sflbilling.msbatch.web.rest.response;

import bo.com.luminia.sflbilling.domain.response.BaseResponseSiat;

public class OfflineValidatorRes extends BaseResponseSiat {

    public OfflineValidatorRes(Integer code, String message) {
        super(code, message);
    }

    public OfflineValidatorRes() {
    }
}
