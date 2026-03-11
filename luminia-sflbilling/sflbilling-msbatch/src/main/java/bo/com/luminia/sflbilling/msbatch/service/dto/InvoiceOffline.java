package bo.com.luminia.sflbilling.msbatch.service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Setter
@Getter
public class InvoiceOffline {

        @NotNull
        private Integer significantEventSiatId;
        @NotNull
        private String description;

}
