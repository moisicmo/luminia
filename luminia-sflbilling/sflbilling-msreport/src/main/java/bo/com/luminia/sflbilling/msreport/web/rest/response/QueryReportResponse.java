package bo.com.luminia.sflbilling.msreport.web.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryReportResponse {
    private List<InvoiceResponse> invoices;
    private Integer total;

    public List<InvoiceResponse> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<InvoiceResponse> invoices) {
        this.invoices = invoices;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
