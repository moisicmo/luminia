package bo.com.luminia.sflbilling.msbatch.web.rest.certification.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
public class InvoiceOfflineReq {
    @NotNull
    private Integer significantEventSiatId;
    @NotNull
    private String description;

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
