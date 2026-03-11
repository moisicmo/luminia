package bo.com.luminia.sflbilling.domain.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class BaseResponseSiat<T> {

    protected Integer code;
    protected String message;
    protected T body;

    public BaseResponseSiat() {
    }

    public BaseResponseSiat(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
