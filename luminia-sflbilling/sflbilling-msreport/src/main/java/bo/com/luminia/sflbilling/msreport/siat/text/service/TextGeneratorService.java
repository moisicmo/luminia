package bo.com.luminia.sflbilling.msreport.siat.text.service;

import bo.com.luminia.sflbilling.msreport.repository.*;
import bo.com.luminia.sflbilling.msreport.siat.text.invoices.*;
import bo.com.luminia.sflbilling.domain.Invoice;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import bo.com.luminia.sflbilling.msreport.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msreport.repository.*;
import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoiceBuilder;
import bo.com.luminia.sflbilling.msreport.siat.text.invoices.*;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.spec.PdfInvoiceCurrencyNotFoundException;
import bo.com.luminia.sflbilling.msreport.web.rest.errors.spec.TextInvoiceCouldBeGeneratedException;
import bo.com.luminia.sflbilling.msreport.web.rest.response.TextInvoiceResponse;
import bo.com.luminia.sflbilling.msreport.web.rest.util.ResponseCodes;
import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlSectorType;
import bo.gob.impuestos.sfe.invoice.xml.cmp.*;
import bo.gob.impuestos.sfe.invoice.xml.eltr.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class TextGeneratorService {

    private final InvoiceRepository invoiceRepository;
    private final ActivityRepository activityRepository;
    private final ApprovedProductRepository approvedProductRepository;
    private final CurrencyTypeRepository currencyTypeRepository;
    private final ApplicationProperties properties;
    private final MeasurementUnitRepository measurementUnitRepository;

    private static final Integer EN_LINEA = 1;
    private static final Integer FUERA_LINEA = 2;
    private static final Integer MASIVO = 3;

    private String getLeyendaModalidad(Invoice i) {
        // FUERA DE LINEA
        if (i.getBroadcastType().getSiatId() == FUERA_LINEA) {
            return "Este documento es la Representación Gráfica de un Documento Fiscal Digital emitido fuera de línea, verifique su envío con su proveedor o en la página web www.impuestos.gob.bo";
        } else { // if (i.getBroadcastType().getSiatId() == EN_LINEA || i.getBroadcastType().getSiatId() == MASIVO){
            return "Este documento es la Representación Gráfica de un Documento Fiscal Digital emitido en una modalidad de facturación en línea" ;
        }
    }

    public TextInvoiceResponse exportReportText(String cuf, String paperType)
        throws TextInvoiceCouldBeGeneratedException {
        TextInvoiceResponse response = new TextInvoiceResponse();

        Optional<Invoice> data = invoiceRepository.findInvoiceByCuf(cuf);
        byte[] bytes = null;
        try {
            XmlBaseInvoice baseInvoice = null;
            if (data.isPresent()) {
                Invoice invoice = data.get();
                byte[] logo = invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getLogo();
                log.info(String.format("Generando reporte para %s", cuf));
                //1
                if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_COMPRA_VENTA)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlCompraVentaEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlCompraVentaEltrInvoice.class);
                        TextEltrCompraVentaInvoice textInvoice = new TextEltrCompraVentaInvoice((XmlCompraVentaEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlCompraVentaCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlCompraVentaCmpInvoice.class);

                        TextCmpCompraVentaInvoice textInvoice = new TextCmpCompraVentaInvoice((XmlCompraVentaCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);
                        response.setBody(textInvoice.generate());

                    }
                    //2
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlAlquilerBienesInmueblesEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlAlquilerBienesInmueblesEltrInvoice.class);

                        TextEltrAlquilerBienesInmueblesInvoice textInvoice = new TextEltrAlquilerBienesInmueblesInvoice((XmlAlquilerBienesInmueblesEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlAlquilerBienesInmueblesCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlAlquilerBienesInmueblesCmpInvoice.class);

                        TextCmpAlquilerBienesInmueblesInvoice textInvoice = new TextCmpAlquilerBienesInmueblesInvoice((XmlAlquilerBienesInmueblesCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());
                    }
                    //3
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlEntidadesFinancierasEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlEntidadesFinancierasEltrInvoice.class);

                        TextEltrEntidadesFinancierasInvoice textInvoice = new TextEltrEntidadesFinancierasInvoice((XmlEntidadesFinancierasEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlEntidadesFinancierasCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlEntidadesFinancierasCmpInvoice.class);

                        TextCmpEntidadesFinancierasInvoice textInvoice = new TextCmpEntidadesFinancierasInvoice((XmlEntidadesFinancierasCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());
                    }
                    //4
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_TELECOMUNICACIONES)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlTelecomunicacionesEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlTelecomunicacionesEltrInvoice.class);

                        TextEltrTelecomunicacionesInvoice textInvoice = new TextEltrTelecomunicacionesInvoice((XmlTelecomunicacionesEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlTelecomunicacionesCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlTelecomunicacionesCmpInvoice.class);

                        TextCmpTelecomunicacionesInvoice textInvoice = new TextCmpTelecomunicacionesInvoice((XmlTelecomunicacionesCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());
                    }
                    //5
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_SERVICIOS_BASICOS)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlServiciosBasicosEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlServiciosBasicosEltrInvoice.class);

                        TextEltrServiciosBasicosInvoice textInvoice = new TextEltrServiciosBasicosInvoice((XmlServiciosBasicosEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setCurrencyTypeRepository(currencyTypeRepository);
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());
                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlServiciosBasicosCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlServiciosBasicosCmpInvoice.class);

                        TextCmpServiciosBasicosInvoice textInvoice = new TextCmpServiciosBasicosInvoice((XmlServiciosBasicosCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setCurrencyTypeRepository(currencyTypeRepository);
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());
                    }
                    //6
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlHidrocarburosEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlHidrocarburosEltrInvoice.class);

                        TextEltrComercializacionHidrocarburosInvoice textInvoice = new TextEltrComercializacionHidrocarburosInvoice((XmlHidrocarburosEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlHidrocarburosCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlHidrocarburosCmpInvoice.class);

                        TextCmpComercializacionHidrocarburosInvoice textInvoice = new TextCmpComercializacionHidrocarburosInvoice((XmlHidrocarburosCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());
                    }
                    //7
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_SECTOR_EDUCATIVO)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlSectorEducativoEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlSectorEducativoEltrInvoice.class);

                        TextEltrSectorEducativoInvoice textInvoice = new TextEltrSectorEducativoInvoice((XmlSectorEducativoEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setCurrencyTypeRepository(currencyTypeRepository);
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlSectorEducativoCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlSectorEducativoCmpInvoice.class);

                        TextCmpSectorEducativoInvoice textInvoice = new TextCmpSectorEducativoInvoice((XmlSectorEducativoCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setCurrencyTypeRepository(currencyTypeRepository);
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());
                    }
                    //8
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlAlimentariaAbastecimientoEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlAlimentariaAbastecimientoEltrInvoice.class);

                        TextEltrSeguridadAlimentariaInvoice textInvoice = new TextEltrSeguridadAlimentariaInvoice((XmlAlimentariaAbastecimientoEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlAlimentariaAbastecimientoCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlAlimentariaAbastecimientoCmpInvoice.class);

                        TextCmpSeguridadAlimentariaInvoice textInvoice = new TextCmpSeguridadAlimentariaInvoice((XmlAlimentariaAbastecimientoCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());
                    }
                    //9
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_HOSPITAL_CLINICAS)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlHospitalClinicaEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlHospitalClinicaEltrInvoice.class);

                        TextEltrHospitalesClinicasInvoice textInvoice = new TextEltrHospitalesClinicasInvoice((XmlHospitalClinicaEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlHospitalClinicaCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlHospitalClinicaCmpInvoice.class);

                        TextCmpHospitalesClinicasInvoice textInvoice = new TextCmpHospitalesClinicasInvoice((XmlHospitalClinicaCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());
                    }
                    //10
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlTuristicoHospedajeEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlTuristicoHospedajeEltrInvoice.class);

                        TextEltrTuristicoHospedajeInvoice textInvoice = new TextEltrTuristicoHospedajeInvoice((XmlTuristicoHospedajeEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlTuristicoHospedajeCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlTuristicoHospedajeCmpInvoice.class);

                        TextCmpTuristicoHospedajeInvoice textInvoice = new TextCmpTuristicoHospedajeInvoice((XmlTuristicoHospedajeCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());
                    }
                    //11
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_FACTURAS_HOTELES)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlHotelEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlHotelEltrInvoice.class);

                        TextEltrHotelesInvoice textInvoice = new TextEltrHotelesInvoice((XmlHotelEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlHotelCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlHotelCmpInvoice.class);

                        TextCmpHotelesInvoice textInvoice = new TextCmpHotelesInvoice((XmlHotelCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());
                    }
                    //12
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlExportacionServicioEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlExportacionServicioEltrInvoice.class);

                        TextEltrExportacionServiciosInvoice textInvoice = new TextEltrExportacionServiciosInvoice((XmlExportacionServicioEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setCurrencyTypeRepository(currencyTypeRepository);
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlExportacionServicioCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlExportacionServicioCmpInvoice.class);

                        TextCmpExportacionServiciosInvoice textInvoice = new TextCmpExportacionServiciosInvoice((XmlExportacionServicioCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setCurrencyTypeRepository(currencyTypeRepository);
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    }
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_NOTA_CREDITO_DEBITO)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlNotaCreditoDebitoEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlNotaCreditoDebitoEltrInvoice.class);

                        TextEltrNotaDebitoInvoice textInvoice = new TextEltrNotaDebitoInvoice((XmlNotaCreditoDebitoEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setCurrencyTypeRepository(currencyTypeRepository);
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlNotaCreditoDebitoCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlNotaCreditoDebitoCmpInvoice.class);

                        TextCmpNotaDebitoInvoice textInvoice = new TextCmpNotaDebitoInvoice((XmlNotaCreditoDebitoCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setCurrencyTypeRepository(currencyTypeRepository);
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    }
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_PREVALORADA)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlPrevaloradaEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlPrevaloradaEltrInvoice.class);

                        TextEltrPrevaloradaInvoice textInvoice = new TextEltrPrevaloradaInvoice((XmlPrevaloradaEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setCurrencyTypeRepository(currencyTypeRepository);
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlPrevaloradaCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlPrevaloradaCmpInvoice.class);

                        TextCmpPrevaloradaInvoice textInvoice = new TextCmpPrevaloradaInvoice((XmlPrevaloradaCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setCurrencyTypeRepository(currencyTypeRepository);
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    }
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_NOTA_CONCILIACION)){
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlConciliacionEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlConciliacionEltrInvoice.class);

                        TextEltrNotaConciliacionInvoice textInvoice = new TextEltrNotaConciliacionInvoice((XmlConciliacionEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setCurrencyTypeRepository(currencyTypeRepository);
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlConciliacionCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlConciliacionCmpInvoice.class);

                        TextCmpNotaConciliacionInvoice textInvoice = new TextCmpNotaConciliacionInvoice((XmlConciliacionCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setCurrencyTypeRepository(currencyTypeRepository);
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    }

                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION)){
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlComercialExportacionEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlComercialExportacionEltrInvoice.class);

                        TextEltrComercialExportacionInvoice textInvoice = new TextEltrComercialExportacionInvoice((XmlComercialExportacionEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setCurrencyTypeRepository(currencyTypeRepository);
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {

                        baseInvoice = (XmlComercialExportacionCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlComercialExportacionCmpInvoice.class);

                        TextCmpComercialExportacionInvoice textInvoice = new TextCmpComercialExportacionInvoice((XmlComercialExportacionCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setCurrencyTypeRepository(currencyTypeRepository);
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    }
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_SEGUROS)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlSegurosEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlSegurosEltrInvoice.class);

                        TextEltrSegurosInvoice textInvoice = new TextEltrSegurosInvoice((XmlSegurosEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlSegurosCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlSegurosCmpInvoice.class);

                        TextCmpSegurosInvoice textInvoice = new TextCmpSegurosInvoice((XmlSegurosCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());
                    }
                    //5
                } else if (invoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_ZONA_FRANCA)) {
                    if (invoice.getModalitySiat() == ModalitySiatEnum.ELECTRONIC_BILLING) {
                        baseInvoice = (XmlZonaFrancaEltrInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlZonaFrancaEltrInvoice.class);

                        TextEltrZonaFrancaInvoice textInvoice = new TextEltrZonaFrancaInvoice((XmlZonaFrancaEltrInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());

                    } else if (invoice.getModalitySiat() == ModalitySiatEnum.COMPUTERIZED_BILLING) {
                        baseInvoice = (XmlZonaFrancaCmpInvoice) PdfInvoiceBuilder.buildInstance(data.get().getInvoiceJson(), XmlZonaFrancaCmpInvoice.class);

                        TextCmpZonaFrancaInvoice textInvoice = new TextCmpZonaFrancaInvoice((XmlZonaFrancaCmpInvoice) baseInvoice, paperType);
                        textInvoice.setActivityRepository(this.activityRepository);
                        textInvoice.setApprovedProductRepository(this.approvedProductRepository);
                        textInvoice.setCompanyId(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());
                        textInvoice.setPointOfSale(invoice.getCufd().getCuis().getPointSale().getName());
                        textInvoice.setSiatUrl(properties.getSiatUrl());
                        textInvoice.setModalidadLeyenda(getLeyendaModalidad(invoice));
                        textInvoice.setMeasurementUnitRepository(this.measurementUnitRepository);

                        response.setBody(textInvoice.generate());
                    }
                    //5
                }
            }
        } catch (PdfInvoiceCurrencyNotFoundException e) {
            log.error(String.format("Error generando reporte %s. No se encontro la moneda especificada ", cuf), e.getMessage());
            e.printStackTrace();
            throw new PdfInvoiceCurrencyNotFoundException(String.format("Error generando reporte %s. No se encontro la moneda especificada ", cuf));

        } catch (JsonProcessingException e) {
            log.error(String.format("Error generando reporte %s. No se encontro la moneda especificada ", cuf), e.getMessage());
            e.printStackTrace();
            throw new PdfInvoiceCurrencyNotFoundException(String.format("Error generando reporte %s. No se encontro la moneda especificada ", cuf));
        }

        response.setCode(ResponseCodes.SUCCESS);

        return response;
    }

}
