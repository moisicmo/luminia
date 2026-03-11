package bo.com.luminia.sflbilling.msreport.web.rest.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ResumeInvoiceRequest {
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;

    private Long companyId;
    private String businessCode;
}
