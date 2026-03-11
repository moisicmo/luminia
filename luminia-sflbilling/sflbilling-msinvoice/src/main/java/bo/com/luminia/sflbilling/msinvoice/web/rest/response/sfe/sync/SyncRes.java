package bo.com.luminia.sflbilling.msinvoice.web.rest.response.sfe.sync;

import lombok.Data;

@Data
public class SyncRes {

    private Boolean response;
    private String message;

    public SyncRes(Boolean response, String message) {
        this.response = response;
        this.message = message;
    }
}
