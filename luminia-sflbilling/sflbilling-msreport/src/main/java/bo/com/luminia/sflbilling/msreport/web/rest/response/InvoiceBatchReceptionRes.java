package bo.com.luminia.sflbilling.msreport.web.rest.response;

import lombok.Data;

@Data
public class InvoiceBatchReceptionRes {
    private Long invoiceNumber;
    private String cuf;
    private String status;
    private String validationSource;
    private String responseMessage;
}
