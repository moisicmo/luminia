package bo.com.luminia.sflbilling.msreport.web.rest.response;

import bo.com.luminia.sflbilling.msreport.web.rest.response.data.SearchInvoiceData;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchInvoiceResponse implements Serializable {
    private List<SearchInvoiceData> invoices;
    private Integer total;
}
