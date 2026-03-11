package bo.com.luminia.sflbilling.msbatch.web.rest.response;

import lombok.Data;

@Data
public class CrudRes {

    private Boolean response;
    private String message;
    private Object body;

    public CrudRes(Boolean response, String message, Object body) {
        this.response = response;
        this.message = message;
        this.body = body;
    }

    public CrudRes() {
    }
}
