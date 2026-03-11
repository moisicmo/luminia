package bo.com.luminia.sflbilling.msbatch.service.dto;

import bo.com.luminia.sflbilling.domain.response.BaseResponseSiat;

public class ResponseSync extends BaseResponseSiat {
    public ResponseSync(int code, String message, Object body) {
        this.setCode(code);
        this.setMessage(message);
        this.setBody(body);
    }
}
