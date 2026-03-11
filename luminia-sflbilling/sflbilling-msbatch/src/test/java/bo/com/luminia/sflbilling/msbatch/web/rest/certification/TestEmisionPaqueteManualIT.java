package bo.com.luminia.sflbilling.msbatch.web.rest.certification;

import bo.com.luminia.sflbilling.msbatch.repository.*;
import bo.com.luminia.sflbilling.msbatch.web.rest.certification.request.InvoiceIssueManualReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.certification.request.InvoiceOfflineManualReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.InvoicePackReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.InvoicePackValidateReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.TestUtil;
import bo.com.luminia.sflbilling.msbatch.repository.*;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import bo.gob.impuestos.sfe.invoice.xml.XmlSectorType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@SpringBootTest
public class TestEmisionPaqueteManualIT {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private InvoiceWrapperEventRepository invoiceWrapperEventRepository;
    @Autowired
    private WrapperEventRepository wrapperEventRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private OfflineRepository offlineRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test() throws Exception {

        int numberTests = 1;
        int numberInvoices = 1000;
        long companyId = 1001;
        Integer branchOfficeSiat = 0;
        Integer pointSaleSiat = 1;
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTYzNjA3NDg4OX0.zQ7L-mlmRLoPXUPdXb4XT5QE7Lld9T3Ste8OwvEb66f0MFJ6oyM07fr_440QdN8LXLbKG6rb7FUffmZ_i132Mg";
        String companyHash = "9ce9f485-9b86-4e60-82b0-65ce7daceece";

        // Actualiza la empresa para envio offline (solo la primera vez).
//        Optional<Company> companyFromDb = companyRepository.findById(companyId);
//        if (companyFromDb.isPresent()) {
//            Company company = companyFromDb.get();
//            company.setPackageSend(false);
//            company.setEventSend(true);
//            companyRepository.save(company);
//        }

        HashMap<Integer, String> sectorDocumentTypeList = new HashMap<>();
//        sectorDocumentTypeList.put(XmlSectorType.INVOICE_COMPRA_VENTA, "1014BFB48055E");
//        sectorDocumentTypeList.put(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES, "1024BFB48058E");
//        sectorDocumentTypeList.put(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION, "2034BFB48052E");
//        sectorDocumentTypeList.put(XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE, "2064BFB48055E");
//        sectorDocumentTypeList.put(XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD, "2074BFB48053E");
//        sectorDocumentTypeList.put(XmlSectorType.INVOICE_SECTOR_EDUCATIVO, "1114BFB48050E");
//        sectorDocumentTypeList.put(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS, "1124BFB48052E");
//        sectorDocumentTypeList.put(XmlSectorType.INVOICE_SERVICIOS_BASICOS, "1134BFB48057E");
//        sectorDocumentTypeList.put(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS, "1154BFB48055E");
//        sectorDocumentTypeList.put(XmlSectorType.INVOICE_FACTURAS_HOTELES, "1164BFB48054E");
//        sectorDocumentTypeList.put(XmlSectorType.INVOICE_HOSPITAL_CLINICAS, "1174BFB48056E");
//        sectorDocumentTypeList.put(XmlSectorType.INVOICE_TELECOMUNICACIONES, "1224BFB48053E");
//        sectorDocumentTypeList.put(XmlSectorType.INVOICE_PREVALORADA, "1234BFB48058E");
        sectorDocumentTypeList.put(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS, "2283F63A18A5E");
        //sectorDocumentTypeList.put(XmlSectorType.INVOICE_BOLETO_AEREO,"");

        // Itera todos los tipos de factura.
        for (Map.Entry<Integer, String> sectorDocumentType : sectorDocumentTypeList.entrySet()) {

            // Itera la colección de tipos de eventos significativos.
            for (int significantEvent = 5; significantEvent <= 7; significantEvent++) {

                // Itera el numero de pruebas requeridas.
                for (int number = 1; number <= numberTests; number++) {

                    // Configura el sistema para envio de facturas manuales.
                    InvoiceOfflineManualReq invoiceOfflineManualReq = new InvoiceOfflineManualReq();
                    invoiceOfflineManualReq.setCompanyId(companyId);
                    invoiceOfflineManualReq.setCafc(sectorDocumentType.getValue());
                    invoiceOfflineManualReq.setSignificantEventSiatId(significantEvent);
                    invoiceOfflineManualReq.setDescription("CORTE DE INTERNET");

                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.set("Authorization", "Bearer " + token);
                    headers.set("X-Company-Hash", companyHash);
                    HttpEntity<InvoiceOfflineManualReq> request = new HttpEntity<>(invoiceOfflineManualReq, headers);
                    String result = restTemplate.postForObject("http://localhost:8082/api/invoice/offline/manual", request, String.class);

                    //Thread.sleep(5000);
                    // Emisión de facturas masivas.
                    InvoiceIssueManualReq requestInvoice = this.createInvoice(companyId, branchOfficeSiat, pointSaleSiat, sectorDocumentType.getKey(), sectorDocumentType.getValue());
                    for (int i = 1; i <= numberInvoices; i++) {
                        RestTemplate restTemplate2 = new RestTemplate();
                        HttpHeaders headers2 = new HttpHeaders();
                        headers2.setContentType(MediaType.APPLICATION_JSON);
                        headers2.set("Authorization", "Bearer " + token);
                        headers2.set("X-Company-Hash", companyHash);
                        HttpEntity<InvoiceIssueManualReq> request2 = new HttpEntity<>(requestInvoice, headers2);
                        String result2 = restTemplate2.postForObject("http://localhost:8082/api/invoice/issue/manual", request2, String.class);
                    }
                    //Thread.sleep(5000);

                    // Envío de paquete de facturas de facturas.
                    InvoicePackReq requestPack = new InvoicePackReq();
                    requestPack.setCompanyId(companyId);
                    mockMvc
                        .perform(post("/api/invoice/pack")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(TestUtil.convertObjectToJsonBytes(requestPack)))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(200));

                    //Thread.sleep(10000);

                    //invoiceWrapperEventRepository.deleteAll();
                    //wrapperEventRepository.deleteAll();
                    //eventRepository.deleteAll();

                    // Validación de envío de paquete de facturas.
                    InvoicePackValidateReq requestPackValidate = new InvoicePackValidateReq();
                    requestPackValidate.setCompanyId(companyId);
                    mockMvc
                        .perform(post("/api/invoice/pack/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(TestUtil.convertObjectToJsonBytes(requestPackValidate)))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(200));
                }
            }
        }
    }

    private InvoiceIssueManualReq createInvoice(Long companyId, Integer branchOfficeSiat, Integer pointSaleSiat, Integer sectorDocumentType, String cafc) {
        InvoiceIssueManualReq request = new InvoiceIssueManualReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(branchOfficeSiat);
        request.setPointSaleSiat(pointSaleSiat);
        request.setDocumentSectorType(sectorDocumentType);
        request.setCafc(cafc);

        if (sectorDocumentType.equals(XmlSectorType.INVOICE_COMPRA_VENTA)) {
            HashMap<String, String> header = new HashMap<>();
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
            header.put("cafc", cafc);

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
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES)) {
            HashMap<String, String> header = new HashMap<>();
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
            header.put("cafc", cafc);

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
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION)) {
            HashMap<String, String> header = new HashMap<>();
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
            header.put("cafc", cafc);

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
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE)) {
            HashMap<String, String> header = new HashMap<>();
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
            header.put("cafc", cafc);

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
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD)) {
            HashMap<String, String> header = new HashMap<>();
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
            header.put("cafc", cafc);

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
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_SECTOR_EDUCATIVO)) {
            HashMap<String, String> header = new HashMap<>();
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
            header.put("cafc", cafc);

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
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS)) {
            HashMap<String, String> header = new HashMap<>();
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
            header.put("cafc", cafc);

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
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_SERVICIOS_BASICOS)) {
            HashMap<String, String> header = new HashMap<>();
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
            header.put("cafc", cafc);

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
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS)) {
            HashMap<String, String> header = new HashMap<>();
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
            header.put("cafc", cafc);

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
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_FACTURAS_HOTELES)) {
            HashMap<String, String> header = new HashMap<>();
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
            header.put("cafc", cafc);

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
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_HOSPITAL_CLINICAS)) {
            HashMap<String, String> header = new HashMap<>();
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
            header.put("cafc", cafc);

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
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_TELECOMUNICACIONES)) {
            HashMap<String, String> header = new HashMap<>();
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
            header.put("cafc", cafc);

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
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_PREVALORADA)) {
            HashMap<String, String> header = new HashMap<>();
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
            header.put("cafc", cafc);

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
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS)) {
            HashMap<String, String> header = new HashMap<>();
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
            header.put("cafc", cafc);

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
        }
        return request;
    }
}
