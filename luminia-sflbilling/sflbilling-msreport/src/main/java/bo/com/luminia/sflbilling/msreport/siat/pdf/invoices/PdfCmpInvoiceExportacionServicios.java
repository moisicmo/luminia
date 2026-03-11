package bo.com.luminia.sflbilling.msreport.siat.pdf.invoices;


import bo.com.luminia.sflbilling.domain.CurrencyType;
import bo.com.luminia.sflbilling.msreport.repository.CurrencyTypeRepository;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoice;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoiceParameters;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfProxy;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.spec.PdfInvoiceCurrencyNotFoundException;
import bo.gob.impuestos.sfe.invoice.xml.cmp.XmlExportacionServicioCmpInvoice;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlExportacionServiciosHeader;
import com.google.zxing.WriterException;
import net.sf.jasperreports.engine.JRException;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Optional;

public class PdfCmpInvoiceExportacionServicios extends PdfInvoice<XmlExportacionServicioCmpInvoice, XmlExportacionServiciosHeader> implements PdfProxy {

    private CurrencyTypeRepository repository;

    public PdfCmpInvoiceExportacionServicios(XmlExportacionServicioCmpInvoice invoice, String paperType,
                                             CurrencyTypeRepository currencyTypeRepository, Image logo) {
        super(invoice, paperType, logo);
        this.fileName = String.format("/reports/facturaExportacionServicios_%s.jrxml", paperType);
        this.repository = currencyTypeRepository;
        this.productCode = invoice.getDetalle().get(0).getCodigoProducto();
    }

    public HashMap<String, Object> getDefaultParametersForHeader(XmlExportacionServiciosHeader header)
        throws IOException, WriterException {

        Optional<CurrencyType> currencyType = repository.findCurrencyTypeBySiatIdAndActiveIsTrue(header.getCodigoMoneda());

        if (!currencyType.isPresent()) {
            throw new PdfInvoiceCurrencyNotFoundException(String.format("Codigo de Moneda no encontrado: %d", header.getCodigoMoneda()));
        }

        HashMap<String, Object> parameters = getDefaultParameters(header);
        parameters.put(PdfInvoiceParameters.INVOICE_DIRECCION_COMPRADOR, header.getDireccionComprador());
        parameters.put(PdfInvoiceParameters.INVOICE_TIPO_CAMBIO, header.getTipoCambio().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_LUGAR_DESTINO, header.getLugarDestino());
        parameters.put(PdfInvoiceParameters.INVOICE_INFORMACION_ADICIONAL, header.getInformacionAdicional());
        parameters.put(PdfInvoiceParameters.INVOICE_MONEDA_TIPO_CAMBIO_LITERAL, currencyType.get().getDescription());
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_TOTAL_TIPO_CAMBIO_LITERAL, integerToLiteral(header.getMontoTotalMoneda().intValue()));
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_TOTAL_TIPO_CAMBIO, header.getMontoTotalMoneda().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_TOTAL_TIPO_CAMBIO_DECIMAL, obtainDecimalPart(header.getMontoTotalMoneda()));
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_SUBTOTAL, (detailsToSubtotal( invoice.getDetalle() )));

        return parameters;
    }

    @Override
    public byte[] generate() throws IOException, WriterException, JRException, PdfInvoiceCurrencyNotFoundException, URISyntaxException {

        HashMap<String, Object> parameters = this.getDefaultParametersForHeader(this.invoice.getCabecera());

        byte[] bytes = generateReportPdf(this.invoice.getDetalle(), parameters);

        return bytes;
    }


}
