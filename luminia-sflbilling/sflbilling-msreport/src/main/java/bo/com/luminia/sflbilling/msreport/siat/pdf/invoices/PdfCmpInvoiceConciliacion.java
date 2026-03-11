package bo.com.luminia.sflbilling.msreport.siat.pdf.invoices;


import bo.com.luminia.sflbilling.domain.MeasurementUnit;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoice;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoiceParameters;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfProxy;
import bo.gob.impuestos.sfe.invoice.xml.XmlDateUtil;
import bo.gob.impuestos.sfe.invoice.xml.cmp.XmlConciliacionCmpInvoice;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlBaseDetailInvoice;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlConciliacionDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlConciliacionHeader;
import com.google.zxing.WriterException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class PdfCmpInvoiceConciliacion extends PdfInvoice<XmlConciliacionCmpInvoice, XmlConciliacionHeader> implements PdfProxy {

    public PdfCmpInvoiceConciliacion(XmlConciliacionCmpInvoice invoice, String paperType, Image logo) {
        super(invoice, paperType, logo);
        this.fileName = String.format("/reports/facturaConciliacion_%s.jrxml", paperType);
        this.productCode = invoice.getDetalle().get(0).getCodigoProducto();
    }

    public HashMap<String, Object> getDefaultParametersForHeader(XmlConciliacionHeader header)
        throws IOException, WriterException {
        HashMap<String, Object> parameters = getDefaultParameters(header);
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_SUBTOTAL, (detailsToSubtotal( invoice.getDetalle() )));

        return parameters;
    }

    @Override
    public byte[] generate() throws IOException, WriterException, JRException, URISyntaxException {

        HashMap<String, Object> parameters = this.getDefaultParametersForHeader(this.invoice.getCabecera());


        parameters.put("INVOICE_MONTO_TOTAL", this.invoice.getCabecera().getMontoTotal().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_TOTAL_LITERAL, integerToLiteral(this.invoice.getCabecera().getMontoTotalConciliado().intValue()));
        parameters.put(PdfInvoiceParameters.INVOICE_TOTAL_DECIMAL, this.obtainDecimalPart(this.invoice.getCabecera().getMontoTotalConciliado()));

        parameters.put("MONTO_TOTAL_ORIGINAL", this.invoice.getCabecera().getMontoTotalOriginal().toString());
        parameters.put("MONTO_TOTAL_CONCILIADO", this.invoice.getCabecera().getMontoTotalConciliado().toString());
        parameters.put("INVOICE_AUTORIZACION_CUF", this.invoice.getCabecera().getNumeroAutorizacionCuf());
        parameters.put("INVOICE_NUMERO_FACTURA", this.invoice.getCabecera().getNumeroFactura().toString());
        parameters.put("INVOICE_FECHA_FACTURA", XmlDateUtil.convertDateToYyyymmddhhmmssSSS(this.invoice.getCabecera().getFechaEmisionFactura()));
        parameters.put("INVOICE_CODIGO_CONTROL", this.invoice.getCabecera().getCodigoControl().toString());

        parameters.put("INVOICE_MONTO_DEBITO_FISCAL_IVA", this.invoice.getCabecera().getDebitoFiscalIva() !=  null ? this.invoice.getCabecera().getDebitoFiscalIva(): new BigDecimal("0.00"));
        parameters.put("INVOICE_MONTO_CREDITO_FISCAL_IVA",this.invoice.getCabecera().getCreditoFiscalIva()!= null ?  this.invoice.getCabecera().getCreditoFiscalIva() :new BigDecimal("0.00"));

        parameters.put("INVOICE_NUMERO_NOTA_CONCILIACION", this.invoice.getCabecera().getNumeroNotaConciliacion().toString());

        byte[] bytes = generateReportPdf(this.invoice.getDetalle(), this.invoice.getDetalleConciliacion() , parameters);

        return bytes;
    }

    protected byte[] generateReportPdf(List<?> detail, List<?>conciliacion, HashMap<String, Object> parameters) throws FileNotFoundException, JRException, URISyntaxException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

        InputStream stream = PdfCmpInvoiceConciliacion.class.getResourceAsStream(this.fileName);

        JasperReport jasperReport = JasperCompileManager.compileReport(stream);

        List<XmlBaseDetailInvoice> detailList = (List<XmlBaseDetailInvoice>) detail;
        for (XmlBaseDetailInvoice obj: detailList  ) {
            Optional<MeasurementUnit> unit =  this.measurementUnitRepository.findAllByCompanyIdAndSiatIdAndActiveIsTrue(companyId.intValue(), obj.getUnidadMedida());
            if(unit.isPresent()){
                obj.setUnidadMedidaLiteral(unit.get().getDescription());
            }else {
                obj.setUnidadMedidaLiteral("");
            }
        }

        List<XmlConciliacionDetail> conciliacionDetailList = (List<XmlConciliacionDetail>) conciliacion;
        for (XmlBaseDetailInvoice obj: conciliacionDetailList  ) {
            Optional<MeasurementUnit> unit =  this.measurementUnitRepository.findAllByCompanyIdAndSiatIdAndActiveIsTrue(companyId.intValue(), obj.getUnidadMedida());
            if(unit.isPresent()){
                obj.setUnidadMedidaLiteral(unit.get().getDescription());
            }else {
                obj.setUnidadMedidaLiteral("");
            }
        }

        InputStream stDatosOriginales = PdfCmpInvoiceConciliacion.class.getResourceAsStream(String.format("/reports/facturaConciliacion_%s_datosOriginales.jrxml", paperType));
        InputStream stDatosConciliacion = PdfCmpInvoiceConciliacion.class.getResourceAsStream(String.format("/reports/facturaConciliacion_%s_datosConciliacion.jrxml", paperType));

        JasperReport subDatosOriginales = JasperCompileManager.compileReport(stDatosOriginales);
        JasperReport subDatosConciliacion = JasperCompileManager.compileReport(stDatosConciliacion);

        parameters.put("SUBREPORT_1", new ArrayList<>(detail));
        parameters.put("SUBREPORT_2", new ArrayList<>(conciliacion));

        parameters.put("SUBREPORT_FILE_1", subDatosOriginales);
        parameters.put("SUBREPORT_FILE_2", subDatosConciliacion);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(detail.subList(0, 1));

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);

        return bytes;
    }


}
