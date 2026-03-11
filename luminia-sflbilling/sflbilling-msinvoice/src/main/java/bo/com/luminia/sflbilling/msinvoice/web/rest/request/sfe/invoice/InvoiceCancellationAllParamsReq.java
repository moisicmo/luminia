package bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Getter
@Setter
public class InvoiceCancellationAllParamsReq {
    @NotNull
    private String cuf;

    @NotNull
    private Integer pointSaleSiat;

    @NotNull
    private Integer branchOfficeSiat;

    @NotNull
    private Integer sectorDocumentTypeSiat;

    @NotNull
    private Long companyId;

    private String email;

    @NotNull
    private Integer cancellationReasonSiat;

}
