package bo.com.luminia.sflbilling.msreport.siat.pdf.invoices;


import bo.com.luminia.sflbilling.domain.MeasurementUnit;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoice;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoiceParameters;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfProxy;
import bo.gob.impuestos.sfe.invoice.xml.XmlDateUtil;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlBaseDetailInvoice;
import bo.gob.impuestos.sfe.invoice.xml.eltr.XmlNotaCreditoDebitoEltrInvoice;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlNotaCreditoDebitoHeader;
import com.google.zxing.WriterException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class PdfEltrInvoiceNotaCreditoDebito extends PdfInvoice<XmlNotaCreditoDebitoEltrInvoice, XmlNotaCreditoDebitoHeader> implements PdfProxy {

    public PdfEltrInvoiceNotaCreditoDebito(XmlNotaCreditoDebitoEltrInvoice invoice, String paperType, Image logo) {
        super(invoice, paperType, logo);
        this.fileName = String.format("/reports/facturaNotaCreditoDebito_%s.jrxml", paperType);
        this.productCode = invoice.getDetalle().get(0).getCodigoProducto();
    }

    public HashMap<String, Object> getDefaultParametersForHeader(XmlNotaCreditoDebitoHeader header)
        throws IOException, WriterException {
        HashMap<String, Object> parameters = getDefaultParameters(header);
        return parameters;
    }

    @Override
    public byte[] generate() throws IOException, WriterException, JRException, URISyntaxException {

        HashMap<String, Object> parameters = this.getDefaultParametersForHeader(this.invoice.getCabecera());

        parameters.put("INVOICE_MONTO_TOTAL", this.invoice.getCabecera().getMontoTotalDevuelto());
        parameters.put(PdfInvoiceParameters.INVOICE_TOTAL_LITERAL, integerToLiteral(this.invoice.getCabecera().getMontoTotalDevuelto().intValue()));
        parameters.put(PdfInvoiceParameters.INVOICE_TOTAL_DECIMAL, this.obtainDecimalPart(this.invoice.getCabecera().getMontoTotalDevuelto()));

        parameters.put("MONTO_TOTAL_ORIGINAL", this.invoice.getCabecera().getMontoTotalOriginal());
        parameters.put("MONTO_EFECTIVO_CREDITO_DEBITO", this.invoice.getCabecera().getMontoEfectivoCreditoDebito());
        parameters.put("MONTO_TOTAL_DEVUELTO", this.invoice.getCabecera().getMontoTotalDevuelto());
        parameters.put("INVOICE_AUTORIZACION_CUF", this.invoice.getCabecera().getNumeroAutorizacionCuf());
        parameters.put("INVOICE_NUMERO_FACTURA", this.invoice.getCabecera().getNumeroFactura().toString());
        parameters.put("INVOICE_FECHA_FACTURA", XmlDateUtil.convertDateToYyyymmddhhmmssSSS(this.invoice.getCabecera().getFechaEmisionFactura()));
        parameters.put("INVOICE_NUMERO_NOTA_CREDITO_DEBITO", this.invoice.getCabecera().getNumeroNotaCreditoDebito().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_SUBTOTAL, (detailsToSubtotal( invoice.getDetalle() )));

        byte[] bytes = generateReportPdf(this.invoice.getDetalle(), parameters);

        return bytes;
    }

    protected byte[] generateReportPdf(List<?> detail, HashMap<String, Object> parameters) throws FileNotFoundException, JRException, URISyntaxException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

        InputStream stream = PdfEltrInvoiceNotaCreditoDebito.class.getResourceAsStream(this.fileName);

        //File f = ResourceUtils.getFile(this.fileName);
        //JasperReport jasperReport = JasperCompileManager.compileReport(f.getAbsolutePath());
        JasperReport jasperReport = JasperCompileManager.compileReport(stream);

        InputStream st1 = PdfEltrInvoiceNotaCreditoDebito.class.getResourceAsStream(String.format("/reports/datos_original_%s.jrxml", paperType));
        InputStream st2 = PdfEltrInvoiceNotaCreditoDebito.class.getResourceAsStream(String.format("/reports/devolucion_%s.jrxml", paperType));
        //File f1 = ResourceUtils.getFile((String.format("classpath:reports/datos_original_%s.jasper", paperType)));
        //File f2 = ResourceUtils.getFile((String.format("classpath:reports/devolucion_%s.jasper", paperType)));

        JasperReport subreport1 = JasperCompileManager.compileReport(st1);
        JasperReport subreport2 = JasperCompileManager.compileReport(st2);

        List<XmlBaseDetailInvoice> detailList = (List<XmlBaseDetailInvoice>) detail;
        for (XmlBaseDetailInvoice obj: detailList  ) {
            Optional<MeasurementUnit> unit =  this.measurementUnitRepository.findAllByCompanyIdAndSiatIdAndActiveIsTrue(companyId.intValue(), obj.getUnidadMedida());
            if(unit.isPresent()){
                obj.setUnidadMedidaLiteral(unit.get().getDescription());
            }else {
                obj.setUnidadMedidaLiteral("");
            }
        }

        parameters.put("SUBREPORT_1", new ArrayList<>(detail));
        parameters.put("SUBREPORT_2", new ArrayList<>(detail));

        //parameters.put("SUBREPORT_FILE_1",f1.getAbsolutePath() );
        parameters.put("SUBREPORT_FILE_1", subreport1);
        //parameters.put("SUBREPORT_FILE_2", f2.getAbsolutePath());
        parameters.put("SUBREPORT_FILE_2", subreport2);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(detail.subList(0, 1));

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);

        return bytes;
    }


}
