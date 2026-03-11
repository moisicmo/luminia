package bo.com.luminia.sflbilling.msreport.web.rest.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
public class WrapperReportReq {
    @NotNull
    private Long companyId;
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;
}
