package bo.com.luminia.sflbilling.msinvoice.siat.invoice.xml;

import bo.com.luminia.sflbilling.domain.InvoiceLegend;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import bo.com.luminia.sflbilling.msinvoice.repository.InvoiceLegendRepository;
import bo.com.luminia.sflbilling.msinvoice.service.dto.siat.XmlRecoveredDto;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec.XmlFailedConversionException;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceIssueReq;
import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlSectorType;
import bo.gob.impuestos.sfe.invoice.xml.cmp.*;
import bo.gob.impuestos.sfe.invoice.xml.eltr.*;
import bo.gob.impuestos.sfe.invoice.xml.header.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

public class XmlInvoiceAdapter {

    /**
     * Convierte al tipo de dato s de la factura entrante.
     *
     * @param request
     * @return
     */
    public static XmlBaseInvoice convert(InvoiceIssueReq request, ModalitySiatEnum modality, final InvoiceLegendRepository legendRepository)
        throws XmlFailedConversionException {
        XmlBaseInvoice response = null;

        String legend = "";
        // TODO la tabla document_sector no tiene todos los type y no funcionaria enviando en DocumentSector
        List<InvoiceLegend> legends = legendRepository.findAllByCompanyIdAndActivityCodeAndActiveIsTrue(request.getCompanyId().intValue(),
            request.getDocumentSectorType().intValue());
        if (!legends.isEmpty()) {
            Random r = new Random();
            int random = r.nextInt(legends.size());
            legend = legends.get(random).getDescription();
        } else {
            legends = legendRepository.findAllByCompanyIdAndActiveIsTrue(request.getCompanyId().intValue());
            if (!legends.isEmpty()) {
                Random r = new Random();
                int random = r.nextInt(legends.size());
                legend = legends.get(random).getDescription();
            }
        }

        try {
            if (request.getDocumentSectorType() == XmlSectorType.INVOICE_COMPRA_VENTA) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlCompraVentaCmpInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlCompraVentaEltrInvoice();
                    ((XmlCompraVentaEltrInvoice) response).convert(request.getHeader(), request.getDetail());
                }
                ((XmlCompraVentaHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlAlquilerBienesInmueblesCmpInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlAlquilerBienesInmueblesEltrInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                }
                ((XmlAlquilerBienesInmueblesHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_ZONA_FRANCA) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlZonaFrancaCmpInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlZonaFrancaEltrInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                }
                ((XmlZonaFrancaHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlAlimentariaAbastecimientoCmpInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlAlimentariaAbastecimientoEltrInvoice();
                    ((XmlAlimentariaAbastecimientoEltrInvoice) response).convert(request.getHeader(), request.getDetail());
                }
                ((XmlAlimentariaAbastecimientoHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlEntidadesFinancierasCmpInvoice();
                    ((XmlEntidadesFinancierasCmpInvoice) response).convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlEntidadesFinancierasEltrInvoice();
                    ((XmlEntidadesFinancierasEltrInvoice) response).convert(request.getHeader(), request.getDetail());
                }
                ((XmlEntidadesFinancierasHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_EXPORTACION_SERVICIOS) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlExportacionServicioCmpInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlExportacionServicioEltrInvoice();
                    ((XmlExportacionServicioEltrInvoice) response).convert(request.getHeader(), request.getDetail());
                }
                ((XmlExportacionServiciosHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlHidrocarburosCmpInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlHidrocarburosEltrInvoice();
                    ((XmlHidrocarburosEltrInvoice) response).convert(request.getHeader(), request.getDetail());
                }
                ((XmlHidrocarburosHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_HOSPITAL_CLINICAS) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlHospitalClinicaCmpInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlHospitalClinicaEltrInvoice();
                    ((XmlHospitalClinicaEltrInvoice) response).convert(request.getHeader(), request.getDetail());
                }
                ((XmlHospitalClinicaHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_FACTURAS_HOTELES) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlHotelCmpInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlHotelEltrInvoice();
                    ((XmlHotelEltrInvoice) response).convert(request.getHeader(), request.getDetail());
                }
                ((XmlHotelesHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_NOTA_CREDITO_DEBITO) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlNotaCreditoDebitoCmpInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlNotaCreditoDebitoEltrInvoice();
                    ((XmlNotaCreditoDebitoEltrInvoice) response).convert(request.getHeader(), request.getDetail());
                }
                ((XmlNotaCreditoDebitoHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_SECTOR_EDUCATIVO) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlSectorEducativoCmpInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlSectorEducativoEltrInvoice();
                    ((XmlSectorEducativoEltrInvoice) response).convert(request.getHeader(), request.getDetail());
                }
                ((XmlSectorEducativoHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_SERVICIOS_BASICOS) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlServiciosBasicosCmpInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlServiciosBasicosEltrInvoice();
                    ((XmlServiciosBasicosEltrInvoice) response).convert(request.getHeader(), request.getDetail());
                }
                ((XmlServiciosBasicosHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_TELECOMUNICACIONES) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlTelecomunicacionesCmpInvoice();
                    ((XmlTelecomunicacionesCmpInvoice) response).convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlTelecomunicacionesEltrInvoice();
                    ((XmlTelecomunicacionesEltrInvoice) response).convert(request.getHeader(), request.getDetail());
                }
                ((XmlTelecomunicacionesHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlTuristicoHospedajeCmpInvoice();
                    ((XmlTuristicoHospedajeCmpInvoice) response).convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlTuristicoHospedajeEltrInvoice();
                    ((XmlTuristicoHospedajeEltrInvoice) response).convert(request.getHeader(), request.getDetail());
                }
                ((XmlTuristicoHospedajeHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_COMERCIAL_EXPORTACION) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlComercialExportacionCmpInvoice();
                    ((XmlComercialExportacionCmpInvoice) response).convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlComercialExportacionEltrInvoice();
                    ((XmlComercialExportacionEltrInvoice) response).convert(request.getHeader(), request.getDetail());
                }
                ((XmlComercialExportacionHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_PREVALORADA) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlPrevaloradaCmpInvoice();
                    ((XmlPrevaloradaCmpInvoice) response).convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlPrevaloradaEltrInvoice();
                    ((XmlPrevaloradaEltrInvoice) response).convert(request.getHeader(), request.getDetail());
                }
                ((XmlPrevaloradaHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_NOTA_CONCILIACION) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlConciliacionCmpInvoice();
                    ((XmlConciliacionCmpInvoice) response).convert(request.getHeader(), request.getDetailOriginal(), request.getDetailConciliation());
                } else {
                    response = new XmlConciliacionEltrInvoice();
                    ((XmlConciliacionEltrInvoice) response).convert(request.getHeader(), request.getDetailOriginal(), request.getDetailConciliation());
                }
                ((XmlConciliacionHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_BOLETO_AEREO) {
                throw new XmlFailedConversionException(String.format("Emisión de la factura solo para: Modalidad Computarizada y Masiva"));
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_SEGUROS) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlSegurosCmpInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlSegurosEltrInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                }
                ((XmlSegurosHeader) response.getCabecera()).setLeyenda(legend);
            } else if (request.getDocumentSectorType() == XmlSectorType.INVOICE_TASA_CERO_VENTA_LIBROS_TRANSPORTE_INTERNACIONAL) {
                if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                    response = new XmlTasaCeroCmpInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                } else {
                    response = new XmlTasaCeroEltrInvoice();
                    response.convert(request.getHeader(), request.getDetail());
                }
                ((XmlTasaCeroHeader) response.getCabecera()).setLeyenda(legend);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new XmlFailedConversionException(String.format("No se pudo generar el contenido XML %s", request));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new XmlFailedConversionException(String.format("No se pudo generar el contenido XML %s", request));
        }

        return response;
    }


    /**
     * En base a un json, recupera el xml que previamente se ha formado.
     *
     * @param json
     * @param modality
     * @param sectorType
     * @return
     * @throws JsonProcessingException
     */
    public static XmlRecoveredDto deduceAndChangeXml(
           String json,
           ModalitySiatEnum modality,
           Integer sectorType,
           boolean forceException,
           Integer changeDocumentType
    ) throws JsonProcessingException {
        XmlRecoveredDto result = new XmlRecoveredDto();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getTimeZone("America/La_Paz"));

        if (sectorType == XmlSectorType.INVOICE_COMPRA_VENTA) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlCompraVentaCmpInvoice invoice = (XmlCompraVentaCmpInvoice) mapper.readValue(json, XmlCompraVentaCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlCompraVentaEltrInvoice invoice = (XmlCompraVentaEltrInvoice) mapper.readValue(json, XmlCompraVentaEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlAlquilerBienesInmueblesCmpInvoice invoice = (XmlAlquilerBienesInmueblesCmpInvoice) mapper.readValue(json, XmlAlquilerBienesInmueblesCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlAlquilerBienesInmueblesEltrInvoice invoice = (XmlAlquilerBienesInmueblesEltrInvoice) mapper.readValue(json, XmlAlquilerBienesInmueblesEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_ZONA_FRANCA) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlZonaFrancaCmpInvoice invoice = (XmlZonaFrancaCmpInvoice) mapper.readValue(json, XmlZonaFrancaCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlZonaFrancaEltrInvoice invoice = (XmlZonaFrancaEltrInvoice) mapper.readValue(json, XmlZonaFrancaEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlAlimentariaAbastecimientoCmpInvoice invoice = (XmlAlimentariaAbastecimientoCmpInvoice) mapper.readValue(json, XmlAlimentariaAbastecimientoCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlAlimentariaAbastecimientoEltrInvoice invoice = (XmlAlimentariaAbastecimientoEltrInvoice) mapper.readValue(json, XmlAlimentariaAbastecimientoEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlEntidadesFinancierasCmpInvoice invoice = (XmlEntidadesFinancierasCmpInvoice) mapper.readValue(json, XmlEntidadesFinancierasCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlEntidadesFinancierasEltrInvoice invoice = (XmlEntidadesFinancierasEltrInvoice) mapper.readValue(json, XmlEntidadesFinancierasEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_EXPORTACION_SERVICIOS) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlExportacionServicioCmpInvoice invoice = (XmlExportacionServicioCmpInvoice) mapper.readValue(json, XmlExportacionServicioCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlExportacionServicioEltrInvoice invoice = (XmlExportacionServicioEltrInvoice) mapper.readValue(json, XmlExportacionServicioEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlHidrocarburosCmpInvoice invoice = (XmlHidrocarburosCmpInvoice) mapper.readValue(json, XmlHidrocarburosCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlHidrocarburosEltrInvoice invoice = (XmlHidrocarburosEltrInvoice) mapper.readValue(json, XmlHidrocarburosEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_HOSPITAL_CLINICAS) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlHospitalClinicaCmpInvoice invoice = (XmlHospitalClinicaCmpInvoice) mapper.readValue(json, XmlHospitalClinicaCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlHospitalClinicaEltrInvoice invoice = (XmlHospitalClinicaEltrInvoice) mapper.readValue(json, XmlHospitalClinicaEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_FACTURAS_HOTELES) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlHotelCmpInvoice invoice = (XmlHotelCmpInvoice) mapper.readValue(json, XmlHotelCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlHotelEltrInvoice invoice = (XmlHotelEltrInvoice) mapper.readValue(json, XmlHotelEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_NOTA_CREDITO_DEBITO) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlNotaCreditoDebitoCmpInvoice invoice = (XmlNotaCreditoDebitoCmpInvoice) mapper.readValue(json, XmlNotaCreditoDebitoCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlNotaCreditoDebitoEltrInvoice invoice = (XmlNotaCreditoDebitoEltrInvoice) mapper.readValue(json, XmlNotaCreditoDebitoEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_SECTOR_EDUCATIVO) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlSectorEducativoCmpInvoice invoice = (XmlSectorEducativoCmpInvoice) mapper.readValue(json, XmlSectorEducativoCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlSectorEducativoEltrInvoice invoice = (XmlSectorEducativoEltrInvoice) mapper.readValue(json, XmlSectorEducativoEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_SERVICIOS_BASICOS) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlServiciosBasicosCmpInvoice invoice = (XmlServiciosBasicosCmpInvoice) mapper.readValue(json, XmlServiciosBasicosCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlServiciosBasicosEltrInvoice invoice = (XmlServiciosBasicosEltrInvoice) mapper.readValue(json, XmlServiciosBasicosEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_TELECOMUNICACIONES) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlTelecomunicacionesCmpInvoice invoice = (XmlTelecomunicacionesCmpInvoice) mapper.readValue(json, XmlTelecomunicacionesCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlTelecomunicacionesEltrInvoice invoice = (XmlTelecomunicacionesEltrInvoice) mapper.readValue(json, XmlTelecomunicacionesEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlTuristicoHospedajeCmpInvoice invoice = (XmlTuristicoHospedajeCmpInvoice) mapper.readValue(json, XmlTuristicoHospedajeCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlTuristicoHospedajeEltrInvoice invoice = (XmlTuristicoHospedajeEltrInvoice) mapper.readValue(json, XmlTuristicoHospedajeEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_COMERCIAL_EXPORTACION) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlComercialExportacionCmpInvoice invoice = (XmlComercialExportacionCmpInvoice) mapper.readValue(json, XmlComercialExportacionCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlComercialExportacionEltrInvoice invoice = (XmlComercialExportacionEltrInvoice) mapper.readValue(json, XmlComercialExportacionEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_PREVALORADA) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlPrevaloradaCmpInvoice invoice = (XmlPrevaloradaCmpInvoice) mapper.readValue(json, XmlPrevaloradaCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlPrevaloradaEltrInvoice invoice = (XmlPrevaloradaEltrInvoice) mapper.readValue(json, XmlPrevaloradaEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_NOTA_CONCILIACION) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlConciliacionCmpInvoice invoice = (XmlConciliacionCmpInvoice) mapper.readValue(json, XmlConciliacionCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlConciliacionEltrInvoice invoice = (XmlConciliacionEltrInvoice) mapper.readValue(json, XmlConciliacionEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_BOLETO_AEREO) {
            throw new XmlFailedConversionException(String.format("Emisión de la factura solo para: Modalidad Computarizada y Masiva"));
        } else if (sectorType == XmlSectorType.INVOICE_SEGUROS) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlSegurosCmpInvoice invoice = (XmlSegurosCmpInvoice) mapper.readValue(json, XmlSegurosCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlSegurosEltrInvoice invoice = (XmlSegurosEltrInvoice) mapper.readValue(json, XmlSegurosEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        } else if (sectorType == XmlSectorType.INVOICE_TASA_CERO_VENTA_LIBROS_TRANSPORTE_INTERNACIONAL) {
            if (modality.getKey() == ModalitySiatEnum.COMPUTERIZED_BILLING.getKey()) {
                XmlTasaCeroCmpInvoice invoice = (XmlTasaCeroCmpInvoice) mapper.readValue(json, XmlTasaCeroCmpInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            } else {
                XmlTasaCeroEltrInvoice invoice = (XmlTasaCeroEltrInvoice) mapper.readValue(json, XmlTasaCeroEltrInvoice.class);
                result.setXml(invoice);
                result.setBarcode(invoice.getCabecera().getBarcode());
                result.setBusinessCode(invoice.getCabecera().getBusinessCode());
                result.setDocumentSectorType(invoice.getCabecera().getCodigoDocumentoSector());
                if (forceException)
                    invoice.getCabecera().setCodigoExcepcion(1);
                if (changeDocumentType > 0)
                    invoice.getCabecera().setCodigoTipoDocumentoIdentidad(changeDocumentType);
            }
        }

        return result;
    }
}
