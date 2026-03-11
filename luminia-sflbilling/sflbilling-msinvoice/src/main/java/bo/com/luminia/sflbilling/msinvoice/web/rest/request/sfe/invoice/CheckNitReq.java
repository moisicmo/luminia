package bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
public class CheckNitReq {
    @JsonIgnore
    private Long companyId;
    @NotNull
    private String businessCode;
    @NotNull
    private Integer branchOfficeSiat;
    @NotNull
    private Integer pointSaleSiat;
    @NotNull
    private Long nit;
}
