package bo.com.luminia.sflbilling.msbatch.web.rest.certification.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class InvoiceIssueReq {
    @NotNull
    private Long companyId;
    @NotNull
    private Integer branchOfficeSiat;
    @NotNull
    private Integer pointSaleSiat;
    @NotNull
    private Integer documentSectorType;
    @NotNull
    private HashMap<String, String> header;
    @NotNull
    private List<Map<String, String>> detail;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Integer getBranchOfficeSiat() {
        return branchOfficeSiat;
    }

    public void setBranchOfficeSiat(Integer branchOfficeSiat) {
        this.branchOfficeSiat = branchOfficeSiat;
    }

    public Integer getPointSaleSiat() {
        return pointSaleSiat;
    }

    public void setPointSaleSiat(Integer pointSaleSiat) {
        this.pointSaleSiat = pointSaleSiat;
    }

    public Integer getDocumentSectorType() {
        return documentSectorType;
    }

    public void setDocumentSectorType(Integer documentSectorType) {
        this.documentSectorType = documentSectorType;
    }

    public HashMap<String, String> getHeader() {
        return header;
    }

    public void setHeader(HashMap<String, String> header) {
        this.header = header;
    }

    public List<Map<String, String>> getDetail() {
        return detail;
    }

    public void setDetail(List<Map<String, String>> detail) {
        this.detail = detail;
    }
}
