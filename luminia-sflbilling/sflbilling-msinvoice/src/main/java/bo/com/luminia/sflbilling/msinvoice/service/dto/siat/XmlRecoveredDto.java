package bo.com.luminia.sflbilling.msinvoice.service.dto.siat;


import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XmlRecoveredDto {

    private XmlBaseInvoice xml;

    private String businessCode;
    private Integer documentSectorType;
    private String barcode;

}
