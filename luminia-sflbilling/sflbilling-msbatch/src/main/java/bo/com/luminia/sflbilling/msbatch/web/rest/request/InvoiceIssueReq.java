package bo.com.luminia.sflbilling.msbatch.web.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Setter
@Getter
public class InvoiceIssueReq {
    @JsonIgnore
    private Long companyId;
    @NotNull
    private String businessCode;
    @NotNull
    private Integer branchOfficeSiat;
    @NotNull
    private Integer pointSaleSiat;
    @NotNull
    private Integer documentSectorType;
    private String email;
    private Boolean useCurrencyType;
    private String barcode;
    @NotNull
    private HashMap<String, Object> header;

    private List<Map<String, String>> detail;
    private List<Map<String, String>> detailOriginal;
    private List<Map<String, String>> detailConciliation;
}
