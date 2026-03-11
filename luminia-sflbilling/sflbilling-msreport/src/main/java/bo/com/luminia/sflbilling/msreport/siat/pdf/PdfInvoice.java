package bo.com.luminia.sflbilling.msreport.siat.pdf;

import bo.com.luminia.sflbilling.domain.Activity;
import bo.com.luminia.sflbilling.domain.ApprovedProduct;
import bo.com.luminia.sflbilling.domain.MeasurementUnit;
import bo.com.luminia.sflbilling.msreport.repository.ActivityRepository;
import bo.com.luminia.sflbilling.msreport.repository.ApprovedProductRepository;
import bo.com.luminia.sflbilling.msreport.repository.MeasurementUnitRepository;
import bo.com.luminia.sflbilling.msreport.siat.pdf.literal.Spanish;
import bo.gob.impuestos.sfe.invoice.xml.XmlDateUtil;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlBaseDetailInvoice;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlBaseHeaderInvoice;
import com.google.zxing.WriterException;
import lombok.Setter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class PdfInvoice<T, H> {

    public static final String PAPER_A4 = "a4";
    public static final String PAPER_ROLL = "roll";

    private static final String TIPO = "2";

    protected String paperType;
    protected String fileName;
    protected int dynamicHeightBand;
    protected String absolutePath;
    protected Long companyId;
    protected String productCode;
    protected Integer measurementUnit;
    protected T invoice;
    protected Image logo;

    public Integer pointOfSaleId;
    public String pointOfSale;

    public String branchOffice;
    protected ApprovedProductRepository approvedProductRepository;
    protected ActivityRepository activityRepository;
    protected MeasurementUnitRepository measurementUnitRepository;
    @Setter
    protected String modalidadLeyenda;
    public String siatUrl;

    public PdfInvoice(T invoice, String paperType, Image logo) {
        this.invoice = invoice;
        this.paperType = paperType;
        this.logo = logo;
    }

    /**
     * @param value
     * @return
     */
    protected String obtainDecimalPart(BigDecimal value) {
        BigDecimal fractional = value.remainder(BigDecimal.ONE);
        String result = fractional.setScale(2, BigDecimal.ROUND_CEILING).toString();
        return result.substring(result.indexOf('.') + 1);
    }

    /**
     * @param value
     * @return
     */
    protected String integerToLiteral(Integer value) {
        Spanish literal = new Spanish();
        String result = literal.convert(value);
        result = result.substring(0, 1).toUpperCase() + result.substring(1);
        return result;
    }

    /**
     * Convierte los valores del subtotal de los detalles en el Subtotal del Reporte
     *
     * @param details
     * @return
     */
    public static BigDecimal detailsToSubtotal(List<?> details) {
        BigDecimal detailsOfSubtotal = new BigDecimal(0);

        for (Object xmlBaseDetailInvoice : details) {
            detailsOfSubtotal = detailsOfSubtotal.add(((XmlBaseDetailInvoice) xmlBaseDetailInvoice).getSubTotal());
        }

        return detailsOfSubtotal;
    }

    /**
     * @param
     * @return
     */
    protected HashMap<String, Object> getDefaultParameters(XmlBaseHeaderInvoice header) throws IOException, WriterException {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(PdfInvoiceParameters.INVOICE_CUF, header.getCuf());
        parameters.put(PdfInvoiceParameters.INVOICE_DIRECCION, header.getDireccion());
        parameters.put(PdfInvoiceParameters.INVOICE_LEYENDA, header.getLeyenda());
        parameters.put(PdfInvoiceParameters.INVOICE_NIT_EMISION, String.valueOf(header.getNitEmisor()));
        parameters.put(PdfInvoiceParameters.INVOICE_NUMERO_DOCUMENTO, header.getNumeroDocumento());
        parameters.put(PdfInvoiceParameters.INVOICE_FECHA_EMISION, XmlDateUtil.convertDateToYyyymmddhhmmssSSS(header.getFechaEmision()));
        parameters.put(PdfInvoiceParameters.INVOICE_TELEFONO, header.getTelefono());
        parameters.put(PdfInvoiceParameters.INVOICE_MUNICIPIO, header.getMunicipio());
        parameters.put(PdfInvoiceParameters.INVOICE_RAZON_SOCIAL_EMISOR, header.getRazonSocialEmisor());
        parameters.put(PdfInvoiceParameters.INVOICE_NOMBRE_RAZON_SOCIAL, header.getNombreRazonSocial());
        parameters.put(PdfInvoiceParameters.INVOICE_NUMERO_FACTURA, String.valueOf(header.getNumeroFactura()));
        parameters.put(PdfInvoiceParameters.INVOICE_TOTAL_LITERAL, integerToLiteral(header.getMontoTotal().intValue()));
        parameters.put(PdfInvoiceParameters.INVOICE_TOTAL_SUJETO_IVA_LITERAL, integerToLiteral(header.getMontoTotalSujetoIva().intValue()));
        // IMPLEMENTACION BARCODE 24-03-2022
        parameters.put(PdfInvoiceParameters.INVOICE_BARCODE, header.getBarcode());
        // IMPLEMENTACION DE CABECERA DINAMICA 24-03-2022


        File file = null;
        String fileR = "reports/subReports";
        ClassPathResource resource = new ClassPathResource(fileR);
        String path = "";
        if (resource.getFile().exists()) {
            path = resource.getFile().getAbsolutePath();
        }
        parameters.put(PdfInvoiceParameters.SUBREPORT_DIR, path);
        int sbrHeight = 44;

        if (this.fileName.contains(PAPER_ROLL)) {
            dynamicHeightBand = header.getOptionalHeader().size() * sbrHeight;
        }
        JRBeanCollectionDataSource subDS = new JRBeanCollectionDataSource(header.getOptionalHeader());
        parameters.put(PdfInvoiceParameters.INVOICE_HEADER, subDS);

        // Se debe confirmar con la nota de exportacion, nota debito, conciliacion si se envia Dolares
        // ya que agregaron una funcionalidad dnd se envia la moneda usada dolares
        // pero siempre se imprime bolivianos
        parameters.put(PdfInvoiceParameters.INVOICE_MONEDA_LITERAL, "Bolivianos");
        parameters.put(PdfInvoiceParameters.INVOICE_TOTAL_DECIMAL, this.obtainDecimalPart(header.getMontoTotal()));
        parameters.put(PdfInvoiceParameters.INVOICE_COD_CLIENTE, header.getCodigoCliente());
        parameters.put(PdfInvoiceParameters.INVOICE_PUNTO_VENTA, this.getPointOfSale());
        parameters.put(PdfInvoiceParameters.INVOICE_NUMERO_PUNTO_VENTA, this.getPointOfSaleId().toString());
        parameters.put(PdfInvoiceParameters.INVOICE_SUCURSAL, this.getBranchOffice());
        parameters.put(PdfInvoiceParameters.INVOICE_MODALIDAD_LEYENDA, this.modalidadLeyenda);

        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_TOTAL, (header.getMontoTotal()));
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_TOTAL_SUJETO_IVA, (header.getMontoTotalSujetoIva()));

        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_DESCUENTO, header.getDescuentoAdicional() != null ? header.getDescuentoAdicional() : new BigDecimal("0.00"));
        parameters.put(PdfInvoiceParameters.INVOICE_MONTO_GIFT_CARD, header.getMontoGiftCard() != null ? header.getMontoGiftCard() : new BigDecimal("0.00"));

        parameters.put(PdfInvoiceParameters.INVOICE_QR, PdfQrGenerator.generateImage(
            String.valueOf(header.getNitEmisor()), header.getCuf(),
            String.valueOf(header.getNumeroFactura()), TIPO, this.siatUrl
        ));

        parameters.put(PdfInvoiceParameters.INVOICE_LOGO, this.logo);

        Optional<ApprovedProduct> fromDb = approvedProductRepository.findApprovedProductByCompanyIdAndProductCode(companyId, productCode);
        if (fromDb.isPresent()) {
            ApprovedProduct approvedProduct = fromDb.get();

            Optional<Activity> actFromDb = activityRepository.findActivityBySiatIdAndCompanyId(approvedProduct.getProductService().getActivityCode(), approvedProduct.getCompany().getId().intValue());
            if (actFromDb.isPresent()) {
                Activity activity = actFromDb.get();
                parameters.put(PdfInvoiceParameters.INVOICE_ACTIVIDAD, activity.getDescription());
            } else {
                parameters.put(PdfInvoiceParameters.INVOICE_ACTIVIDAD, "-");
            }


        } else {
            parameters.put(PdfInvoiceParameters.INVOICE_ACTIVIDAD, "-");
        }

        if (header.getOptional() != null && !header.getOptional().isEmpty()) {
            int i = 1;
            for (Object item : header.getOptional()) {
                HashMap<String, String> itemInHash = (HashMap<String, String>) item;
                if (paperType == PAPER_A4) {
                    parameters.put("KEY_" + i, itemInHash.get("key") + ":");
                } else {
                    parameters.put("KEY_" + i, itemInHash.get("key"));
                }

                parameters.put("VALUE_" + i, itemInHash.get("value"));
                i++;
            }
        }

        parameters.put(PdfInvoiceParameters.INVOICE_COMPLEMENTO, header.getComplemento());

        return parameters;
    }

    protected BigDecimal createSubTotal(List<BigDecimal> subTotales) {
        BigDecimal subTotal = new BigDecimal(0);
        subTotales.forEach(bigDecimal -> {
            subTotal.add(bigDecimal);
        });

        return subTotal;
    }

    protected byte[] generateReportPdf(List<?> detail, HashMap<String, Object> parameters) throws FileNotFoundException, JRException, URISyntaxException {
        InputStream stream = PdfInvoice.class.getResourceAsStream(this.fileName);
        JasperReport jasperReport = JasperCompileManager.compileReport(stream);

        List<XmlBaseDetailInvoice> detailList = (List<XmlBaseDetailInvoice>) detail;
        for (XmlBaseDetailInvoice obj : detailList) {
            Optional<MeasurementUnit> unit = this.measurementUnitRepository.findAllByCompanyIdAndSiatIdAndActiveIsTrue(companyId.intValue(), obj.getUnidadMedida());
            if (unit.isPresent()) {
                obj.setUnidadMedidaLiteral(unit.get().getDescription());
            } else {
                obj.setUnidadMedidaLiteral("");
            }
        }
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(detail);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);

        return bytes;
    }

    protected abstract HashMap<String, Object> getDefaultParametersForHeader(H header) throws IOException, WriterException;


    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public void setApprovedProductRepository(ApprovedProductRepository approvedProductRepository) {
        this.approvedProductRepository = approvedProductRepository;
    }

    public void setActivityRepository(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public void setMeasurementUnitRepository(MeasurementUnitRepository measurementUnitRepository) {
        this.measurementUnitRepository = measurementUnitRepository;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getPointOfSaleId() {
        return pointOfSaleId;
    }

    public void setPointOfSaleId(Integer pointOfSaleId) {
        this.pointOfSaleId = pointOfSaleId;
    }

    public String getPointOfSale() {
        return pointOfSale;
    }

    public void setPointOfSale(String pointOfSale) {
        this.pointOfSale = pointOfSale;
    }

    public String getBranchOffice() {
        return branchOffice;
    }

    public void setBranchOffice(String branchOffice) {
        this.branchOffice = branchOffice;
    }

    public void setMeasurementUnit(Integer measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public String getSiatUrl() {
        return siatUrl;
    }

    public void setSiatUrl(String siatUrl) {
        this.siatUrl = siatUrl;
    }
}
