package bo.com.luminia.sflbilling.msreport.web.rest.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InvoiceEmailReq {
    @NotNull
    private String cuf;
    @NotNull
    private String email;
}
