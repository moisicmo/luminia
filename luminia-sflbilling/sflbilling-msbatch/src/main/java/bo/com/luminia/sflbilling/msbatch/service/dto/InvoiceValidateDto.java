package bo.com.luminia.sflbilling.msbatch.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InvoiceValidateDto {
    private Integer statusCode;
    private String receptionCode;
    private List<InvoiceValidateMessageDto> validationMessages;

    public InvoiceValidateDto() {
        this.validationMessages = new ArrayList<>();
    }
}
