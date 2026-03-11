package bo.com.luminia.sflbilling.msreport.web.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryRequest {
    @JsonIgnore
    private Long companyId;
    @NotNull
    private String businessCode;
    private Integer branchOffice;
    private Integer pointSale;
    private String startDate;
    private String endDate;
    private String status;
    private Integer offset;
    private Integer limit;
    private Integer invoiceNumber;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public Integer getBranchOffice() {
        return branchOffice;
    }

    public void setBranchOffice(Integer branchOffice) {
        this.branchOffice = branchOffice;
    }

    public Integer getPointSale() {
        return pointSale;
    }

    public void setPointSale(Integer pointSale) {
        this.pointSale = pointSale;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Integer invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
