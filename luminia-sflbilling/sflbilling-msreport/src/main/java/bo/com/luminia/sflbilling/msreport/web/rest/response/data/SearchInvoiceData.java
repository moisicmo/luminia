package bo.com.luminia.sflbilling.msreport.web.rest.response.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchInvoiceData {
    private Long id;
    private Integer invoiceNumber;
    private String cuf;
    private String broadcastDate;
    private String status;
    private String modalitySiat;
    private Integer sectorDocumentTypeId;
    private String sectorDocumentTypeDescription;
    private Integer broadcastType;
    private String broadcastTypeDescription;
    private Integer invoiceType;
    private String invoiceTypeDescription;
    private Double amount;

    private Double amountIva;
    private String socialReason;
    private String document;
    private String complement;

}
