package bo.com.luminia.sflbilling.msbatch.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceValidateMessageDto {
    private Integer fileNumber;
    private String description;

    public InvoiceValidateMessageDto(Integer fileNumber, String description) {
        this.fileNumber = fileNumber;
        this.description = description;
    }
}
