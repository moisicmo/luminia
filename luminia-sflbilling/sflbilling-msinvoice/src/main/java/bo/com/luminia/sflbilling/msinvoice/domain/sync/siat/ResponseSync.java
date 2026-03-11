package bo.com.luminia.sflbilling.msinvoice.domain.sync.siat;

import bo.com.luminia.sflbilling.domain.response.BaseResponseSiat;

public class ResponseSync extends BaseResponseSiat {
    public ResponseSync(int code, String message, Object body) {
        this.setCode(code);
        this.setMessage(message);
        this.setBody(body);
    }
}
