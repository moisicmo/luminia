package bo.com.luminia.sflbilling.msreport.web.rest.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SearchInvoiceRequest {
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;

    private Long companyId;
    private String businessCode;

    private Long invoiceId;
    private Integer invoiceNumber;
    private String document;
    private String status;


    @NotNull
    private Integer offset;
    @NotNull
    private Integer limit;
}
