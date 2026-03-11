package bo.com.luminia.sflbilling.msinvoice.certificacion;

import bo.com.luminia.sflbilling.msinvoice.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msinvoice.repository.CuisRepository;
import bo.com.luminia.sflbilling.msinvoice.service.sfe.issue.InvoiceIssueService;
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
public class TestSincronizacionCatalogoEltrIT {

    @Autowired
    private InvoiceIssueService invoiceIssueService;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CuisRepository cuisRepository;

    @Autowired
    private MockMvc mockMvc;

    private InvoiceIssueReq createInvoicePaso1() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(0);
        request.setDocumentSectorType(1);

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

    private InvoiceIssueReq createInvoicePaso2() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(1);
        request.setDocumentSectorType(1);

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

    private InvoiceIssueReq createInvoicePaso3() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(0);
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
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
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

    private InvoiceIssueReq createInvoicePaso4() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(1);
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
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
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

    private InvoiceIssueReq createInvoicePaso5() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(1);
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
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
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

    private InvoiceIssueReq createInvoicePaso6() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(0);
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
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("montoTotalMoneda", "50");
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
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

    private InvoiceIssueReq createInvoicePaso7() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(0);
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
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
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

    private InvoiceIssueReq createInvoicePaso8() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(1);
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
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
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

    private InvoiceIssueReq createInvoicePaso9() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(0);
        request.setDocumentSectorType(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS);

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
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
        header.put("usuario", "pperez");
        header.put("codigoPais", "1");
        header.put("placaVehiculo", "123RTF");
        header.put("tipoEnvase", "ENVASE 1");
        header.put("montoTotalSujetoIvaLey317", "35");

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

    private InvoiceIssueReq createInvoicePaso10() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(1);
        request.setDocumentSectorType(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS);

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
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
        header.put("usuario", "pperez");
        header.put("codigoPais", "1");
        header.put("placaVehiculo", "123RTF");
        header.put("tipoEnvase", "ENVASE 1");
        header.put("montoTotalSujetoIvaLey317", "35");


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

    private InvoiceIssueReq createInvoicePaso11() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(0);
        request.setDocumentSectorType(XmlSectorType.INVOICE_SERVICIOS_BASICOS);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "JPEREZ");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "60");
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("nombreEstudiante", "ESTUDIANTE 1");
        header.put("periodoFacturado", "ENERO 2021");
        header.put("montoTotalMoneda", "60");
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
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

    private InvoiceIssueReq createInvoicePaso12() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(1);
        request.setDocumentSectorType(XmlSectorType.INVOICE_SERVICIOS_BASICOS);

        HashMap<String, Object> header = new HashMap<>();
        header.put("numeroFactura", "1");
        header.put("nombreRazonSocial", "JUAN PEREZ");
        header.put("codigoTipoDocumentoIdentidad", "1");
        header.put("numeroDocumento", "1548971");
        header.put("complemento", "1F");
        header.put("codigoCliente", "JPEREZ");
        header.put("codigoMetodoPago", "1");
        header.put("numeroTarjeta", null);
        header.put("montoTotal", "60");
        header.put("montoTotalSujetoIva", "50");
        header.put("codigoMoneda", "1");
        header.put("tipoCambio", "1");
        header.put("nombreEstudiante", "ESTUDIANTE 1");
        header.put("periodoFacturado", "ENERO 2021");
        header.put("montoTotalMoneda", "60");
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
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

    private InvoiceIssueReq createInvoicePaso13() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(0);
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
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
        header.put("usuario", "pperez");
        header.put("montoTotalArrendamientoFinanciero", "0");

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

    private InvoiceIssueReq createInvoicePaso14() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(1);
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
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
        header.put("usuario", "pperez");
        header.put("montoTotalArrendamientoFinanciero", "0");

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

    private InvoiceIssueReq createInvoicePaso15() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(0);
        request.setDocumentSectorType(XmlSectorType.INVOICE_FACTURAS_HOTELES);

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
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
        header.put("usuario", "pperez");
        header.put("montoTotalArrendamientoFinanciero", "0");
        header.put("cantidadHuespedes", "3");
        header.put("cantidadHabitaciones", "2");
        header.put("cantidadMayores", "2");
        header.put("cantidadMenores", "1");
        header.put("fechaIngresoHospedaje", "2021-07-26T11:00:12.208");

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

    private InvoiceIssueReq createInvoicePaso16() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(1);
        request.setDocumentSectorType(XmlSectorType.INVOICE_FACTURAS_HOTELES);

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
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
        header.put("usuario", "pperez");
        header.put("montoTotalArrendamientoFinanciero", "0");
        header.put("cantidadHuespedes", "3");
        header.put("cantidadHabitaciones", "2");
        header.put("cantidadMayores", "2");
        header.put("cantidadMenores", "1");
        header.put("fechaIngresoHospedaje", "2021-07-26T11:00:12.208");

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

    private InvoiceIssueReq createInvoicePaso17() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(0);
        request.setDocumentSectorType(XmlSectorType.INVOICE_HOSPITAL_CLINICAS);

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
        header.put("leyenda", "Ley N 453, Tienes derecho a recibir informacion sobre las caracteristicas y contenidos de los servicios que utilices.");
        header.put("usuario", "pperez");
        header.put("modalidadServicio", " Post Operatorio");

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

    private InvoiceIssueReq createInvoicePaso18() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(1);
        request.setDocumentSectorType(XmlSectorType.INVOICE_HOSPITAL_CLINICAS);

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
        header.put("leyenda", "Ley N 453, Tienes derecho a recibir informacion sobre las caracteristicas y contenidos de los servicios que utilices.");
        header.put("usuario", "pperez");
        header.put("modalidadServicio", " Post Operatorio");

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

    private InvoiceIssueReq createInvoicePaso19() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(0);
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
        header.put("leyenda", "Ley N° 453, Tienes derecho a recibir información sobre las características y contenidos de los servicios que utilices.");
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

    private InvoiceIssueReq createInvoicePaso20() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(1);
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
        header.put("leyenda", "Ley N 453, Tienes derecho a recibir informacion sobre las caracteristicas y contenidos de los servicios que utilices.");
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

    private InvoiceIssueReq createInvoicePaso21() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(0);
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

    private InvoiceIssueReq createInvoicePaso22() {
        InvoiceIssueReq request = new InvoiceIssueReq();
        request.setCompanyId(1L);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(1);
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
        header.put("montoDescuentoCreditoDebito", "0");
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

    //@Test
    public void testPaso1() {

        InvoiceIssueReq request = createInvoicePaso1();
        int numberOfTests = 124;

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

    //@Test
    public void testPaso2() {

        InvoiceIssueReq request = createInvoicePaso2();
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

    //@Test
    public void testPaso3() {

        InvoiceIssueReq request = createInvoicePaso3();
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

    //@Test
    public void testPaso4() {

        InvoiceIssueReq request = createInvoicePaso4();
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
    public void testPaso5() {

        InvoiceIssueReq request = createInvoicePaso5();
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
    public void testPaso6() {

        InvoiceIssueReq request = createInvoicePaso6();
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
    public void testPaso7() {

        InvoiceIssueReq request = createInvoicePaso7();
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
    public void testPaso8() {

        InvoiceIssueReq request = createInvoicePaso8();
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
    public void testPaso9() {

        InvoiceIssueReq request = createInvoicePaso9();
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
    public void testPaso10() {

        InvoiceIssueReq request = createInvoicePaso10();
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

    //@Test
    public void testPaso11() {

        InvoiceIssueReq request = createInvoicePaso11();
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

    //@Test
    public void testPaso12() {

        InvoiceIssueReq request = createInvoicePaso12();
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

    //@Test
    public void testPaso13() {

        InvoiceIssueReq request = createInvoicePaso13();
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

    //@Test
    public void testPaso14() {

        InvoiceIssueReq request = createInvoicePaso14();
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

    //@Test
    public void testPaso15() {

        InvoiceIssueReq request = createInvoicePaso15();
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

    //@Test
    public void testPaso16() {

        InvoiceIssueReq request = createInvoicePaso16();
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

    //@Test
    public void testPaso17() {

        InvoiceIssueReq request = createInvoicePaso17();
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

    //@Test
    public void testPaso18() {

        InvoiceIssueReq request = createInvoicePaso18();
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

    //@Test
    public void testPaso19() {

        InvoiceIssueReq request = createInvoicePaso19();
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

    //@Test
    public void testPaso20() {

        InvoiceIssueReq request = createInvoicePaso20();
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
    public void testPaso21() {

        InvoiceIssueReq request = createInvoicePaso21();
        int numberOfTests = 1;

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

    //@Test
    public void testPaso22() {

        InvoiceIssueReq request = createInvoicePaso22();
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
