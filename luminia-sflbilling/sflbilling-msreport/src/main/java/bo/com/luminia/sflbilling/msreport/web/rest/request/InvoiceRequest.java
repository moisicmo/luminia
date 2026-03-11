package bo.com.luminia.sflbilling.msreport.web.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InvoiceRequest {
    @JsonIgnore
    private Long companyId;
    @NotNull
    private String businessCode;
    private Integer branchOffice;
    private Integer pointSale;
    private String startDate;
    private String endDate;
    private String status;
    private String nit;
    private String businessName;

}
