package bo.com.luminia.sflbilling.msreport.siat.pdf.invoices;

import bo.com.luminia.sflbilling.domain.CurrencyType;
import bo.com.luminia.sflbilling.domain.MeasurementUnit;
import bo.com.luminia.sflbilling.msreport.repository.CurrencyTypeRepository;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoice;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoiceParameters;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfProxy;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.spec.PdfInvoiceCurrencyNotFoundException;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlBaseDetailInvoice;
import bo.gob.impuestos.sfe.invoice.xml.eltr.XmlComercialExportacionEltrInvoice;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlComercialExportacionHeader;
import com.google.gson.Gson;
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
import java.util.List;
import java.util.*;

public class PdfEltrInvoiceComercialExportacion extends PdfInvoice<XmlComercialExportacionEltrInvoice, XmlComercialExportacionHeader> implements PdfProxy {

    private CurrencyTypeRepository repository;

    public PdfEltrInvoiceComercialExportacion(XmlComercialExportacionEltrInvoice invoice, String paperType,
                                              CurrencyTypeRepository currencyTypeRepository, Image logo) {
        super(invoice, paperType, logo);
        this.fileName = String.format("/reports/facturaComercialExportacion_%s.jrxml", paperType);
        this.repository = currencyTypeRepository;
        this.productCode = invoice.getDetalle().get(0).getCodigoProducto();
    }

    public HashMap<String, Object> getDefaultParametersForHeader(XmlComercialExportacionHeader header)
        throws IOException, WriterException {

        Optional<CurrencyType> currencyType = repository.findDistinctFirstBySiatIdAndActiveIsTrue(header.getCodigoMoneda());

        if (!currencyType.isPresent()) {
            throw new PdfInvoiceCurrencyNotFoundException(String.format("Codigo de Moneda no encontrado: %d", header.getCodigoMoneda()));
        }

        HashMap<String, Object> parameters = getDefaultParameters(header);
        parameters.put(PdfInvoiceParameters.INVOICE_DIRECCION_COMPRADOR, header.getDireccionComprador());
        parameters.put(PdfInvoiceParameters.INVOICE_TIPO_CAMBIO, header.getTipoCambio().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_LUGAR_DESTINO, header.getLugarDestino());
        parameters.put(PdfInvoiceParameters.INVOICE_PUERTO_DESTINO, header.getPuertoDestino());
        parameters.put(PdfInvoiceParameters.INVOICE_INFORMACION_ADICIONAL, header.getInformacionAdicional());
        parameters.put(PdfInvoiceParameters.INVOICE_MONEDA_TIPO_CAMBIO_LITERAL, currencyType.get().getDescription());
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_TOTAL_TIPO_CAMBIO_LITERAL, integerToLiteral(header.getMontoTotalMoneda().intValue()));
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_TOTAL_TIPO_CAMBIO, header.getMontoTotalMoneda());
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_TOTAL_TIPO_CAMBIO_DECIMAL, obtainDecimalPart(header.getMontoTotalMoneda()));
        parameters.put(PdfInvoiceParameters.INVOICE_SUBTOTAL_TIPO_CAMBIO, header.getMontoTotalSujetoIva());
        parameters.put(PdfInvoiceParameters.INVOICE_DESCUENTO_TIPO_CAMBIO, header.getDescuentoAdicional());
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_DETALLE, header.getMontoDetalle());
        parameters.put(PdfInvoiceParameters.INVOICE_INCOTERM, header.getIncoterm());
        parameters.put(PdfInvoiceParameters.INVOICE_INCOTERM_DETALLE, header.getIncotermDetalle());
        parameters.put(PdfInvoiceParameters.INVOICE_NAC_TOTAL_FOB, header.getTotalGastosNacionalesFob());
        parameters.put(PdfInvoiceParameters.INVOICE_INT_TOTAL_CIF, header.getTotalGastosInternacionales());
        parameters.put(PdfInvoiceParameters.INVOICE_INFORMACION_ADICIONAL, header.getInformacionAdicional());
        parameters.put(PdfInvoiceParameters.INVOICE_DESCRIPCION_PAQUETES, header.getNumeroDescripcionPaquetesBultos());

        parameters.put("SUBREPORT_GASTO_NACIONAL", convertoJsonToGastos(header.getCostosGastosNacionales()));
        parameters.put("SUBREPORT_GASTO_INTERNACIONAL", convertoJsonToGastos(header.getCostosGastosInternacionales()));

        return parameters;
    }

    @Override
    public byte[] generate() throws IOException, WriterException, JRException, PdfInvoiceCurrencyNotFoundException, URISyntaxException {

        HashMap<String, Object> parameters = this.getDefaultParametersForHeader(this.invoice.getCabecera());

        byte[] bytes = generateReportPdf2(this.invoice.getDetalle(), parameters);

        return bytes;
    }

    protected byte[] generateReportPdf2(List<?> detail, HashMap<String, Object> parameters) throws FileNotFoundException,
        JRException, URISyntaxException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

        InputStream stream = PdfCmpInvoiceNotaCreditoDebito.class.getResourceAsStream(this.fileName);

        //File f = ResourceUtils.getFile(this.fileName);
        //JasperReport jasperReport = JasperCompileManager.compileReport(f.getAbsolutePath());
        JasperReport jasperReport = JasperCompileManager.compileReport(stream);

        InputStream stDetalle = PdfCmpInvoiceNotaCreditoDebito.class.getResourceAsStream(String.format("/reports/facturaComercialExportacion_%s_Detalle.jrxml", paperType));
        InputStream stNacional = PdfCmpInvoiceNotaCreditoDebito.class.getResourceAsStream(String.format("/reports/facturaComercialExportacion_%s_GastosNacionales.jrxml", paperType));
        InputStream stInternacional = PdfCmpInvoiceNotaCreditoDebito.class.getResourceAsStream(String.format("/reports/facturaComercialExportacion_%s_GastosInternacionales.jrxml", paperType));
        //File f1 = ResourceUtils.getFile((String.format("classpath:reports/datos_original_%s.jasper", paperType)));
        //File f2 = ResourceUtils.getFile((String.format("classpath:reports/devolucion_%s.jasper", paperType)));

        JasperReport subDetalle = JasperCompileManager.compileReport(stDetalle);
        JasperReport subNacional = JasperCompileManager.compileReport(stNacional);
        JasperReport subInternacional = JasperCompileManager.compileReport(stInternacional);

        List<XmlBaseDetailInvoice> detailList = (List<XmlBaseDetailInvoice>) detail;
        for (XmlBaseDetailInvoice obj: detailList  ) {
            Optional<MeasurementUnit> unit =  this.measurementUnitRepository.findAllByCompanyIdAndSiatIdAndActiveIsTrue(companyId.intValue(), obj.getUnidadMedida());
            if(unit.isPresent()){
                obj.setUnidadMedidaLiteral(unit.get().getDescription());
            }else {
                obj.setUnidadMedidaLiteral("");
            }
        }

        parameters.put("SUBREPORT_DETAIL", new ArrayList<>(detail));

        parameters.put("SUBREPORT_DETAIL_FILE", subDetalle);
        parameters.put("SUBREPORT_GASTO_NACIONAL_FILE", subNacional);
        parameters.put("SUBREPORT_GASTO_INTERNACIONAL_FILE", subInternacional);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(detail.subList(0, 1));

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);

        return bytes;
    }

    private ArrayList<PdfComercialExportacionGastos> convertoJsonToGastos(String json){
        ArrayList<PdfComercialExportacionGastos> response = new ArrayList<>();

        if (json == null){
            return response ;
        }

        if (json.length() <= 1){
            return response;
        }

        Gson g = new Gson();
        Map<String, String> s = g.fromJson(json, HashMap.class);
        if (!s.isEmpty()){
            s.entrySet().stream().forEach(e -> {
                PdfComercialExportacionGastos data = new PdfComercialExportacionGastos();
                data.setDescripcion(e.getKey());
                data.setSubTotal( new BigDecimal(e.getValue()));
                response.add(data);
            });
        }
        return response;
    }

}
