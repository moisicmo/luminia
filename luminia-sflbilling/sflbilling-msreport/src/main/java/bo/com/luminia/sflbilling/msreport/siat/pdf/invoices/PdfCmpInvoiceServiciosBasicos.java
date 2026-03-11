package bo.com.luminia.sflbilling.msreport.siat.pdf.invoices;


import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoice;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoiceParameters;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfProxy;
import bo.gob.impuestos.sfe.invoice.xml.cmp.XmlServiciosBasicosCmpInvoice;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlServiciosBasicosHeader;
import com.google.zxing.WriterException;
import net.sf.jasperreports.engine.JRException;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class PdfCmpInvoiceServiciosBasicos extends PdfInvoice<XmlServiciosBasicosCmpInvoice, XmlServiciosBasicosHeader> implements PdfProxy {

    public PdfCmpInvoiceServiciosBasicos(XmlServiciosBasicosCmpInvoice invoice, String paperType, Image logo) {
        super(invoice, paperType, logo);
        this.fileName = String.format("/reports/facturaServiciosBasicos_%s.jrxml", paperType);
        this.productCode = invoice.getDetalle().get(0).getCodigoProducto();
    }

    public HashMap<String, Object> getDefaultParametersForHeader(XmlServiciosBasicosHeader header)
        throws IOException, WriterException {
        HashMap<String, Object> parameters = getDefaultParameters(header);
        parameters.put(PdfInvoiceParameters.INVOICE_CODIGO_CLIENTE, header.getCodigoCliente());
        parameters.put(PdfInvoiceParameters.INVOICE_CONSUMO_PERIODO, header.getConsumoPeriodo());
        parameters.put(PdfInvoiceParameters.INVOICE_DOMICILIO_CLIENTE, header.getDomicilioCliente());
        parameters.put(PdfInvoiceParameters.INVOICE_TASA_ALUMBRADO, header.getTasaAlumbrado() == null ? "0.00" : header.getTasaAlumbrado().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_OTROS_PAGOS, header.getOtrosPagosNoSujetoIva() == null ? "0.00" : header.getOtrosPagosNoSujetoIva().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_AJUSTE_NO_SUJETO_IVA, header.getAjusteNoSujetoIva() == null ? "0.00" : header.getAjusteNoSujetoIva().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_TASA_ASEO, header.getTasaAseo() == null ? "0.00" : header.getTasaAseo().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_OTRAS_TASAS, header.getOtrasTasas() == null ? "0.00" : header.getOtrasTasas().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_DESCUENTO_TARIFA_DIGNIDAD, header.getMontoDescuentoTarifaDignidad() == null ? "0.00" : header.getMontoDescuentoTarifaDignidad().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_DESCUENTO_LEY_1886, header.getMontoDescuentoLey1886() == null ? "0.00" : header.getMontoDescuentoLey1886().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_NUMERO_MEDIDOR, header.getNumeroMedidor() == null ? "0.00" : header.getNumeroMedidor());
        parameters.put(PdfInvoiceParameters.INVOICE_CONSUMO_GESTION, header.getGestion().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_CONSUMO_MES, header.getMes());
        parameters.put(PdfInvoiceParameters.INVOICE_BENEFICIARIO_LEY_1886, header.getBeneficiarioLey1886() == null ? "0.00" : header.getBeneficiarioLey1886());
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
