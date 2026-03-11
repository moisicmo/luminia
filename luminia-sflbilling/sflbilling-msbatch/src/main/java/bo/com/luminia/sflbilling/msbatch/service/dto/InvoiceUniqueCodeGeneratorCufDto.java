package bo.com.luminia.sflbilling.msbatch.service.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class InvoiceUniqueCodeGeneratorCufDto implements Cloneable {
    @NotBlank
    private String nit;
    @NotBlank
    private String datetime;
    @NotBlank
    private String branchOffice;
    @NotBlank
    private String modality;
    @NotBlank
    private String emisionType;
    @NotBlank
    private String invoiceType;
    @NotBlank
    private String documentSectorType;
    @NotBlank
    private String invoiceNumber;
    @NotBlank
    private String pointOfSale;
    @NotBlank
    private String autoCheckerCode;

    public Object clone() {
        InvoiceUniqueCodeGeneratorCufDto result = new InvoiceUniqueCodeGeneratorCufDto();

        result.setInvoiceType(this.getInvoiceType());
        result.setInvoiceNumber(this.getInvoiceNumber());
        result.setPointOfSale(this.getPointOfSale());
        result.setNit(this.getNit());
        result.setModality(this.getModality());
        result.setEmisionType(this.getEmisionType());
        result.setDatetime(this.getDatetime());
        result.setBranchOffice(this.getBranchOffice());
        result.setDocumentSectorType(this.getDocumentSectorType());
        result.setAutoCheckerCode(this.getAutoCheckerCode());

        return result;
    }

}
