package bo.com.luminia.sflbilling.msinvoice.service.sfe.issue;

import bo.com.luminia.sflbilling.msinvoice.repository.*;
import bo.com.luminia.sflbilling.domain.*;
import bo.com.luminia.sflbilling.domain.enumeration.InvoiceStatusEnum;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import bo.com.luminia.sflbilling.msinvoice.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msinvoice.repository.*;
import bo.com.luminia.sflbilling.msinvoice.service.constants.SiatBroadcastType;
import bo.com.luminia.sflbilling.msinvoice.service.constants.SiatInvoiceType;
import bo.com.luminia.sflbilling.msinvoice.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec.CufdNotFoundException;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec.CuisNotFoundException;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceCancellationAllParamsReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceCancellationReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceCancellationReversalReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.response.sfe.invoice.InvoiceIssueRes;
import bo.gob.impuestos.sfe.electronicinvoice.RespuestaRecepcion;
import bo.gob.impuestos.sfe.electronicinvoice.ServicioFacturacion;
import bo.gob.impuestos.sfe.electronicinvoice.SolicitudAnulacion;
import bo.gob.impuestos.sfe.invoice.xml.XmlSectorType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class InvoiceOperationService {
    private final SectorDocumentTypeRepository sectorDocumentTypeRepository;
    private final PointSaleRepository pointSaleRepository;
    private final BranchOfficeRepository branchOfficeRepository;
    private final CompanyRepository companyRepository;
    private final Environment environment;
    private final InvoiceSoapUtil invoiceSoapUtil;
    private final CuisRepository cuisRepository;
    private final CufdRepository cufdRepository;
    private final InvoiceRepository invoiceRepository;
    private final OfflineRepository offlineRepository;
    private final InvoiceRequestRepository invoiceRequestRepository;

    /**
     * Método que realiza la anulación de la factura.
     *
     * @param request        Solicitud.
     * @param currentInvoice Factura.
     * @return
     */
    public InvoiceIssueRes cancellation(InvoiceCancellationReq request, Invoice currentInvoice, Long recordId, StopWatch watch) {
        // 1. Verifica la comunicación con los servicios del SIAT.
        if (this.isSiatOffline()) {
            log.debug("1. Anulación de factura rechazada, sistema en modo offline : {}", request.getCuf());
            return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "No hay conectividad con el Siat");
        } else {
            log.debug("1. Anulación de factura en proceso: {}", request.getCuf());
        }

        watch.start("Recolección datos para anulación 1-4");

        // 2. Obtiene el CUIS activo.
        Optional<Cuis> cuisOptional = null;
        try {
            cuisOptional = this.obtainCuisActive(currentInvoice);
        } catch (CuisNotFoundException e) {
            throw new CuisNotFoundException();
            //return new InvoiceIssueRes(SiatResponseCodes.ERROR_CUIS_NOT_FOUND, "Codigo Cuis no encontrado o no ha sido generado");
        }
        Cuis cuisFromDb = cuisOptional.get();
        log.debug("2. Datos del código CUIS : {}", cuisFromDb);

        // 3. Obtiene el CUFD activo.
        Optional<Cufd> cufdOptional = null;
        try {
            cufdOptional = this.obtainCufdActive(currentInvoice);
        } catch (CufdNotFoundException e) {
            throw new CufdNotFoundException();
            //return new InvoiceIssueRes(SiatResponseCodes.ERROR_CUFD_NOT_FOUND, "Codigo Cufd no encontrado o no ha sido generado");
        }
        Cufd cufdFromDb = cufdOptional.get();
        log.debug("3. Datos del código CUFD : {}", cufdFromDb);

        // Verifica el tiempo para la anulación de la factura.
        ZonedDateTime invoiceDate = currentInvoice.getBroadcastDate();
        ZonedDateTime validateDate = null;
        if (currentInvoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION)) {
            String days = environment.getProperty(ApplicationProperties.CANCELLATION_TERM_DAYS_COMERICIAL_EXPORTACION);
            validateDate = invoiceDate.plusDays(Integer.parseInt(days));
        } else {
            String days = environment.getProperty(ApplicationProperties.CANCELLATION_TERM_DAYS);
            ZoneId zoneId = ZoneId.of("America/La_Paz");
            validateDate = invoiceDate.plusMonths(1);
            validateDate = ZonedDateTime.of(validateDate.getYear(), validateDate.getMonthValue(), Integer.parseInt(days), 23, 59, 59, 0, zoneId);
        }

        watch.stop();

        log.debug("Request cuf={}, invoiceDate={}, zone={}", request.getCuf(), invoiceDate, validateDate);
        if (validateDate.compareTo(ZonedDateTime.now()) <= 0) {
            return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "El plazo para la anulación de la factura a concluido");
        }

        // 4. Realiza la anulación de la factura en el SIAT.
        watch.start("Envio  anulación Siat 4");
        InvoiceIssueRes invoiceIssueRes = this.sendCancellationSiat(request, cuisFromDb, cufdFromDb, currentInvoice);
        watch.stop();
        if (invoiceIssueRes.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT)) {
            return invoiceIssueRes;
        }

        if (request.getEmail() != null && !request.getEmail().equals("")) {
            watch.start("Envío correo");
            this.sendInvoiceEmail(request.getCuf(), request.getEmail());
            watch.stop();
        }

        currentInvoice.setStatus(InvoiceStatusEnum.CANCELED);
        invoiceRepository.save(currentInvoice);
        log.debug("5. Factura anulada correctamente");

        return new InvoiceIssueRes(SiatResponseCodes.SUCCESS, "La factura ha sido anulada correctamente");
    }

    /**
     * Método que envia la factura para anulación al servicio del SIAT.
     *
     * @param request        Solicitud.
     * @param cuisFromDb     Código Único del Sistema.
     * @param cufdFromDb     Código Único diario.
     * @param currentInvoice Factura.
     * @return
     * @throws Exception
     */
    private InvoiceIssueRes sendCancellationSiat(InvoiceCancellationReq request, Cuis cuisFromDb, Cufd cufdFromDb, Invoice currentInvoice) {
        return sendCancellationSiatGeneric(
            currentInvoice.getCufd().getCuis().getPointSale().getPointSaleSiatId(),
            currentInvoice.getCufd().getCuis().getPointSale().getBranchOffice().getBranchOfficeSiatId(),
            currentInvoice.getSectorDocumentType().getSiatId(),
            currentInvoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getModalitySiat(),
            currentInvoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany(),
            request,
            cuisFromDb,
            cufdFromDb
        );
    }

    /**
     * Método que realiza la reversion de anulación de la factura.
     *
     * @param request        Solicitud.
     * @param currentInvoice Factura.
     * @return
     */
    public InvoiceIssueRes cancellationReversal(InvoiceCancellationReversalReq request, Invoice currentInvoice, Long recordId, StopWatch watch) {
        // 1. Verifica la comunicación con los servicios del SIAT.
        if (this.isSiatOffline()) {
            log.debug("1. Reversion de factura anulada, sistema en modo offline : {}", request.getCuf());
            return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "No hay conectividad con el Siat");
        } else {
            log.debug("1. Reversion de factura anulada en proceso: {}", request.getCuf());
        }

        watch.start("Recolección datos para reversion 1-4");

        // 2. Obtiene el CUIS activo.
        Optional<Cuis> cuisOptional = null;
        try {
            cuisOptional = this.obtainCuisActive(currentInvoice);
        } catch (CuisNotFoundException e) {
            throw new CuisNotFoundException();
        }
        Cuis cuisFromDb = cuisOptional.get();
        log.debug("2. Datos del código CUIS : {}", cuisFromDb);

        // 3. Obtiene el CUFD activo.
        Optional<Cufd> cufdOptional = null;
        try {
            cufdOptional = this.obtainCufdActive(currentInvoice);
        } catch (CufdNotFoundException e) {
            throw new CufdNotFoundException();
        }
        Cufd cufdFromDb = cufdOptional.get();
        log.debug("3. Datos del código CUFD : {}", cufdFromDb);

        // Verifica el tiempo para la anulación de la factura.
        ZonedDateTime invoiceDate = currentInvoice.getBroadcastDate();
        ZonedDateTime validateDate = null;
        if (currentInvoice.getSectorDocumentType().getSiatId().equals(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION)) {
            String days = environment.getProperty(ApplicationProperties.CANCELLATION_TERM_DAYS_COMERICIAL_EXPORTACION);
            validateDate = invoiceDate.plusDays(Integer.parseInt(days));
        } else {
            String days = environment.getProperty(ApplicationProperties.CANCELLATION_TERM_DAYS);
            ZoneId zoneId = ZoneId.of("America/La_Paz");
            validateDate = invoiceDate.plusMonths(1);
            validateDate = ZonedDateTime.of(validateDate.getYear(), validateDate.getMonthValue(), Integer.parseInt(days), 23, 59, 59, 0, zoneId);
        }

        watch.stop();

        log.debug("Request cuf={}, invoiceDate={}, zone={}", request.getCuf(), invoiceDate, validateDate);
        if (validateDate.compareTo(ZonedDateTime.now()) <= 0) {
            return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "El plazo para la reversion de anulación de la factura a concluido");
        }

        // 4. Realiza la anulación de la factura en el SIAT.
        watch.start("Envio  anulación Siat 4");
        InvoiceIssueRes invoiceIssueRes = this.sendCancellationReversalSiat(request, cuisFromDb, cufdFromDb, currentInvoice);
        watch.stop();
        if (invoiceIssueRes.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT)) {
            return invoiceIssueRes;
        }

        if (request.getEmail() != null && !request.getEmail().equals("")) {
            watch.start("Envío correo");
            this.sendInvoiceEmail(request.getCuf(), request.getEmail());
            watch.stop();
        }

        currentInvoice.setStatus(InvoiceStatusEnum.EMITTED);
        invoiceRepository.save(currentInvoice);
        log.debug("5. Factura revertida correctamente");

        return new InvoiceIssueRes(SiatResponseCodes.SUCCESS, "La factura ha sido revertida correctamente");
    }

    /**
     * Método que envia la factura para reversion de anulación al servicio del SIAT.
     *
     * @param request        Solicitud.
     * @param cuisFromDb     Código Único del Sistema.
     * @param cufdFromDb     Código Único diario.
     * @param currentInvoice Factura.
     * @return
     * @throws Exception
     */
    private InvoiceIssueRes sendCancellationReversalSiat(InvoiceCancellationReversalReq request, Cuis cuisFromDb, Cufd cufdFromDb, Invoice currentInvoice) {
        return sendCancellationReversalSiatGeneric(
            currentInvoice.getCufd().getCuis().getPointSale().getPointSaleSiatId(),
            currentInvoice.getCufd().getCuis().getPointSale().getBranchOffice().getBranchOfficeSiatId(),
            currentInvoice.getSectorDocumentType().getSiatId(),
            currentInvoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getModalitySiat(),
            currentInvoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany(),
            request,
            cuisFromDb,
            cufdFromDb
        );
    }

    /**
     * Método que verifica la conexión.
     *
     * @return
     */
    /*private int checkConnectivity(String token) {
        ServicioFacturacionSincronizacion serviceConectividad = invoiceSoapUtil.getSincronizacion(token);
        bo.gob.impuestos.sfe.synchronization.RespuestaComunicacion response = serviceConectividad.verificarComunicacion();

        if (response.getMensajesList().isEmpty())
            return 0; //no hay conectividad

        for (MensajeServicio ms : response.getMensajesList()) {
            return ms.getCodigo().intValue();
        }

        return 0;
    }*/

    /**
     * Método que obtiene el código CUIS vigente.
     *
     * @param currentInvoice
     * @return
     * @throws CuisNotFoundException
     */
    protected Optional<Cuis> obtainCuisActive(Invoice currentInvoice) throws CuisNotFoundException {
        Optional<Cuis> cuisOptional = cuisRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(
            currentInvoice.getCufd().getCuis().getPointSale().getPointSaleSiatId(),
            currentInvoice.getCufd().getCuis().getPointSale().getBranchOffice().getBranchOfficeSiatId(),
            currentInvoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());

        if (!cuisOptional.isPresent()) {
            throw new CuisNotFoundException();
        }
        return cuisOptional;
    }

    /**
     * Método que obtiene el código CUFD vigente.
     *
     * @param currentInvoice
     * @return
     * @throws CufdNotFoundException
     */
    protected Optional<Cufd> obtainCufdActive(Invoice currentInvoice) throws CufdNotFoundException {
        Optional<Cufd> cufdOptional = cufdRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(
            currentInvoice.getCufd().getCuis().getPointSale().getPointSaleSiatId(),
            currentInvoice.getCufd().getCuis().getPointSale().getBranchOffice().getBranchOfficeSiatId(),
            currentInvoice.getCufd().getCuis().getPointSale().getBranchOffice().getCompany().getId());

        if (!cufdOptional.isPresent()) {
            throw new CufdNotFoundException();
        }
        return cufdOptional;
    }

    private void sendInvoiceEmail(String cuf, String email) {
        new Thread(() -> {
            String emailUrl = environment.getProperty(ApplicationProperties.NOTIFICATION_ENDPOINT_EMAIL);
            String clientId = environment.getProperty(ApplicationProperties.NOTIFICATION_CLIENT_ID);
            String apiKey = environment.getProperty(ApplicationProperties.NOTIFICATION_API_KEY);
            String htmlContent = "<p>Estimado(a) contribuyente:</p>" +
                "<p>Se le informa que se ha anulado el documento fiscal con Código Único de Factura (CUF): “" + cuf + "”.</p>" +
                "<p>Saludos cordiales.</p>";
            String subject = "Anulación de documento fiscal";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Client-ID", clientId);
            headers.set("X-API-Key", apiKey);

            JSONObject content = new JSONObject();
            content.put("type", "direct");
            content.put("to", email);
            content.put("subject", subject);
            content.put("html", htmlContent);

            HttpEntity<String> requestEmail = new HttpEntity<String>(content.toString(), headers);
            ResponseEntity<String> resultEmail = restTemplate.postForEntity(emailUrl, requestEmail, String.class);
            log.debug("Response notificador: {}", resultEmail);

            if (resultEmail.getStatusCode().is2xxSuccessful()) {
                log.info("Envio exitoso al correo electrónico: {}", email);
            } else {
                log.warn("Error al enviar el correo. Status: {}, body: {}",
                    resultEmail.getStatusCode(), resultEmail.getBody());
            }
        }).start();
    }

    /**
     * Método que verifica si el sistema se encuentra EN LINEA.
     *
     * @return
     */
    private boolean isSiatOffline() {
        Optional<Offline> searchOffline = offlineRepository.findAllNative().stream().findFirst();

        if (searchOffline.isPresent()) {
            Offline updateOffline = searchOffline.get();
            return updateOffline.getActive();
        }
        return false;
    }

    private InvoiceIssueRes sendCancellationSiatGeneric(int pointSaleSiatId, int branchOfficeSiatId, Integer sectorDocumentType, ModalitySiatEnum modality, Company company, InvoiceCancellationReq request, Cuis cuisFromDb, Cufd cufdFromDb) {
        StopWatch cron = new StopWatch();

        // Identifica el tipo de factura para anulación.
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_COMPRA_VENTA)) {
            log.debug("Anulación compra venta");
            bo.gob.impuestos.sfe.buysellinvoice.SolicitudAnulacion invoiceRequest = new bo.gob.impuestos.sfe.buysellinvoice.SolicitudAnulacion();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleSiatId);
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(branchOfficeSiatId);
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_ONLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoMotivo(request.getCancellationReasonSiat());
            invoiceRequest.setCuf(request.getCuf());

            bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion service = invoiceSoapUtil.getCompraVentaService(company.getToken());
            bo.gob.impuestos.sfe.buysellinvoice.RespuestaRecepcion response;

            cron.start();
            try {
                response = service.anulacionFactura(invoiceRequest);
            } catch (Exception e) {
                cron.stop();
                log.debug("ONLY_CANCEL|{}|Error al anular|{}", cron.getTotalTimeMillis(), e.getMessage());
                return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, e.getMessage());
            }
            cron.stop();
            log.debug("ONLY_CANCEL|{}", cron.getTotalTimeMillis());

            if (!response.isTransaccion()) {
                return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            log.debug("4. Resultado anulación de la factura: {}", response);
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_ZONA_FRANCA) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_SEGUROS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_SECTOR_EDUCATIVO) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_FACTURAS_HOTELES) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_PREVALORADA) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_HOSPITAL_CLINICAS) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS)) {
            log.debug("Anulación flujo 2");
            if (modality.equals(ModalitySiatEnum.ELECTRONIC_BILLING)) {
                SolicitudAnulacion invoiceRequest = new SolicitudAnulacion();
                invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
                invoiceRequest.setCodigoPuntoVenta(pointSaleSiatId);
                invoiceRequest.setCodigoSistema(company.getSystemCode());
                invoiceRequest.setCodigoSucursal(branchOfficeSiatId);
                invoiceRequest.setNit(company.getNit());
                invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
                invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_ONLINE);
                invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
                invoiceRequest.setCufd(cufdFromDb.getCufd());
                invoiceRequest.setCuis(cuisFromDb.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
                invoiceRequest.setCodigoMotivo(request.getCancellationReasonSiat());
                invoiceRequest.setCuf(request.getCuf());

                ServicioFacturacion service = invoiceSoapUtil.getElectronicaInvoice(company.getToken());
                RespuestaRecepcion response;

                cron.start();
                try {
                    response = service.anulacionFactura(invoiceRequest);
                } catch (Exception e) {
                    cron.stop();
                    log.debug("ONLY_CANCEL|{}|Error al anular|{}", cron.getTotalTimeMillis(), e.getMessage());
                    return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, e.getMessage());
                }
                cron.stop();
                log.debug("ONLY_CANCEL|{}", cron.getTotalTimeMillis());

                if (!response.isTransaccion()) {
                    return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
                }
                log.debug("4. Resultado anulación de la factura: {}", response);
            } else {
                bo.gob.impuestos.sfe.computerizedinvoice.SolicitudAnulacion invoiceRequest = new bo.gob.impuestos.sfe.computerizedinvoice.SolicitudAnulacion();
                invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
                invoiceRequest.setCodigoPuntoVenta(pointSaleSiatId);
                invoiceRequest.setCodigoSistema(company.getSystemCode());
                invoiceRequest.setCodigoSucursal(branchOfficeSiatId);
                invoiceRequest.setNit(company.getNit());
                invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
                invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_ONLINE);
                invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
                invoiceRequest.setCufd(cufdFromDb.getCufd());
                invoiceRequest.setCuis(cuisFromDb.getCuis());
                invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
                invoiceRequest.setCodigoMotivo(request.getCancellationReasonSiat());
                invoiceRequest.setCuf(request.getCuf());

                bo.gob.impuestos.sfe.computerizedinvoice.ServicioFacturacion service = invoiceSoapUtil.getComputarizadaInvoice(company.getToken());
                bo.gob.impuestos.sfe.computerizedinvoice.RespuestaRecepcion response;

                cron.start();
                try {
                    response = service.anulacionFactura(invoiceRequest);
                } catch (Exception e) {
                    cron.stop();
                    log.debug("ONLY_CANCEL|{}|Error al anular|{}", cron.getTotalTimeMillis(), e.getMessage());
                    return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, e.getMessage());
                }
                cron.stop();
                log.debug("ONLY_CANCEL|{}", cron.getTotalTimeMillis());


                if (!response.isTransaccion()) {
                    return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
                }
                log.debug("4. Resultado anulación de la factura: {}", response);
            }
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_SERVICIOS_BASICOS)) {
            log.debug("Anulación Servicios básicos");

            bo.gob.impuestos.sfe.basicserviceinvoice.SolicitudAnulacion invoiceRequest = new bo.gob.impuestos.sfe.basicserviceinvoice.SolicitudAnulacion();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleSiatId);
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(branchOfficeSiatId);
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_ONLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoMotivo(request.getCancellationReasonSiat());
            invoiceRequest.setCuf(request.getCuf());

            bo.gob.impuestos.sfe.basicserviceinvoice.ServicioFacturacion service = invoiceSoapUtil.getServiciosBasicos(company.getToken());
            bo.gob.impuestos.sfe.basicserviceinvoice.RespuestaRecepcion response;

            cron.start();
            try {
                response = service.anulacionFactura(invoiceRequest);
            } catch (Exception e) {
                cron.stop();
                log.debug("ONLY_CANCEL|{}|Error al anular|{}", cron.getTotalTimeMillis(), e.getMessage());
                return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, e.getMessage());
            }
            cron.stop();
            log.debug("ONLY_CANCEL|{}", cron.getTotalTimeMillis());


            if (!response.isTransaccion()) {
                return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            log.debug("4. Resultado anulación de la factura: {}", response);
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS)) {
            log.debug("Anulación entidades financieras");

            bo.gob.impuestos.sfe.financialinvoice.SolicitudAnulacion invoiceRequest = new bo.gob.impuestos.sfe.financialinvoice.SolicitudAnulacion();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleSiatId);
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(branchOfficeSiatId);
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_ONLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoMotivo(request.getCancellationReasonSiat());
            invoiceRequest.setCuf(request.getCuf());

            bo.gob.impuestos.sfe.financialinvoice.ServicioFacturacion service = invoiceSoapUtil.getEntidadesFinancieras(company.getToken());
            bo.gob.impuestos.sfe.financialinvoice.RespuestaRecepcion response;

            cron.start();
            try {
                response = service.anulacionFactura(invoiceRequest);
            } catch (Exception e) {
                cron.stop();
                log.debug("ONLY_CANCEL|{}|Error al anular|{}", cron.getTotalTimeMillis(), e.getMessage());
                return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, e.getMessage());
            }
            cron.stop();
            log.debug("ONLY_CANCEL|{}", cron.getTotalTimeMillis());

            if (!response.isTransaccion()) {
                return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            log.debug("4. Resultado anulación de la factura: {}", response);
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_TELECOMUNICACIONES)) {
            log.debug("Anulación telecomunicaciones");

            bo.gob.impuestos.sfe.telecommunicationinvoice.SolicitudAnulacion invoiceRequest = new bo.gob.impuestos.sfe.telecommunicationinvoice.SolicitudAnulacion();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleSiatId);
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(branchOfficeSiatId);
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_ONLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoMotivo(request.getCancellationReasonSiat());
            invoiceRequest.setCuf(request.getCuf());

            bo.gob.impuestos.sfe.telecommunicationinvoice.ServicioFacturacion service = invoiceSoapUtil.getTelecomunicacionesInvoice(company.getToken());
            bo.gob.impuestos.sfe.telecommunicationinvoice.RespuestaRecepcion response;

            cron.start();
            try {
                response = service.anulacionFactura(invoiceRequest);
            } catch (Exception e) {
                cron.stop();
                log.debug("ONLY_CANCEL|{}|Error al anular|{}", cron.getTotalTimeMillis(), e.getMessage());
                return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, e.getMessage());
            }
            cron.stop();
            log.debug("ONLY_CANCEL|{}", cron.getTotalTimeMillis());

            if (!response.isTransaccion()) {
                return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            log.debug("4. Resultado anulación de la factura: {}", response);
        }
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_NOTA_CREDITO_DEBITO) ||
            sectorDocumentType.equals(XmlSectorType.INVOICE_NOTA_CONCILIACION)) {
            log.debug("Anulación flujo 3");

            bo.gob.impuestos.sfe.creditdebitnoteinvoice.SolicitudAnulacion invoiceRequest = new bo.gob.impuestos.sfe.creditdebitnoteinvoice.SolicitudAnulacion();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleSiatId);
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(branchOfficeSiatId);
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_ONLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCodigoMotivo(request.getCancellationReasonSiat());
            invoiceRequest.setCuf(request.getCuf());

            bo.gob.impuestos.sfe.creditdebitnoteinvoice.ServicioFacturacion service = invoiceSoapUtil.getCreditoDebitoService(company.getToken());
            bo.gob.impuestos.sfe.creditdebitnoteinvoice.RespuestaRecepcion response;

            cron.start();
            try {
                response = service.anulacionDocumentoAjuste(invoiceRequest);
            } catch (Exception e) {
                cron.stop();
                log.debug("ONLY_CANCEL|{}|Error al anular|{}", cron.getTotalTimeMillis(), e.getMessage());
                return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, e.getMessage());
            }
            cron.stop();
            log.debug("ONLY_CANCEL|{}", cron.getTotalTimeMillis());

            if (!response.isTransaccion()) {
                return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            log.debug("4. Resultado anulación de la factura: {}", response);
        }

        return new InvoiceIssueRes(SiatResponseCodes.SUCCESS, "");
    }

    public InvoiceIssueRes cancellationsAllParams(InvoiceCancellationAllParamsReq request,
                                                  InvoiceRequest record,
                                                  Optional<Invoice> optionalInvoice,
                                                  StopWatch watch) {

        // 1. Verifica la comunicación con los servicios del SIAT.
        if (this.isSiatOffline()) {
            log.debug("1. Anulación de factura rechazada, sistema en modo offline : {}", request.getCuf());
            return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "No hay conectividad con el Siat");
        } else {
            log.debug("1. Anulación de factura en proceso: {}", request.getCuf());
        }

        watch.start("Proceso de anulación todos los parámetros 1-4");

        Company company = companyRepository.getById(request.getCompanyId());
        InvoiceCancellationReq basicRequest = new InvoiceCancellationReq();
        basicRequest.setEmail(request.getEmail());
        basicRequest.setCuf(request.getCuf());
        basicRequest.setCancellationReasonSiat(request.getCancellationReasonSiat());

        // 2. Obtiene el CUIS activo.
        Optional<Cuis> cuisOptional = cuisRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(
            request.getPointSaleSiat(),
            request.getBranchOfficeSiat(),
            company.getId());
        if (!cuisOptional.isPresent()) {
            return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "No se pudo recuperar CUIS");
        }

        Cuis cuisFromDb = cuisOptional.get();
        log.debug("2. Datos del código CUIS : {}", cuisFromDb);

        // 3. Obtiene el CUFD activo.
        Optional<Cufd> cufdOptional = null;
        cufdOptional = cufdRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(
            request.getPointSaleSiat(),
            request.getBranchOfficeSiat(),
            company.getId());

        if (!cufdOptional.isPresent()) {
            return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, "No se pudo recuperar CUFD");
        }

        Cufd cufdFromDb = cufdOptional.get();
        log.debug("3. Datos del código CUFD : {}", cufdFromDb);

        InvoiceIssueRes invoiceIssueRes = sendCancellationSiatGeneric(
            request.getPointSaleSiat(),
            request.getBranchOfficeSiat(),
            request.getSectorDocumentTypeSiat(),
            company.getModalitySiat(),
            company,
            basicRequest,
            cuisFromDb,
            cufdFromDb
        );

        if (!Strings.isBlank(invoiceIssueRes.getMessage()) && invoiceIssueRes.getMessage().contains("LA FACTURA O NOTA DE CREDITO-DEBITO YA SE ENCUENTRA ANULADA")) {
            log.debug("Factura revertida todos los parámetros: cuf={} registrada como ya revertida en SIAT",
                request.getCuf());
            if (optionalInvoice.isPresent()) {
                Invoice invoice = optionalInvoice.get();
                invoice.setStatus(InvoiceStatusEnum.CANCELED);
                invoiceRepository.save(invoice);
            }
            return new InvoiceIssueRes(SiatResponseCodes.SUCCESS, "factura previamente anulada");
        }

        if (!invoiceIssueRes.getCode().equals(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT)) {
            log.debug("Factura revertida por todos los parámetros: cuf={} con pointSaleSiat={} branchOfficeSiatId={}",
                request.getCuf(),
                request.getPointSaleSiat(),
                request.getBranchOfficeSiat());

            if (optionalInvoice.isPresent()) {
                Invoice invoice = optionalInvoice.get();
                invoice.setStatus(InvoiceStatusEnum.CANCELED);
                invoiceRepository.save(invoice);
            }

            if (request.getEmail() != null && !request.getEmail().equals("")) {
                this.sendInvoiceEmail(request.getCuf(), request.getEmail());
            }

            log.debug("5. Factura anulada correctamente");
            return new InvoiceIssueRes(SiatResponseCodes.SUCCESS, "La factura ha sido anulada correctamente");
        } else {
            log.error("Intento revertir FALLIDO por {} cuf={} con pointSaleSiat={} branchOfficeSiatId={} sectorDoctypeSiat={}",
                invoiceIssueRes.getMessage(),
                request.getCuf(),
                request.getPointSaleSiat(),
                request.getBranchOfficeSiat(),
                request.getSectorDocumentTypeSiat());
        }


        return new InvoiceIssueRes(SiatResponseCodes.ERROR, "No se pudo revertir la factura por fuerza bruta.");
    }

    private InvoiceIssueRes sendCancellationReversalSiatGeneric(int pointSaleSiatId, int branchOfficeSiatId, Integer sectorDocumentType, ModalitySiatEnum modality, Company company, InvoiceCancellationReversalReq request, Cuis cuisFromDb, Cufd cufdFromDb) {
        StopWatch cron = new StopWatch();

        // Identifica el tipo de factura para reversion de anulación.
        if (sectorDocumentType.equals(XmlSectorType.INVOICE_COMPRA_VENTA)) {
            log.debug("Reversion de anulación compra venta");
            bo.gob.impuestos.sfe.buysellinvoice.SolicitudReversionAnulacion invoiceRequest = new bo.gob.impuestos.sfe.buysellinvoice.SolicitudReversionAnulacion();
            invoiceRequest.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            invoiceRequest.setCodigoPuntoVenta(pointSaleSiatId);
            invoiceRequest.setCodigoSistema(company.getSystemCode());
            invoiceRequest.setCodigoSucursal(branchOfficeSiatId);
            invoiceRequest.setNit(company.getNit());
            invoiceRequest.setCodigoDocumentoSector(sectorDocumentType);
            invoiceRequest.setCodigoEmision(SiatBroadcastType.BROADCAST_TYPE_ONLINE);
            invoiceRequest.setCodigoModalidad(company.getModalitySiat().getKey());
            invoiceRequest.setCufd(cufdFromDb.getCufd());
            invoiceRequest.setCuis(cuisFromDb.getCuis());
            invoiceRequest.setTipoFacturaDocumento(SiatInvoiceType.map.get(sectorDocumentType));
            invoiceRequest.setCuf(request.getCuf());

            bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion service = invoiceSoapUtil.getCompraVentaService(company.getToken());
            bo.gob.impuestos.sfe.buysellinvoice.RespuestaRecepcion response;

            cron.start();
            try {
                response = service.reversionAnulacionFactura(invoiceRequest);
            } catch (Exception e) {
                cron.stop();
                log.debug("ONLY_CANCEL|{}|Error al anular|{}", cron.getTotalTimeMillis(), e.getMessage());
                return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, e.getMessage());
            }
            cron.stop();
            log.debug("ONLY_CANCEL|{}", cron.getTotalTimeMillis());

            if (!response.isTransaccion()) {
                return new InvoiceIssueRes(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion());
            }
            log.debug("4. Resultado reversion de anulación de la factura: {}", response);
        }

        return new InvoiceIssueRes(SiatResponseCodes.SUCCESS, "");
    }
}
