package bo.com.luminia.sflbilling.msbatch.web.rest.certification.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
public class InvoiceOfflineManualReq {
    @NotNull
    private Long companyId ;
    @NotNull
    private String cafc;
    @NotNull
    private Integer significantEventSiatId;
    @NotNull
    private String description;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCafc() {
        return cafc;
    }

    public void setCafc(String cafc) {
        this.cafc = cafc;
    }

    public Integer getSignificantEventSiatId() {
        return significantEventSiatId;
    }

    public void setSignificantEventSiatId(Integer significantEventSiatId) {
        this.significantEventSiatId = significantEventSiatId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
