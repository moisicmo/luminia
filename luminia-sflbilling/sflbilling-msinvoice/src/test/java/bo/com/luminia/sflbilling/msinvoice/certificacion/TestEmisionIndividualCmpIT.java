package bo.com.luminia.sflbilling.msinvoice.certificacion;

import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceIssueReq;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import bo.gob.impuestos.sfe.invoice.xml.XmlSectorType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@SpringBootTest
public class TestEmisionIndividualCmpIT {

    @Autowired
    private MockMvc mockMvc;

    private final static Long companyId = 1001L;
    private final static Integer pointSaleSiat = 0;

    private InvoiceIssueReq createInvoiceCompraVenta() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_COMPRA_VENTA);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "JPEREZ");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "50");
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("montoGiftCard", "0");
        header.put("descuentoAdicional", "0");
        header.put("codigoExcepcion", "0");
        header.put("usuario", "pperez");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "10");
        d1.put("precioUnitario", "2.5");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "25");
        d1.put("numeroSerie", null);
        d1.put("numeroImei", null);

        HashMap<String, String> d2 = new HashMap<>();
        d2.put("codigoProducto", "DEV01");
        d2.put("cantidad", "10");
        d2.put("precioUnitario", "2.5");
        d2.put("montoDescuento", "0");
        d2.put("subTotal", "25");
        d2.put("numeroSerie", null);
        d2.put("numeroImei", null);

        detail.add(d1);
        detail.add(d2);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoiceAlquilerBienes() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "JPEREZ");
        header.put("periodoFacturado", "MES DE ENERO");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "50");
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("descuentoAdicional", "0");
        header.put("codigoExcepcion", "0");
        header.put("usuario", "pperez");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "10");
        d1.put("precioUnitario", "2.5");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "25");
        d1.put("numeroSerie", null);
        d1.put("numeroImei", null);

        HashMap<String, String> d2 = new HashMap<>();
        d2.put("codigoProducto", "DEV01");
        d2.put("cantidad", "10");
        d2.put("precioUnitario", "2.5");
        d2.put("montoDescuento", "0");
        d2.put("subTotal", "25");
        d2.put("numeroSerie", null);
        d2.put("numeroImei", null);

        detail.add(d1);
        detail.add(d2);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoiceComercialExportacion() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("direccionComprador", "VILLA FATIMA");
        header.put("codigoCliente", "Pmamani");
        header.put("incoterm", "CIF");
        header.put("incotermDetalle", "CIF-WEBEX");
        header.put("puertoDestino", "Arica");
        header.put("lugarDestino", "Chile");
        header.put("codigoPais", "100");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "163000");
        header.put("costosGastosNacionales", "{\"Gasto Transporte\":\"2000\",\"Gasto de Seguro\":\"1000\"}");
        header.put("totalGastosNacionalesFob", "153000");
        header.put("costosGastosInternacionales", "{\"Gasto Transporte\":\"7000\",\"Gasto de Seguro\":\"3000\"}");
        header.put("totalGastosInternacionales", "10000");
        header.put("montoDetalle", "150000.00");
        header.put("montoTotalSujetoIva", "0");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "163000");
        header.put("numeroDescripcionPaquetesBultos", "10 PAQUETES DE MEDIO QUINTAL");
        header.put("informacionAdicional", "INFO ADD");
        header.put("descuentoAdicional", "0");
        header.put("codigoExcepcion", "0");
        header.put("usuario", "pperez");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("codigoNandina", "0909610000");
        d1.put("cantidad", "1000");
        d1.put("precioUnitario", "150");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "150000");

        detail.add(d1);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoiceTurismoHospedaje() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "JPEREZ");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("montoGiftCard", "0");
        header.put("descuentoAdicional", "0");
        header.put("codigoExcepcion", "0");
        header.put("usuario", "pperez");

        header.put("razonSocialOperadorTurismo", "PMAMANI");
        header.put("cantidadHuespedes", "3");
        header.put("cantidadHabitaciones", "2");
        header.put("cantidadMayores", "2");
        header.put("cantidadMenores", "1");
        header.put("fechaIngresoHospedaje", "2021-07-26T11:00:12.208");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "10");
        d1.put("precioUnitario", "5");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "50");
        d1.put("codigoTipoHabitacion", "1");
        d1.put("detalleHuespedes", "HUESPED 1, HUESPED 2, HUESPED 3");

        detail.add(d1);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoiceAlimentosSeguridad() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "JPEREZ");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("montoGiftCard", "0");
        header.put("descuentoAdicional", "0");
        header.put("codigoExcepcion", "0");
        header.put("usuario", "pperez");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "10");
        d1.put("precioUnitario", "2.5");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "25");

        HashMap<String, String> d2 = new HashMap<>();
        d2.put("codigoProducto", "DEV01");
        d2.put("cantidad", "10");
        d2.put("precioUnitario", "2.5");
        d2.put("montoDescuento", "0");
        d2.put("subTotal", "25");

        detail.add(d1);
        detail.add(d2);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoiceSectorEducativo() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_SECTOR_EDUCATIVO);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "JPEREZ");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "50");
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("nombreEstudiante", "ESTUDIANTE 1");
        header.put("periodoFacturado", "ENERO 2021");
        header.put("montoTotalMoneda", "50");
        header.put("montoGiftCard", "0");
        header.put("descuentoAdicional", "0");
        header.put("codigoExcepcion", "0");
        header.put("usuario", "pperez");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "10");
        d1.put("precioUnitario", "2.5");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "25");

        HashMap<String, String> d2 = new HashMap<>();
        d2.put("codigoProducto", "DEV01");
        d2.put("cantidad", "10");
        d2.put("precioUnitario", "2.5");
        d2.put("montoDescuento", "0");
        d2.put("subTotal", "25");

        detail.add(d1);
        detail.add(d2);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoiceComercializacionHidrocarburos() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "Pmamani");
        header.put("codigoPais", "1");
        header.put("placaVehiculo", "123RTF");
        header.put("tipoEnvase", "ENVASE 1");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "50");
        header.put("montoTotalSujetoIvaLey317", "35");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("descuentoAdicional", "0");
        header.put("codigoExcepcion", "0");
        header.put("usuario", "pperez");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "10");
        d1.put("precioUnitario", "5");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "50");

        detail.add(d1);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoiceServiciosBasicos() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_SERVICIOS_BASICOS);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "Pmamani");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "60");
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "60");
        header.put("usuario", "pperez");
        header.put("mes", "FEBRERO");
        header.put("gestion", "2021");
        header.put("ciudad", "LA PAZ");
        header.put("zona", "CENTRO");
        header.put("numeroMedidor", "12255-2343");
        header.put("domicilioCliente", "AV. ANTOFAGASTA");
        header.put("consumoPeriodo", "10");
        header.put("beneficiarioLey1886", "556688745");
        header.put("montoDescuentoLey1886", "0");
        header.put("montoDescuentoTarifaDignidad", "0");
        header.put("tasaAseo", "5");
        header.put("tasaAlumbrado", "5");
        header.put("ajusteNoSujetoIva", "0");
        header.put("otrosPagosNoSujetoIva", "0");
        header.put("detalleAjusteNoSujetoIva", "0");
        header.put("ajusteSujetoIva", "0");
        header.put("detalleAjusteSujetoIva", "0");
        header.put("detalleOtrosPagosNoSujetoIva", "0");
        header.put("descuentoAdicional", "0");
        header.put("codigoExcepcion", "0");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "1");
        d1.put("precioUnitario", "50");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "50");

        detail.add(d1);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoiceEntidadesFinancieras() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "JPEREZ");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "50");
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("nombreEstudiante", "ESTUDIANTE 1");
        header.put("periodoFacturado", "ENERO 2021");
        header.put("montoTotalMoneda", "50");
        header.put("descuentoAdicional", "0");
        header.put("codigoExcepcion", "0");
        header.put("usuario", "pperez");
        header.put("montoTotalArrendamientoFinanciero", "0");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "10");
        d1.put("precioUnitario", "2.5");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "25");

        HashMap<String, String> d2 = new HashMap<>();
        d2.put("codigoProducto", "DEV01");
        d2.put("cantidad", "10");
        d2.put("precioUnitario", "2.5");
        d2.put("montoDescuento", "0");
        d2.put("subTotal", "25");

        detail.add(d1);
        detail.add(d2);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoiceHoteles() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_FACTURAS_HOTELES);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "Pmamani");
        header.put("cantidadHuespedes", "3");
        header.put("cantidadHabitaciones", "2");
        header.put("cantidadMayores", "2");
        header.put("cantidadMenores", "1");
        header.put("fechaIngresoHospedaje", "2021-07-26T11:00:12.208");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "50");
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("montoGiftCard", "0");
        header.put("descuentoAdicional", "0");
        header.put("codigoExcepcion", "0");
        header.put("usuario", "pperez");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "10");
        d1.put("precioUnitario", "5");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "50");
        d1.put("codigoTipoHabitacion", "1");
        d1.put("detalleHuespedes", "HUESPED 1, HUESPED 2, HUESPED 3");

        detail.add(d1);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoiceHospitalClinicas() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_HOSPITAL_CLINICAS);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "JPEREZ");
        header.put("modalidadServicio", " Post Operatorio");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "50");
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("montoGiftCard", "0");
        header.put("descuentoAdicional", "0");
        header.put("codigoExcepcion", "0");
        header.put("usuario", "pperez");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "10");
        d1.put("precioUnitario", "5");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "50");
        d1.put("especialidad", "TRAUMAS");
        d1.put("especialidadDetalle", "ESPECIALIDAD");
        d1.put("nroQuirofanoSalaOperaciones", "10");
        d1.put("especialidadMedico", "TRAUMATOLOGO");
        d1.put("nombreApellidoMedico", "PEDRO PICA");
        d1.put("nitDocumentoMedico", "1016867023");
        d1.put("nroMatriculaMedico", "MTR1");
        d1.put("nroFacturaMedico", "1");

        detail.add(d1);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoiceTelecomunicaciones() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_TELECOMUNICACIONES);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "JPEREZ");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "50");
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("montoGiftCard", "0");
        header.put("descuentoAdicional", "0");
        header.put("codigoExcepcion", "0");
        header.put("usuario", "pperez");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "10");
        d1.put("precioUnitario", "2.5");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "25");

        HashMap<String, String> d2 = new HashMap<>();
        d2.put("codigoProducto", "DEV01");
        d2.put("cantidad", "10");
        d2.put("precioUnitario", "2.5");
        d2.put("montoDescuento", "0");
        d2.put("subTotal", "25");

        detail.add(d1);
        detail.add(d2);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoicePrevalorada() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_PREVALORADA);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("codigoCliente", "JPEREZ");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "50");
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("usuario", "pperez");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "1");
        d1.put("precioUnitario", "50");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "50");

        detail.add(d1);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoiceNotaCreditoDebito() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_NOTA_CREDITO_DEBITO);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "JPEREZ");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "50");
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
        header.put("usuario", "pperez");
        header.put("numeroNotaCreditoDebito", "1");
        header.put("numeroAutorizacionCuf", "466282576963A1E1F658D53C7E66A3D6159EE686A8722F4B44388DC74");
        header.put("fechaEmisionFactura", "2021-07-08T23:29:17.895");
        header.put("montoTotalOriginal", "50");
        header.put("montoTotalDevuelto", "50");
        header.put("montoDescuentoCreditoDebito", "0.0");
        header.put("montoEfectivoCreditoDebito", "6.5");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "1");
        d1.put("precioUnitario", "50");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "50");
        d1.put("codigoDetalleTransaccion", "1");

        HashMap<String, String> d2 = new HashMap<>();
        d2.put("codigoProducto", "DEV01");
        d2.put("codigoProducto", "DEV01");
        d2.put("cantidad", "1");
        d2.put("precioUnitario", "50");
        d2.put("montoDescuento", "0");
        d2.put("subTotal", "50");
        d2.put("codigoDetalleTransaccion", "2");

        detail.add(d1);
        detail.add(d2);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoiceExportacionServicios() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("direccionComprador", "VILLA FATIMA");
        header.put("codigoCliente", "Pmamani");
        header.put("lugarDestino", "LA PAZ");
        header.put("codigoPais", "1");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "50");
        header.put("montoTotalSujetoIva", "0");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("informacionAdicional", null);
        header.put("descuentoAdicional", "0");
        header.put("codigoExcepcion", "0");
        header.put("usuario", "pperez");

        List<Map<String, String>> detail = new ArrayList<>();

        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "1");
        d1.put("precioUnitario", "50");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "50");

        detail.add(d1);

        request.setHeader(header);
        request.setDetail(detail);

        return request;
    }

    private InvoiceIssueReq createInvoiceNotaConciliacion() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(XmlSectorType.INVOICE_NOTA_CONCILIACION);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "Pmamani");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "50");
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("usuario", "pperez");
        header.put("numeroNotaConciliacion", "1");
        header.put("numeroAutorizacionCuf", "466282576963A5270BE563DDD5AC91F8E27C9E06A73114DBC82C8DC74");
        header.put("codigoControl", "15-dc-00-ea");
        header.put("fechaEmisionFactura", "2021-07-08T23:29:17.895");
        header.put("montoTotalOriginal", "50");
        header.put("montoTotalConciliado", "12.5");
        header.put("creditoFiscalIva", "0");
        header.put("debitoFiscalIva", "3.25");
        header.put("codigoExcepcion", null);

        List<Map<String, String>> detailOriginal = new ArrayList<>();
        HashMap<String, String> d1 = new HashMap<>();
        d1.put("codigoProducto", "DEV01");
        d1.put("cantidad", "1");
        d1.put("precioUnitario", "50");
        d1.put("montoDescuento", "0");
        d1.put("subTotal", "50");

        List<Map<String, String>> detailConciliation = new ArrayList<>();
        HashMap<String, String> d2 = new HashMap<>();
        d2.put("codigoProducto", "DEV01");
        d2.put("montoOriginal", "0");
        d2.put("montoFinal", "12.5");
        d2.put("montoConciliado", "12.5");

        detailOriginal.add(d1);
        detailConciliation.add(d2);

        request.setHeader(header);
        request.setDetailOriginal(detailOriginal);
        request.setDetailConciliation(detailConciliation);

        return request;
    }

    @Test
    public void testCompraVenta() {

        InvoiceIssueReq request = createInvoiceCompraVenta();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAlquilerBienes() {

        InvoiceIssueReq request = createInvoiceAlquilerBienes();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testComercialExportacion() {

        InvoiceIssueReq request = createInvoiceComercialExportacion();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTurismoHospedaje() {

        InvoiceIssueReq request = createInvoiceTurismoHospedaje();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAlimentosSeguridad() {

        InvoiceIssueReq request = createInvoiceAlimentosSeguridad();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSectorEducativo() {

        InvoiceIssueReq request = createInvoiceSectorEducativo();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testComercializacionHidrocarburos() {

        InvoiceIssueReq request = createInvoiceComercializacionHidrocarburos();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testServiciosBasicos() {

        InvoiceIssueReq request = createInvoiceServiciosBasicos();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEntidadesFinancieras() {

        InvoiceIssueReq request = createInvoiceEntidadesFinancieras();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHoteles() {

        InvoiceIssueReq request = createInvoiceHoteles();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHospitalClinicas() {

        InvoiceIssueReq request = createInvoiceHospitalClinicas();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTelecomunicaciones() {

        InvoiceIssueReq request = createInvoiceTelecomunicaciones();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPrevalorada() {

        InvoiceIssueReq request = createInvoicePrevalorada();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNotaCreditoDebito() {

        InvoiceIssueReq request = createInvoiceNotaCreditoDebito();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExportacionServicios() {

        InvoiceIssueReq request = createInvoiceExportacionServicios();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNotaConciliacion() {

        InvoiceIssueReq request = createInvoiceNotaConciliacion();
        int numberOfTests = 125;

        try {
            for (int i = 0; i < numberOfTests; i++) {
                mockMvc
                    .perform(post("/api/invoice/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                    .andExpect(status().isOk());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
