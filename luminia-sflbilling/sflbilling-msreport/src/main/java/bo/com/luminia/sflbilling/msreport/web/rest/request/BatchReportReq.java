package bo.com.luminia.sflbilling.msreport.web.rest.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
public class BatchReportReq {
    @NotNull
    private String businessCode;
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;
}
