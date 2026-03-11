package bo.com.luminia.sflbilling.msreport.siat.pdf.invoices;


import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoice;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoiceParameters;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfProxy;
import bo.gob.impuestos.sfe.invoice.xml.eltr.XmlEntidadesFinancierasEltrInvoice;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlEntidadesFinancierasHeader;
import com.google.zxing.WriterException;
import net.sf.jasperreports.engine.JRException;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class PdfEltrEntidadesFinancieras extends PdfInvoice<XmlEntidadesFinancierasEltrInvoice, XmlEntidadesFinancierasHeader> implements PdfProxy {

    public PdfEltrEntidadesFinancieras(XmlEntidadesFinancierasEltrInvoice invoice, String paperType, Image logo) {
        super(invoice, paperType, logo);
        this.fileName = String.format("/reports/facturaEntidadesFinancieras_%s.jrxml", paperType);
        this.productCode = invoice.getDetalle().get(0).getCodigoProducto();
    }

    public HashMap<String, Object> getDefaultParametersForHeader(XmlEntidadesFinancierasHeader xmlCompraVentaHeader)
        throws IOException, WriterException {
        HashMap<String, Object> parameters = getDefaultParameters(xmlCompraVentaHeader);

        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_ARRENDAMIENTO, this.invoice.getCabecera().getMontoTotalArrendamientoFinanciero().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_SUBTOTAL, (detailsToSubtotal( invoice.getDetalle() )));

        return parameters;
    }

    @Override
    public byte[] generate() throws IOException, WriterException, JRException, URISyntaxException {

        HashMap<String, Object> parameters = this.getDefaultParametersForHeader(this.invoice.getCabecera());

        byte[] bytes = generateReportPdf(this.invoice.getDetalle(), parameters);

        return bytes;
    }

}
