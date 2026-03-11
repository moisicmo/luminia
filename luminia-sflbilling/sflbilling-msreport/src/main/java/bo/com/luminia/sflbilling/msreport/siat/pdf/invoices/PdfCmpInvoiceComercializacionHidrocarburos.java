package bo.com.luminia.sflbilling.msreport.siat.pdf.invoices;


import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoice;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoiceParameters;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfProxy;
import bo.gob.impuestos.sfe.invoice.xml.cmp.XmlHidrocarburosCmpInvoice;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlHidrocarburosHeader;
import com.google.zxing.WriterException;
import net.sf.jasperreports.engine.JRException;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class PdfCmpInvoiceComercializacionHidrocarburos extends PdfInvoice<XmlHidrocarburosCmpInvoice, XmlHidrocarburosHeader> implements PdfProxy {

    public PdfCmpInvoiceComercializacionHidrocarburos(XmlHidrocarburosCmpInvoice invoice, String paperType, Image logo) {
        super(invoice, paperType, logo);
        this.fileName = String.format("/reports/facturaComercializacionHidrocarburos_%s.jrxml", paperType);
        this.productCode = invoice.getDetalle().get(0).getCodigoProducto();
    }

    public HashMap<String, Object> getDefaultParametersForHeader(XmlHidrocarburosHeader header)
        throws IOException, WriterException {
        HashMap<String, Object> parameters = getDefaultParameters(header);
        parameters.put(PdfInvoiceParameters.INVOICE_PLACA_VEHICULO, header.getPlacaVehiculo());
        parameters.put(PdfInvoiceParameters.INVOICE_TIPO_ENVASE, header.getTipoEnvase());
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_LEY_317, header.getMontoTotalSujetoIvaLey317().toString());
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
