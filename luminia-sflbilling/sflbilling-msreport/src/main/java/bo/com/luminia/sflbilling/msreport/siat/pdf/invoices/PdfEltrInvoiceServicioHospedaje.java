package bo.com.luminia.sflbilling.msreport.siat.pdf.invoices;

import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoice;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoiceParameters;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfProxy;
import bo.gob.impuestos.sfe.invoice.xml.eltr.XmlTuristicoHospedajeEltrInvoice;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlTuristicoHospedajeHeader;
import com.google.zxing.WriterException;
import net.sf.jasperreports.engine.JRException;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class PdfEltrInvoiceServicioHospedaje extends PdfInvoice<XmlTuristicoHospedajeEltrInvoice
    , XmlTuristicoHospedajeHeader> implements PdfProxy {

    public PdfEltrInvoiceServicioHospedaje(XmlTuristicoHospedajeEltrInvoice invoice, String paperType, Image logo) {
        super(invoice, paperType, logo);
        this.fileName = String.format("/reports/facturaServicioTuristicoHospedaje_%s.jrxml", paperType);
        this.productCode = invoice.getDetalle().get(0).getCodigoProducto();
    }

    public HashMap<String, Object> getDefaultParametersForHeader(XmlTuristicoHospedajeHeader header)
        throws IOException, WriterException {
        HashMap<String, Object> parameters = getDefaultParameters(header);

        parameters.put(PdfInvoiceParameters.INVOICE_CANTIDAD_HABITACIONES, header.getCantidadHabitaciones().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_CANTIDAD_MAYORES, header.getCantidadMayores().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_CANTIDAD_MENORES, header.getCantidadMenores().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_FECHA_INGRESO_HOSPEDAJE, header.getFechaIngresoHospedaje().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_RAZON_SOCIAL_OPERADOR_TURISMO, header.getRazonSocialOperadorTurismo());
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
