package bo.com.luminia.sflbilling.msbatch.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceMassiveDto {
    private Long id;
    private String invoiceXml;
}
