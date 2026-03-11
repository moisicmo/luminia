package bo.com.luminia.sflbilling.msreport.siat.pdf.service;

import bo.com.luminia.sflbilling.msreport.repository.*;
import bo.com.luminia.sflbilling.msreport.siat.pdf.invoices.*;
import bo.com.luminia.sflbilling.domain.Invoice;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import bo.com.luminia.sflbilling.msreport.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msreport.repository.*;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoiceBuilder;
import bo.com.luminia.sflbilling.msreport.siat.pdf.invoices.*;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.spec.PdfInvoiceCouldBeGeneratedException;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.spec.PdfInvoiceCurrencyNotFoundException;
import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlSectorType;
import bo.gob.impuestos.sfe.invoice.xml.cmp.*;
import bo.gob.impuestos.sfe.invoice.xml.eltr.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.zxing.WriterException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class PdfGeneratorService {

    private final CurrencyTypeRepository currencyTypeRepository;
    private final InvoiceRepository invoiceRepository;
    private final ApprovedProductRepository approvedProductRepository;
    private final ActivityRepository activityRepository;
    private final MeasurementUnitRepository measurementUnitRepository;
    private final ApplicationProperties properties ;

    private static final Integer EN_LINEA = 1;
    private static final Integer FUERA_LINEA = 2;
    private static final Integer MASIVO = 3;

    private String getLeyendaModalidad(Invoice i) {
        // FUERA DE LINEA
        if (i.getBroadcastType().getSiatId() == FUERA_LINEA) {
            return "\"Este documento es la Representación Gráfica de un Documento Fiscal Digital emitido fuera de línea, verifique su envío con su proveedor o en la página web www.impuestos.gob.bo\"";
        } else { // if (i.getBroadcastType().getSiatId() == EN_LINEA || i.getBroadcastType().getSiatId() == MASIVO){
            return "\"Este documento es la Representación Gráfica de un Documento Fiscal Digital emitido en una modalidad de facturación en línea\"";
        }
    }

    //TODO colocar mas logs
    public byte[] exportReportPdf(String cuf, String paperType) {

        Optional<Invoice> data = invoiceRepository.findInvoiceByCuf(cuf);
        byte[] bytes = null;
        try {
            XmlBaseInvoice baseInvoice = null;
            if (data.isPresent()) {
                Invoice invoice = data.get();
                byte[] logo = invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getLogo();
                Image imageLogo = createImageFromBytes(logo);
                log.info(String.format("Generando reporte para %s", cuf));

                //1
                if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_COMPRA_VENTA)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlCompraVentaEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlCompraVentaEltrInvoice.class);
                        PdfEltrInvoiceCompraVenta pdfInvoice = new PdfEltrInvoiceCompraVenta((XmlCompraVentaEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlCompraVentaCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlCompraVentaCmpInvoice.class);
                        PdfCmpInvoiceCompraVenta pdfInvoice = new PdfCmpInvoiceCompraVenta((XmlCompraVentaCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                    //2
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlAlquilerBienesInmueblesEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlAlquilerBienesInmueblesEltrInvoice.class);
                        PdfEltrInvoiceAlquilerBienesInmuebles pdfInvoice = new PdfEltrInvoiceAlquilerBienesInmuebles((XmlAlquilerBienesInmueblesEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlAlquilerBienesInmueblesCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlAlquilerBienesInmueblesCmpInvoice.class);
                        PdfCmpInvoiceAlquilerBienesInmuebles pdfInvoice = new PdfCmpInvoiceAlquilerBienesInmuebles((XmlAlquilerBienesInmueblesCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                    //3
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlEntidadesFinancierasEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlEntidadesFinancierasEltrInvoice.class);
                        PdfEltrEntidadesFinancieras pdfInvoice = new PdfEltrEntidadesFinancieras((XmlEntidadesFinancierasEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlEntidadesFinancierasCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlEntidadesFinancierasCmpInvoice.class);
                        PdfCmpInvoiceEntidadesFinancieras pdfInvoice = new PdfCmpInvoiceEntidadesFinancieras((XmlEntidadesFinancierasCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                    //4
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_TELECOMUNICACIONES)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlTelecomunicacionesEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlTelecomunicacionesEltrInvoice.class);
                        PdfEltrInvoiceTelecomunicaciones pdfInvoice = new PdfEltrInvoiceTelecomunicaciones((XmlTelecomunicacionesEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlTelecomunicacionesCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlTelecomunicacionesCmpInvoice.class);
                        PdfCmpInvoiceTelecomunicaciones pdfInvoice = new PdfCmpInvoiceTelecomunicaciones((XmlTelecomunicacionesCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                    //5
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_SERVICIOS_BASICOS)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlServiciosBasicosEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlServiciosBasicosEltrInvoice.class);
                        PdfEltrInvoiceServiciosBasicos pdfInvoice = new PdfEltrInvoiceServiciosBasicos((XmlServiciosBasicosEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlServiciosBasicosCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlServiciosBasicosCmpInvoice.class);
                        PdfCmpInvoiceServiciosBasicos pdfInvoice = new PdfCmpInvoiceServiciosBasicos((XmlServiciosBasicosCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                    //6
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlHidrocarburosEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlHidrocarburosEltrInvoice.class);
                        PdfEltrInvoiceComercializacionHidrocarburos pdfInvoice = new PdfEltrInvoiceComercializacionHidrocarburos((XmlHidrocarburosEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlHidrocarburosCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlHidrocarburosCmpInvoice.class);
                        PdfCmpInvoiceComercializacionHidrocarburos pdfInvoice = new PdfCmpInvoiceComercializacionHidrocarburos((XmlHidrocarburosCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                    //7
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_SECTOR_EDUCATIVO)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlSectorEducativoEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlSectorEducativoEltrInvoice.class);
                        PdfEltrInvoiceSectorEducativo pdfInvoice = new PdfEltrInvoiceSectorEducativo((XmlSectorEducativoEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlSectorEducativoCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlSectorEducativoCmpInvoice.class);
                        PdfCmpInvoiceSectorEducativo pdfInvoice = new PdfCmpInvoiceSectorEducativo((XmlSectorEducativoCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                    //8
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlAlimentariaAbastecimientoEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlAlimentariaAbastecimientoEltrInvoice.class);
                        PdfEltrInvoiceSeguridadAlimentariaAbastecimiento pdfInvoice = new PdfEltrInvoiceSeguridadAlimentariaAbastecimiento((XmlAlimentariaAbastecimientoEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlAlimentariaAbastecimientoCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlAlimentariaAbastecimientoCmpInvoice.class);
                        PdfCmpInvoiceSeguridadAlimentariaAbastecimiento pdfInvoice = new PdfCmpInvoiceSeguridadAlimentariaAbastecimiento((XmlAlimentariaAbastecimientoCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                    //9
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_HOSPITAL_CLINICAS)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlHospitalClinicaEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlHospitalClinicaEltrInvoice.class);
                        PdfEltrInvoiceHospitalesClinicas pdfInvoice = new PdfEltrInvoiceHospitalesClinicas((XmlHospitalClinicaEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlHospitalClinicaCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlHospitalClinicaCmpInvoice.class);
                        PdfCmpInvoiceHospitalesClinicas pdfInvoice = new PdfCmpInvoiceHospitalesClinicas((XmlHospitalClinicaCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                    //10
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlTuristicoHospedajeEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlTuristicoHospedajeEltrInvoice.class);
                        PdfEltrInvoiceServicioHospedaje pdfInvoice = new PdfEltrInvoiceServicioHospedaje((XmlTuristicoHospedajeEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlTuristicoHospedajeCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlTuristicoHospedajeEltrInvoice.class);
                        PdfCmpInvoiceServicioHospedaje pdfInvoice = new PdfCmpInvoiceServicioHospedaje((XmlTuristicoHospedajeCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                    //11
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_FACTURAS_HOTELES)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlHotelEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlHotelEltrInvoice.class);
                        PdfEltrInvoiceHoteles pdfInvoice = new PdfEltrInvoiceHoteles((XmlHotelEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlHotelCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlHotelCmpInvoice.class);
                        PdfCmpInvoiceHoteles pdfInvoice = new PdfCmpInvoiceHoteles((XmlHotelCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                    //12
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlExportacionServicioEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlExportacionServicioEltrInvoice.class);
                        PdfEltrInvoiceExportacionServicios pdfInvoice = new PdfEltrInvoiceExportacionServicios((XmlExportacionServicioEltrInvoice) baseInvoice, paperType, currencyTypeRepository, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlExportacionServicioCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlExportacionServicioCmpInvoice.class);
                        PdfCmpInvoiceExportacionServicios pdfInvoice = new PdfCmpInvoiceExportacionServicios((XmlExportacionServicioCmpInvoice) baseInvoice, paperType, currencyTypeRepository, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_NOTA_CREDITO_DEBITO)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlNotaCreditoDebitoEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlNotaCreditoDebitoEltrInvoice.class);
                        PdfEltrInvoiceNotaCreditoDebito pdfInvoice = new PdfEltrInvoiceNotaCreditoDebito((XmlNotaCreditoDebitoEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlNotaCreditoDebitoCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlNotaCreditoDebitoCmpInvoice.class);
                        PdfCmpInvoiceNotaCreditoDebito pdfInvoice = new PdfCmpInvoiceNotaCreditoDebito((XmlNotaCreditoDebitoCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_PREVALORADA)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlPrevaloradaEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlPrevaloradaEltrInvoice.class);
                        PdfEltrInvoicePreValorada pdfInvoice = new PdfEltrInvoicePreValorada((XmlPrevaloradaEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlPrevaloradaCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlPrevaloradaCmpInvoice.class);
                        PdfCmpInvoicePreValorada pdfInvoice = new PdfCmpInvoicePreValorada((XmlPrevaloradaCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                }else  if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {

                        baseInvoice = (XmlComercialExportacionEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlComercialExportacionEltrInvoice.class);
                        PdfEltrInvoiceComercialExportacion pdfInvoice = new PdfEltrInvoiceComercialExportacion((XmlComercialExportacionEltrInvoice) baseInvoice, paperType, currencyTypeRepository, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {

                        baseInvoice = (XmlComercialExportacionCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlComercialExportacionCmpInvoice.class);
                        PdfCmpInvoiceComercialExportacion pdfInvoice = new PdfCmpInvoiceComercialExportacion((XmlComercialExportacionCmpInvoice) baseInvoice, paperType, currencyTypeRepository, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                }else  if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_NOTA_CONCILIACION)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {

                        baseInvoice = (XmlConciliacionEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlConciliacionEltrInvoice.class);
                        PdfEltrInvoiceConciliacion pdfInvoice = new PdfEltrInvoiceConciliacion((XmlConciliacionEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());

                        return pdfInvoice.generate();

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {

                        baseInvoice = (XmlConciliacionCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlConciliacionCmpInvoice.class);
                        PdfCmpInvoiceConciliacion pdfInvoice = new PdfCmpInvoiceConciliacion((XmlConciliacionCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_SEGUROS)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlSegurosEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlSegurosEltrInvoice.class);
                        PdfEltrInvoiceSeguros pdfInvoice = new PdfEltrInvoiceSeguros((XmlSegurosEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlSegurosCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlSegurosCmpInvoice.class);
                        PdfCmpInvoiceSeguros pdfInvoice = new PdfCmpInvoiceSeguros((XmlSegurosCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_ZONA_FRANCA)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlZonaFrancaEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlZonaFrancaEltrInvoice.class);
                        PdfEltrInvoiceZonaFranca pdfInvoice = new PdfEltrInvoiceZonaFranca((XmlZonaFrancaEltrInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlZonaFrancaCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlZonaFrancaCmpInvoice.class);
                        PdfCmpInvoiceZonaFranca pdfInvoice = new PdfCmpInvoiceZonaFranca((XmlZonaFrancaCmpInvoice) baseInvoice, paperType, imageLogo);
                        pdfInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        pdfInvoice.setActivityRepository(this.activityRepository);
                        pdfInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        pdfInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        pdfInvoice.setPointOfSaleId(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
                        pdfInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        pdfInvoice.setBranchOffice(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getName());
                        pdfInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        pdfInvoice.setSiatUrl(properties.getSiatUrl());
                        return pdfInvoice.generate();
                    }
                    //6
                }
            }
        } catch (PdfInvoiceCurrencyNotFoundException e) {
            log.error(String.format("Error generando reporte %s. No se encontro la moneda especificada ", cuf), e.getMessage());
            e.printStackTrace();
            throw new PdfInvoiceCouldBeGeneratedException(String.format("Error generando reporte %s. No se encontro la moneda especificada ", cuf));
        } catch (FileNotFoundException | JRException e) {
            log.error(String.format("Error generando reporte %s", cuf), e.getMessage());
            e.printStackTrace();
            throw new PdfInvoiceCouldBeGeneratedException(String.format("Error generando reporte %s. No se encotraron los archivos de reporte ", cuf));
        } catch (JsonProcessingException e) {
            log.error(String.format("Error generando reporte %s", cuf), e.getMessage());
            e.printStackTrace();
            throw new PdfInvoiceCouldBeGeneratedException(String.format("Error generando reporte %s. Contacte al administrador", cuf));
        } catch (IOException e) {
            log.error(String.format("Error generando reporte %s", cuf), e.getMessage());
            e.printStackTrace();
            throw new PdfInvoiceCouldBeGeneratedException(String.format("Error generando reporte %s. Contacte al administrador", cuf));
        } catch (WriterException | URISyntaxException e) {
            log.error(String.format("Error generando reporte %s", cuf), e.getMessage());
            e.printStackTrace();
            throw new PdfInvoiceCouldBeGeneratedException(String.format("Error generando reporte %s. Contacte al administrador", cuf));
        }
        return bytes;
    }

    private BufferedImage createImageFromBytes(byte[] imageData) {
        if (imageData == null) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
