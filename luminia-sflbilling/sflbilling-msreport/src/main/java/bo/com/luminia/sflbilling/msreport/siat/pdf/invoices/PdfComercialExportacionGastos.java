package bo.com.luminia.sflbilling.msreport.siat.pdf.invoices;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PdfComercialExportacionGastos {

    private String descripcion ;
    private BigDecimal subTotal ;

}
