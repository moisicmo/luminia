package bo.com.luminia.sflbilling.msreport.siat.pdf;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.cmp.*;
import bo.gob.impuestos.sfe.invoice.xml.eltr.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PdfInvoiceBuilder {

    private PdfInvoiceBuilder() {

    }

    /**
     * @param json
     * @param object
     * @return
     * @throws JsonProcessingException
     */

    public static XmlBaseInvoice buildInstance(String json, Class<?> object) throws JsonProcessingException {
        // TODO se deben crear instancias para todas las facturas

        if (object == XmlCompraVentaEltrInvoice.class) {
            return createCompraVentaEltrFromJson(json);
        }

        if (object == XmlCompraVentaCmpInvoice.class) {
            return createCompraVentaCmpFromJson(json);
        }

        if (object == XmlAlquilerBienesInmueblesCmpInvoice.class) {
            return createAlquileresCmpFromJson(json);
        }

        if (object == XmlAlquilerBienesInmueblesEltrInvoice.class) {
            return createAlquileresEltrFromJson(json);
        }

        if (object == XmlEntidadesFinancierasCmpInvoice.class) {
            return createEntidadesFinancierasCmpFromJson(json);
        }

        if (object == XmlEntidadesFinancierasEltrInvoice.class) {
            return createEntidadesFinancierasEltrFromJson(json);
        }

        if (object == XmlTelecomunicacionesCmpInvoice.class) {
            return createTelecomunicacionesCmpFromJson(json);
        }

        if (object == XmlTelecomunicacionesEltrInvoice.class) {
            return createTelecomunicacionesEltrFromJson(json);
        }

        if (object == XmlServiciosBasicosCmpInvoice.class) {
            return createserviciosBasicosCmpFromJson(json);
        }

        if (object == XmlServiciosBasicosEltrInvoice.class) {
            return createServiciosBasicosEltrFromJson(json);
        }

        if (object == XmlSectorEducativoCmpInvoice.class) {
            return createSectorEducativoCmpFromJson(json);
        }

        if (object == XmlSectorEducativoEltrInvoice.class) {
            return createSectorEducativoEltrFromJson(json);
        }

        if (object == XmlHidrocarburosCmpInvoice.class) {
            return createComercializacionHidrocarburosCmpFromJson(json);
        }

        if (object == XmlHidrocarburosEltrInvoice.class) {
            return createComercializacionHidrocarburosEltrFromJson(json);
        }

        if (object == XmlAlimentariaAbastecimientoCmpInvoice.class) {
            return createSeguridadAlimentariaAbastecimientoCmpFromJson(json);
        }

        if (object == XmlAlimentariaAbastecimientoEltrInvoice.class) {
            return createSeguridadAlimentariaAbastecimientoEltrFromJson(json);
        }

        if (object == XmlHotelCmpInvoice.class) {
            return createHotelesCmpInvoiceFromJson(json);
        }

        if (object == XmlHotelEltrInvoice.class) {
            return createHotelesEltrFromJson(json);
        }

        if (object == XmlTuristicoHospedajeCmpInvoice.class) {
            return createTuristicoHospedajeCmpFromJson(json);
        }

        if (object == XmlTuristicoHospedajeEltrInvoice.class) {
            return createTuristicoHospedajeEltrFromJson(json);
        }

        if (object == XmlHospitalClinicaCmpInvoice.class) {
            return createHospitalClinicaCmpFromJson(json);
        }

        if (object == XmlHospitalClinicaEltrInvoice.class) {
            return createHospitalClinicaEltrFromJson(json);
        }

        if (object == XmlExportacionServicioCmpInvoice.class) {
            return createExportacionServiciosCmpFromJson(json);
        }

        if (object == XmlExportacionServicioEltrInvoice.class) {
            return createExportacionServiciosEltrFromJson(json);
        }

        if (object == XmlNotaCreditoDebitoCmpInvoice.class) {
            return createNotaCreditoDebitoCmpFromJson(json);
        }

        if (object == XmlNotaCreditoDebitoEltrInvoice.class) {
            return createNotaCreditoDebitoEltrFromJson(json);
        }

        if (object == XmlPrevaloradaCmpInvoice.class) {
            return createPrevaloradaCmpFromJson(json);
        }

        if (object == XmlPrevaloradaEltrInvoice.class) {
            return createPrevaloradaEltrFromJson(json);
        }

        if (object == XmlComercialExportacionCmpInvoice.class) {
            return createComerciaExportacionCmpFromJson(json);
        }

        if (object == XmlComercialExportacionEltrInvoice.class) {
            return createComerciaExportacionEltrFromJson(json);
        }

        if (object == XmlConciliacionEltrInvoice.class){
            return createConciliacionEltrFromJson(json);
        }

        if (object == XmlConciliacionCmpInvoice.class){
            return createConciliacionCmpFromJson(json);
        }
        if (object == XmlSegurosEltrInvoice.class) {
            return craeteSegurosEltrFromJson(json);
        }
        if (object == XmlSegurosCmpInvoice.class) {
            return craeteSegurosCmpFromJson(json);
        }
        if (object == XmlZonaFrancaEltrInvoice.class) {
            return craeteZonaFrancaEltrFromJson(json);
        }
        if (object == XmlZonaFrancaCmpInvoice.class) {
            return craeteZonaFrancaCmpFromJson(json);
        }

        return null;
    }

    private static XmlBaseInvoice craeteZonaFrancaCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlZonaFrancaCmpInvoice invoice = objectMapper.readValue(json, XmlZonaFrancaCmpInvoice.class);
        return invoice;
    }

    private static XmlBaseInvoice craeteZonaFrancaEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlZonaFrancaEltrInvoice invoice = objectMapper.readValue(json, XmlZonaFrancaEltrInvoice.class);
        return invoice;
    }

    private static XmlBaseInvoice craeteSegurosEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlSegurosEltrInvoice invoice = objectMapper.readValue(json, XmlSegurosEltrInvoice.class);
        return invoice;
    }

    private static XmlBaseInvoice craeteSegurosCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlSegurosCmpInvoice invoice = objectMapper.readValue(json, XmlSegurosCmpInvoice.class);
        return invoice;
    }

    private static XmlBaseInvoice createConciliacionCmpFromJson(String json)throws JsonProcessingException  {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlConciliacionCmpInvoice invoice = objectMapper.readValue(json, XmlConciliacionCmpInvoice.class);
        return invoice;
    }

    private static XmlBaseInvoice createConciliacionEltrFromJson(String json) throws JsonProcessingException  {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlConciliacionEltrInvoice invoice = objectMapper.readValue(json, XmlConciliacionEltrInvoice.class);
        return invoice;
    }

    private static XmlBaseInvoice createComerciaExportacionEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlComercialExportacionEltrInvoice invoice = objectMapper.readValue(json, XmlComercialExportacionEltrInvoice.class);
        return invoice;
    }

    private static XmlBaseInvoice createComerciaExportacionCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlComercialExportacionCmpInvoice invoice = objectMapper.readValue(json, XmlComercialExportacionCmpInvoice.class);
        return invoice;
    }

    private static XmlPrevaloradaEltrInvoice createPrevaloradaEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlPrevaloradaEltrInvoice invoice = objectMapper.readValue(json, XmlPrevaloradaEltrInvoice.class);
        return invoice;
    }

    private static XmlPrevaloradaCmpInvoice createPrevaloradaCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlPrevaloradaCmpInvoice invoice = objectMapper.readValue(json, XmlPrevaloradaCmpInvoice.class);
        return invoice;
    }

    private static XmlNotaCreditoDebitoCmpInvoice createNotaCreditoDebitoCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlNotaCreditoDebitoCmpInvoice invoice = objectMapper.readValue(json, XmlNotaCreditoDebitoCmpInvoice.class);
        return invoice;
    }

    private static XmlNotaCreditoDebitoEltrInvoice createNotaCreditoDebitoEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlNotaCreditoDebitoEltrInvoice invoice = objectMapper.readValue(json, XmlNotaCreditoDebitoEltrInvoice.class);
        return invoice;
    }

    private static XmlExportacionServicioCmpInvoice createExportacionServiciosCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlExportacionServicioCmpInvoice invoice = objectMapper.readValue(json, XmlExportacionServicioCmpInvoice.class);
        return invoice;
    }

    private static XmlExportacionServicioEltrInvoice createExportacionServiciosEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlExportacionServicioEltrInvoice invoice = objectMapper.readValue(json, XmlExportacionServicioEltrInvoice.class);
        return invoice;
    }

    private static XmlHospitalClinicaCmpInvoice createHospitalClinicaCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlHospitalClinicaCmpInvoice invoice = objectMapper.readValue(json, XmlHospitalClinicaCmpInvoice.class);
        return invoice;
    }

    private static XmlHospitalClinicaEltrInvoice createHospitalClinicaEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlHospitalClinicaEltrInvoice invoice = objectMapper.readValue(json, XmlHospitalClinicaEltrInvoice.class);
        return invoice;
    }

    private static XmlTuristicoHospedajeCmpInvoice createTuristicoHospedajeCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlTuristicoHospedajeCmpInvoice invoice = objectMapper.readValue(json, XmlTuristicoHospedajeCmpInvoice.class);
        return invoice;
    }

    private static XmlTuristicoHospedajeEltrInvoice createTuristicoHospedajeEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlTuristicoHospedajeEltrInvoice invoice = objectMapper.readValue(json, XmlTuristicoHospedajeEltrInvoice.class);
        return invoice;
    }

    private static XmlHotelCmpInvoice createHotelesCmpInvoiceFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlHotelCmpInvoice invoice = objectMapper.readValue(json, XmlHotelCmpInvoice.class);
        return invoice;
    }

    private static XmlHotelEltrInvoice createHotelesEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlHotelEltrInvoice invoice = objectMapper.readValue(json, XmlHotelEltrInvoice.class);
        return invoice;
    }

    private static XmlAlimentariaAbastecimientoCmpInvoice createSeguridadAlimentariaAbastecimientoCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlAlimentariaAbastecimientoCmpInvoice invoice = objectMapper.readValue(json, XmlAlimentariaAbastecimientoCmpInvoice.class);
        return invoice;
    }

    private static XmlAlimentariaAbastecimientoEltrInvoice createSeguridadAlimentariaAbastecimientoEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlAlimentariaAbastecimientoEltrInvoice invoice = objectMapper.readValue(json, XmlAlimentariaAbastecimientoEltrInvoice.class);
        return invoice;
    }

    private static XmlHidrocarburosCmpInvoice createComercializacionHidrocarburosCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlHidrocarburosCmpInvoice invoice = objectMapper.readValue(json, XmlHidrocarburosCmpInvoice.class);
        return invoice;
    }

    private static XmlHidrocarburosEltrInvoice createComercializacionHidrocarburosEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlHidrocarburosEltrInvoice invoice = objectMapper.readValue(json, XmlHidrocarburosEltrInvoice.class);
        return invoice;
    }

    private static XmlSectorEducativoCmpInvoice createSectorEducativoCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlSectorEducativoCmpInvoice invoice = objectMapper.readValue(json, XmlSectorEducativoCmpInvoice.class);
        return invoice;
    }

    private static XmlSectorEducativoEltrInvoice createSectorEducativoEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlSectorEducativoEltrInvoice invoice = objectMapper.readValue(json, XmlSectorEducativoEltrInvoice.class);
        return invoice;
    }

    private static XmlServiciosBasicosEltrInvoice createServiciosBasicosEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlServiciosBasicosEltrInvoice invoice = objectMapper.readValue(json, XmlServiciosBasicosEltrInvoice.class);
        return invoice;
    }

    private static XmlServiciosBasicosCmpInvoice createserviciosBasicosCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlServiciosBasicosCmpInvoice invoice = objectMapper.readValue(json, XmlServiciosBasicosCmpInvoice.class);
        return invoice;
    }

    private static XmlTelecomunicacionesEltrInvoice createTelecomunicacionesEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlTelecomunicacionesEltrInvoice invoice = objectMapper.readValue(json, XmlTelecomunicacionesEltrInvoice.class);
        return invoice;
    }

    private static XmlTelecomunicacionesCmpInvoice createTelecomunicacionesCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlTelecomunicacionesCmpInvoice invoice = objectMapper.readValue(json, XmlTelecomunicacionesCmpInvoice.class);
        return invoice;
    }

    private static XmlEntidadesFinancierasEltrInvoice createEntidadesFinancierasEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlEntidadesFinancierasEltrInvoice invoice = objectMapper.readValue(json, XmlEntidadesFinancierasEltrInvoice.class);
        return invoice;
    }

    private static XmlEntidadesFinancierasCmpInvoice createEntidadesFinancierasCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlEntidadesFinancierasCmpInvoice invoice = objectMapper.readValue(json, XmlEntidadesFinancierasCmpInvoice.class);
        return invoice;
    }

    private static XmlAlquilerBienesInmueblesCmpInvoice createAlquileresCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlAlquilerBienesInmueblesCmpInvoice invoice = objectMapper.readValue(json, XmlAlquilerBienesInmueblesCmpInvoice.class);
        return invoice;
    }

    private static XmlAlquilerBienesInmueblesEltrInvoice createAlquileresEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlAlquilerBienesInmueblesEltrInvoice invoice = objectMapper.readValue(json, XmlAlquilerBienesInmueblesEltrInvoice.class);
        return invoice;
    }

    // TODO crear metodo asi para todas las facturas
    private static XmlCompraVentaEltrInvoice createCompraVentaEltrFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlCompraVentaEltrInvoice invoice = objectMapper.readValue(json, XmlCompraVentaEltrInvoice.class);

        return invoice;
    }

    private static XmlCompraVentaCmpInvoice createCompraVentaCmpFromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        XmlCompraVentaCmpInvoice invoice = objectMapper.readValue(json, XmlCompraVentaCmpInvoice.class);
        return invoice;
    }


}
