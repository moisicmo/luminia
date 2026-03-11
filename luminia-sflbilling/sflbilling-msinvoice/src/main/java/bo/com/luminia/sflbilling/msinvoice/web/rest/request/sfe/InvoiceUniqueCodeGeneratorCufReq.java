package bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class InvoiceUniqueCodeGeneratorCufReq {
    @NotBlank
    private Long nit;
    @NotBlank
    private Date datetime;
    @NotBlank
    private Integer branchOffice;
    @NotBlank
    private Integer modality;
    @NotBlank
    private Integer emisionType;
    @NotBlank
    private Integer invoiceType;
    @NotBlank
    private Integer documentSectorType;
    @NotBlank
    private Integer invoiceNumber;
    @NotBlank
    private Integer pointOfSale;
    @NotBlank
    private String controlCode;
}
