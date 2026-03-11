package bo.com.luminia.sflbilling.msbatch.siat.service;

import bo.com.luminia.sflbilling.msbatch.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msbatch.repository.*;
import bo.com.luminia.sflbilling.msbatch.service.NotificationService;
import bo.com.luminia.sflbilling.msbatch.service.SflIntegrationService;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatInvoiceType;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.siat.SiatBuySellInvoiceStatusVerificationRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.siat.SiatMessage;
import bo.com.luminia.sflbilling.domain.*;
import bo.com.luminia.sflbilling.domain.enumeration.InvoiceSiatStatusEnum;
import bo.com.luminia.sflbilling.domain.enumeration.InvoiceStatusEnum;
import bo.com.luminia.sflbilling.domain.enumeration.PendingToFixActionEnum;
import bo.com.luminia.sflbilling.domain.enumeration.PendingToFixStatusEnum;
import bo.com.luminia.sflbilling.msbatch.repository.*;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.NotFoundAlertException;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.SoapUtil;
import bo.gob.impuestos.sfe.buysellinvoice.RespuestaRecepcion;
import bo.gob.impuestos.sfe.buysellinvoice.ServicioFacturacion;
import bo.gob.impuestos.sfe.buysellinvoice.SolicitudVerificacionEstado;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SiatBuySellService {
    private final PointSaleRepository pointSaleRepository;

    private final SoapUtil soapUtil;

    private final Environment environment;

    private final InvoiceRepository invoiceRepository;

    private final CuisRepository cuisRepository;

    private final CufdRepository cufdRepository;

    private final OfflineRepository offlineRepository;

    private final CheckInvoiceRepository checkInvoiceRepository;

    private final InvoiceWrapperEventRepository invoiceWrapperEventRepository;

    private final EventRepository eventRepository;

    private final AutomaticInvoiceFixRepository automaticInvoiceFixRepository;

    private final PendingToFixRepository pendingToFixRepository;

    private final InvoiceRequestRepository invoiceRequestRepository;

    private final NotificationService notificationService;

    private final InvoiceTypeRepository invoiceTypeRepository;

    private final SectorDocumentTypeRepository sectorDocumentTypeRepository;

    private final SflIntegrationService sflIntegrationService;


    /**
     * Verificación contra el SIAT
     *
     * @param company
     * @param invoice
     * @param cuis
     * @param cufd
     * @return
     */
    public RespuestaRecepcion invoiceStatusVerification(
        Company company,
        Invoice invoice,
        Cuis cuis,
        Cufd cufd
    ) {
        ServicioFacturacion servicioFacturacion = soapUtil.getBuySellService(environment.getProperty(ApplicationProperties.OFFLINE_APIKEY));

        SolicitudVerificacionEstado request = new SolicitudVerificacionEstado();
        request.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
        request.setCodigoDocumentoSector(invoice.getSectorDocumentType().getSiatId());
        request.setCodigoEmision(1); // this field accepts only 1, ONLINE = 1
        request.setCodigoModalidad(invoice.getModalitySiat().getKey());
        request.setCodigoPuntoVenta(invoice.getCufd().getCuis().getPointSale().getPointSaleSiatId());
        request.setCodigoSistema(company.getSystemCode());
        request.setCodigoSucursal(invoice.getCufd().getCuis().getPointSale().getBranchOffice().getBranchOfficeSiatId());
        request.setCufd(cufd.getCufd());
        request.setCuis(cuis.getCuis());
        request.setNit(company.getNit());
        request.setTipoFacturaDocumento(invoice.getInvoiceType().getSiatId());
        request.setCuf(invoice.getCuf());
        return servicioFacturacion.verificacionEstadoFactura(request);
    }

    /**
     * Revisar el estado de una factura bajo demanda.
     * Valida que el usuario pueda
     *
     * @param cuf
     * @return
     */
    public SiatBuySellInvoiceStatusVerificationRes checkInvoiceStatus(String cuf, User user) {
        //Si estamos offline, no podremos consultar con el siat
        if (isSiatOffline()) {
            return new SiatBuySellInvoiceStatusVerificationRes(null, true);
        }

        //Buscamos la factura y sus datos de la compañía
        Invoice invoice = invoiceRepository.findByCuf(cuf).orElseThrow(() -> new NotFoundAlertException("Factura no encontrada", "Factura no encontrada: cuf=" + cuf));
        log.debug("Invoice found with id: {}", invoice.getId());

        PointSale pointSale = invoice.getCufd().getCuis().getPointSale();
        BranchOffice branchOffice = pointSale.getBranchOffice();
        Company company = branchOffice.getCompany();

        //Objetivo: Solo el usuario admin de Luminia o el usuario admin de la propia compañía pueden ver el estado de la factura
        //Admin de otra compañía no deberá poder
        if (user != null && !user.getCompany().getId().equals(company.getId())) {
            log.warn("Factura cuf '{}' pertenece a otra empresa", cuf);
            throw new NotFoundAlertException("Usuario no pertenece a empresa de la factura", "Usuario no pertenece a la empresa de la factura");
        }

        Cuis cuis = cuisRepository.findTop1ByActiveTrueAndPointSaleIdOrderByStartDateDesc(pointSale.getId()).orElseThrow(() -> new NotFoundAlertException("Cuis no encontrado", "Cuis no encontrado"));
        Cufd cufd = cufdRepository.findTop1ByActiveTrueAndCuisIdOrderByStartDateDesc(cuis.getId()).orElseThrow(() -> new NotFoundAlertException("Cufd no encontrado", "Cufd no encontrado"));

        RespuestaRecepcion response = null;
        try {
            response = invoiceStatusVerification(company, invoice, cuis, cufd);
        } catch (Exception e) {
            log.error("Error al buscar estado de la factura: {}", e.getMessage());
        }

        return new SiatBuySellInvoiceStatusVerificationRes(response, false);
    }

    /**
     * Indica si el flag de fuera de línea está levantado o no.
     *
     * @return True si estamos fuera de línea
     */
    private boolean isSiatOffline() {
        Optional<Offline> searchOffline = offlineRepository.findAllNative().stream().findFirst();
        if (searchOffline.isPresent()) {
            Offline updateOffline = searchOffline.get();
            return updateOffline.getActive();
        }
        return false;
    }

    /**
     * Proceso que intenta verificar las facturas contra el SIAT
     */
    public synchronized void process() {
        log.debug("Entrando a proceso de verificar factura");
        if (isSiatOffline()) {
            log.debug("   Sistema fuera de línea. Saliendo.");
        }

        int quantityToCheck = Integer.parseInt(environment.getProperty(ApplicationProperties.BATCH_REVALIDATE_INVOICES_QUANTITY));
        CheckInvoice checkInvoice = getCurrentCheckInvoice();
        long maxInvoice = invoiceRepository.getMaxId();

        //Primera versión: Verificación sequencial
        for (int i = 0; i < quantityToCheck; i++) {

            if (isSiatOffline()) {
                log.debug("   Sistema fuera de línea en proceso de verificación. Saliendo.");
                break;
            }

            //Pivote de la próxima factura a buscar
            long nextInvoiceId = checkInvoice.getPosition() + 1;
            if (nextInvoiceId > maxInvoice)
                break;

            Optional<Invoice> invoice = this.invoiceRepository.findById(nextInvoiceId);

            boolean success = true;
            if (invoice.isPresent()) {
                success = checkInvoiceSiat(invoice.get());
            } else {
                log.info("Factura con id={} no existe, saltando", nextInvoiceId);
            }

            //Actualizo de una vez mi objeto
            if (success) {
                checkInvoice.setPosition(nextInvoiceId);
                checkInvoiceRepository.saveAndFlush(checkInvoice);
            }
        }
    }

    /**
     * Verifica el estado de una factura contra el SIAT
     *
     * @param invoice
     * @return
     */
    private boolean checkInvoiceSiat(Invoice invoice) throws NotFoundAlertException {
        //Nota: Las facturas REVERSED son marcadas manualmente en Luminia, estas deben ser omitidas de validaciones
        if (invoice.getStatus().equals(InvoiceStatusEnum.REVERSED)) {
            log.debug("Factura id={} está en reversed, ignorando", invoice.getId());
            return true;
        }

        //Caso: Cuando las facturas son online, si voy a buscarla en impuestos ese mismo momento puede que no la tenga
        //registrada. Adiciono un delta para recién tener el tiempo
        long secondsFromEmitted = ChronoUnit.SECONDS.between(invoice.getBroadcastDate(), ZonedDateTime.now());
        long secondsToCheck = Long.parseLong(environment.getProperty(ApplicationProperties.BATCH_REVALIDATE_INVOICES_SECONDS_FROM_EMITTED));
        if (secondsFromEmitted < secondsToCheck)
            return false;

        PointSale pointSale = invoice.getCufd().getCuis().getPointSale();
        BranchOffice branchOffice = pointSale.getBranchOffice();
        Company company = branchOffice.getCompany();

        Cuis cuis = cuisRepository.findTop1ByActiveTrueAndPointSaleIdOrderByStartDateDesc(pointSale.getId()).orElseThrow(() -> new NotFoundAlertException("Cuis no encontrado", "Cuis no encontrado"));
        Cufd cufd = cufdRepository.findTop1ByActiveTrueAndCuisIdOrderByStartDateDesc(cuis.getId()).orElseThrow(() -> new NotFoundAlertException("Cufd no encontrado", "Cufd no encontrado"));
        Optional<Event> event = findEvent(invoice);

        //Busco estado en el siat
        RespuestaRecepcion response = null;
        try {
            response = invoiceStatusVerification(company, invoice, cuis, cufd);
        } catch (Exception e) {
            log.error("Error al buscar estado de la factura: {}", e.getMessage());
        }

        if (response != null) {
            //Obtengo el estado reportado con impuestos y la descrićión
            //Existe un estado RECHAZADA, en el que hay que interpretar los mensajes de impuestos
            String siatStatus = response.getCodigoDescripcion();
            List<SiatMessage> messages = response.getMensajesList()
                .stream()
                .map(SiatMessage::new)
                .collect(Collectors.toList());

            //Mensaje del Siat
            String siatMessage = messages.isEmpty() ? "" : messages.get(0).getDescription();

            //Existe algunos servicios que no están implementados por el siat, por lo que intentamos buscar el
            //estado consultando la página web del SIAT utilizando la url del QR
            if (Strings.isBlank(siatStatus) || "null".equals(siatStatus)) {
                siatStatus = searchStatusInWeb(company, invoice);
                siatMessage = "QR";
                if (Strings.isBlank(siatStatus)) {
                    log.debug("Estado de factura no encontrado para invoiceid={}", invoice.getId());
                    throw new NotFoundAlertException(
                        ErrorKeys.ERR_RECORD_NOT_FOUND,
                        "Estado de factura no encontrado contra impuestos, id=" + invoice.getId());
                }
            }

            log.debug("factura bd: id={}, status={} - siat={} obs={}", invoice.getId(), invoice.getStatus(), siatStatus, siatMessage);

            //caso especial: Algunas facturas se quedan en pending pero ya fueron enviadas a impuestos. Revisar.
            if (invoice.getStatus().equals(InvoiceStatusEnum.PENDING)) {
                return checkAndVerivfyPending(invoice, siatStatus, siatMessage, company, event);
            }
            if (invoice.getStatus().equals(InvoiceStatusEnum.EMITTED)) {
                return checkAndVerivfySflEmitted(invoice, siatStatus, siatMessage, company, event);
            }
            if (invoice.getStatus().equals(InvoiceStatusEnum.CANCELED)) {
                return checkAndVerivfySflCanceled(invoice, siatStatus, siatMessage, company, event);
            }
            if (invoice.getStatus().equals(InvoiceStatusEnum.OBSERVED)) {
                return checkAndVerivfySflObserved(invoice, siatStatus, siatMessage, company, event);
            }
            throw new NotFoundAlertException(
                ErrorKeys.ERR_RECORD_NOT_FOUND,
                "Estado de factura desconocido: " + invoice.getStatus());
        }

        return false;
    }

    /**
     * Obtiene El evento de una factura
     *
     * @param invoice
     * @return
     */
    private Optional<Event> findEvent(Invoice invoice) {
        Optional<Event> event = Optional.empty();
        List<Event> events = eventRepository.findByCufdEvent(invoice.getCufd().getCufd());
        if (events.isEmpty())
            return Optional.empty();
        if (events.size() == 1)
            return Optional.of(events.get(0));

        event = events.stream().filter(e -> e.getStartDate().compareTo(invoice.getBroadcastDate()) <= 0).findFirst();
        log.info("Cantidad de eventos: {}, eligiendo evento id=: " + events.size(), event.isPresent() ? event.get().getId() : null);
        return event;
    }

    /**
     * Verificación de facturas en estado pending es un caso especial, ya que en ocaciones la factura
     * ha sido enviada a impuestos pero no se registra la respuesta.
     *
     * @param invoice
     * @param siatStatus
     * @param siatMessage
     * @return
     */
    private boolean checkAndVerivfyPending(Invoice invoice, String siatStatus, String siatMessage, Company company, Optional<Event> event) {
        //Si continúa pendiente pero ya está encolada para resolver, saltarla
        Optional<PendingToFix> pending = this.pendingToFixRepository.findByInvoiceId(invoice.getId());
        if (pending.isPresent())
            return true;

        if (InvoiceSiatStatusEnum.VALIDA.name().equals(siatStatus)) {
            declareInvoiceFix(
                invoice,
                InvoiceStatusEnum.EMITTED, //new status
                siatStatus,
                siatMessage,
                "Factura en estado pending pero válida en SIAT. Marcando com emitida"
            );
            return true;
        }

        if (InvoiceSiatStatusEnum.ANULADA.name().equals(siatStatus)) {
            declareInvoiceFix(
                invoice,
                InvoiceStatusEnum.CANCELED, //new status
                siatStatus,
                siatMessage,
                "Factura en estado pending pero SIAT la tiene anulada. Anulando"
            );
            return true;
        }

        //Si llega a este punto, significa que pendiente de envío y debo intentar reenviarla
        markInvoiceToResend(invoice, company, event);
        return true;
    }

    /**
     * Busca el estado de la factura en la web de Impuestos e infiere el estado
     *
     * @param company
     * @param invoice
     * @return
     */
    private String searchStatusInWeb(Company company, Invoice invoice) throws NotFoundAlertException {
        final String url = generateUrl(company, invoice);
        log.debug("Url generado para invoiceId={}, {}", invoice.getId(), url);
        for (int i = 0; i < 5; i++) {
            String response = getStatusFromWeb(url);
            if (!Strings.isBlank(response))
                return response;
            else
                log.debug("   intento {} de consultar {}", i, url);
        }
        throw new NotFoundAlertException(
            ErrorKeys.ERR_RECORD_NOT_FOUND,
            "No se pudo encontrar la URL luego de varios intentos. " + url);
    }

    /****
     * Verifica el estado de las facturas que en el SFL están marcadas como observadas
     *
     * @param invoice
     * @param siatStatus
     * @return
     */
    private boolean checkAndVerivfySflObserved(
        Invoice invoice,
        String siatStatus,
        String siatMessage,
        Company company,
        Optional<Event> event
    ) {

        if (InvoiceSiatStatusEnum.VALIDA.name().equals(siatStatus)) {
            declareInvoiceFix(
                invoice,
                InvoiceStatusEnum.EMITTED, //new status
                siatStatus,
                siatMessage,
                "Factura observada pero impuestos la marca com válida, marcando como valida"
            );
            return true;
        }

        if (InvoiceSiatStatusEnum.ANULADA.name().equals(siatStatus)) {
            declareInvoiceFix(
                invoice,
                InvoiceStatusEnum.CANCELED, //new status
                siatStatus,
                siatMessage,
                "Factura observada pero impuestos la tiene anulada, marcando como cancelada"
            );
            return true;
        }


        //--------------------------------------------------------------------------------------------------
        // La factura obviamente no se encuentra en impuestos, hay que revisar qué se debe realizar según la
        // oservación y el mensaje. Aglunas observaciones son correctas y finales
        //--------------------------------------------------------------------------------------------------

        String observation = getInvoiceObservation(invoice);
        if (observation == null) {
            //Caso: No se encuentra en impuestos pero tampoco tiene observación (solo está en invoice maracada como observed)
            //se marcará com un intento de reenvío
            markInvoiceToResend(invoice, company, event);
            return true;
        }


        if (observation.contains("EL NUMERO DOCUMENTO DE TIPO NIT NO ES VALIDO")) {
            log.debug("Nit inválido. Queda en observada");
            return true;
        }

        if (observation.contains("EL CUF ENVIADO YA EXISTE EN LA BASE DE DATOS DEL SIN")) {
            declareInvoiceFix(
                invoice,
                InvoiceStatusEnum.EMITTED, //new status
                siatStatus,
                siatMessage,
                "Cuf ya existe en bd del SIAT"
            );
            return true;
        }

        //Caso especial: Error interno de impuestos, esta factura se debe reenviar
        if (observation.contains("Excepcion al validar factura fuera de linea:")) {
            markInvoiceToResend(invoice, company, event);
            return true;
        }

        if (observation.contains("EL CODIGO UNICO DE FACTURA (CUF) ENVIADO EN EL XML ES INVALIDO")) {
            log.debug("Cuf. Queda en observada");
            return true;
        }

        if (observation.contains("EL NUMERO DE TARJETA SOLO PUEDE SER ENVIADO CUANDO EL METODO DE PAGO SEA CON TARJETA")) {
            log.debug("Queda en observada, número de tarjeta enviado incorrectamente");
            return true;
        }

        if (observation.contains("FECHA DE EMISION NO SE ENCUENTRA EN EL RANGO DE CONTINGENCIA")) {
            log.debug("Queda en observada, fuera rango de contingencia");
            return true;
        }

        return false;
    }


    /**
     * Marca una factura para su reenvío
     *
     * @param invoice
     */
    private void markInvoiceToResend(Invoice invoice, Company company, Optional<Event> event) {
        //Caso especial: La factura puede estar pendiente, pero si el evento está activo, espero a que lo resuelva
        // el proceso normal
        if (event.isPresent() && event.get().getActive())
            return;

        log.debug("Preparando reenvío de factura: id={}", invoice.getId());

        //Borro el wrapper
        invoiceWrapperEventRepository.deleteByInvoiceId(invoice.getId());
        log.debug("Borrar invoiceWrapper Event: eventId={}", invoice.getId());

        //Cambio el estado de la factura a pendiente, para que el enviador lo procese posteriormente
        invoice.setStatus(InvoiceStatusEnum.PENDING);
        invoiceRepository.saveAndFlush(invoice);
        log.debug("Factura observada pasada a pending: id={}", invoice.getId());

        //Debo activar el evento
        if (event.isPresent()) {
            Event evt = event.get();
            evt.setActive(true);
            eventRepository.saveAndFlush(evt);
            log.debug("Evento activado: id={}", evt.getId());
        } else {
            log.debug("No se ha encontrado ningún evento para el cufd id={}", invoice.getCufd().getId());
        }

        PendingToFix pending = new PendingToFix();
        pending.setInvoiceId(invoice.getId());
        pending.setCompany(company);
        pending.setEvent(event.isPresent() ? event.get() : null);
        pending.setAction(PendingToFixActionEnum.RESEND);
        pending.setStatus(PendingToFixStatusEnum.PENDING);
        pending.setCounter(0);
        pendingToFixRepository.saveAndFlush(pending);
        log.debug("Factira id={} adicionada a cola de verificación", invoice.getId());
    }

    /**
     * Busca la observación que tiene la factura
     *
     * @param invoice
     * @return
     */
    private String getInvoiceObservation(Invoice invoice) {
        Optional<InvoiceWrapperEvent> wrapperEvent = invoiceWrapperEventRepository.findByInvoiceId(invoice.getId());
        if (wrapperEvent.isPresent()) {
            log.debug("La obs. de la factura id={} es {}", invoice.getId(), wrapperEvent.get().getResponseMessage());
            return wrapperEvent.get().getResponseMessage();
        } else {
            log.debug("Factura sin observación: id={}", invoice.getId());
        }
        return null;
    }

    /**
     * Verifica el estado de las facturas que en el SFL están marcadas como anuladas
     *
     * @param invoice
     * @param siatStatus
     * @return
     */
    private boolean checkAndVerivfySflCanceled(Invoice invoice, String siatStatus, String siatMessage, Company company, Optional<Event> event) {
        //Caso esperado: Está anulado en SFL y SIAT
        if (InvoiceSiatStatusEnum.ANULADA.name().equals(siatStatus)) {
            return true;
        }

        //En este caso, debería estar anulada en el siat, la marco para resolver
        if (InvoiceSiatStatusEnum.VALIDA.name().equals(siatStatus)) {
            queueToFix(
                invoice,
                company,
                event,
                PendingToFixActionEnum.CANCEL_IN_SIAT
            );
            return true;
        }

        if (InvoiceSiatStatusEnum.RECHAZADA.name().equals(siatStatus)) {
            if (siatMessage.contains("NO EXISTE EN LA BASE DE DATOS DEL SIN")) {
                //Al estar anulada de nuestro lado, del lado de impuestos no hay nada que hacer
                return true;
            }
        }

        return true;
    }

    private void queueToFix(Invoice invoice, Company company, Optional<Event> event, PendingToFixActionEnum action) {
        //Si ya está registrada una incidencia, lo salto
        Optional<PendingToFix> wpending = pendingToFixRepository.findByInvoiceId(invoice.getId());
        if (wpending.isPresent())
            return;

        PendingToFix pending = new PendingToFix();
        pending.setInvoiceId(invoice.getId());
        pending.setCompany(company);
        pending.setEvent(event.isPresent() ? event.get() : null);
        pending.setAction(action);
        pending.setStatus(PendingToFixStatusEnum.PENDING);
        pending.setCounter(0);
        pendingToFixRepository.saveAndFlush(pending);
        log.debug("Factura id={} Adicionada para resolver: {}", invoice.getId(), action.name());
    }

    /**
     * Verifica el estado de las facturas que en el SFL están marcadas como emitidas
     *
     * @param invoice
     * @param siatStatus
     * @return
     */
    private boolean checkAndVerivfySflEmitted(Invoice invoice, String siatStatus, String siatMessage, Company company, Optional<Event> event) {
        //Caso esperado: Está emitida en SFL y SIAT
        if (InvoiceSiatStatusEnum.VALIDA.name().equals(siatStatus)) {
            return true;
        }

        //Caso: SFL tiene la factura emitida y SIAT anulada
        //Paso la factura a reversed
        if (InvoiceSiatStatusEnum.ANULADA.name().equals(siatStatus)) {
            declareInvoiceFix(
                invoice,
                InvoiceStatusEnum.CANCELED, //new status
                siatStatus,
                siatMessage,
                "Factura inconsistente con impuestos, marcando como cancelada"
            );
            return true;
        }

        //Caso: SFL tiene la factura emitida y SIAT tiene otro estado, analizar
        if (InvoiceSiatStatusEnum.RECHAZADA.name().equals(siatStatus)) {

            //caso especial, no respondió el siat, busqamos por url de qr
            if ("QR".equals(siatMessage)) {
                markInvoiceToResend(invoice, company, event);
                /*declareInvoiceFix(
                    invoice,
                    InvoiceStatusEnum.REVERSED, //new status
                    siatStatus,
                    siatMessage,
                    "Factura no existe en impuestos, marcando como reversed"
                );*/
                return true;
            }

            if (siatMessage.contains("NO EXISTE EN LA BASE DE DATOS DEL SIN")) {
                markInvoiceToResend(invoice, company, event);
                /*declareInvoiceFix(
                    invoice,
                    InvoiceStatusEnum.REVERSED, //new status
                    siatStatus,
                    siatMessage,
                    "Factura no existe en impuestos, marcando como reversed"
                );*/
                return true;
            }

            //Este escenario no debería darse
            if (siatMessage.contains("CODIGO UNICO DE FACTURACION DIARIA (CUFD) FUERA DE TOLERANCIA")) {
                markInvoiceToResend(invoice, company, event);
                /*declareInvoiceFix(
                    invoice,
                    InvoiceStatusEnum.REVERSED, //new status
                    siatStatus,
                    siatMessage,
                    "Cufd fuera de tolerancia"
                );*/
                return true;
            }

            //Este escenario no debería darse
            if (siatMessage.contains("EL CODIGO UNICO DE FACTURACION DIARIA (CUFD) NO SE ENCUENTRA VIGENTE")) {
                markInvoiceToResend(invoice, company, event);
                /*declareInvoiceFix(
                    invoice,
                    InvoiceStatusEnum.REVERSED, //new status
                    siatStatus,
                    siatMessage,
                    "Cufd no se encuentra vigente"
                );*/
                return true;
            }


        }

        return false;
    }

    /**
     * Registra un ajuste automático, resultdo de la verificación de un método
     *
     * @param invoice
     * @param newStatus
     * @param siatStatus
     * @param siatMessage
     * @param customMessage
     */
    private void declareInvoiceFix(
        Invoice invoice,
        InvoiceStatusEnum newStatus,
        String siatStatus,
        String siatMessage,
        String customMessage
    ) {
        log.error(
            "VERIFICACION_FACTURA|{}|{}|{}|{}|{}|{}",
            invoice.getId(),
            invoice.getStatus().name(), //old status
            newStatus,
            siatStatus,
            siatMessage,
            customMessage);

        AutomaticInvoiceFix model = new AutomaticInvoiceFix();
        model.setInvoiceId(invoice.getId());
        model.setOldStatus(invoice.getStatus());
        model.setNewStatus(newStatus);
        model.setSiatStatus(InvoiceSiatStatusEnum.valueOf(siatStatus));
        model.setSiatObservation(siatMessage);
        automaticInvoiceFixRepository.saveAndFlush(model);

        invoice.setStatus(newStatus);
        this.invoiceRepository.saveAndFlush(invoice);
    }


    /**
     * Devuelve la entidad encargada de retornar dónde está posicionada la verificación.
     * CheckInvoice.position = id de la última factura revisada
     *
     * @return
     */
    private CheckInvoice getCurrentCheckInvoice() {
        CheckInvoice check = checkInvoiceRepository.findAllNative().stream().findFirst().orElse(null);
        if (check == null) {
            check = new CheckInvoice();
            check.setPosition(0L);
            checkInvoiceRepository.saveAndFlush(check);
        }
        return check;
    }


    /**
     * Genera la URL de una factura para que se valide contra impuestos
     *
     * @param company
     * @param invoice
     * @return
     */
    private String generateUrl(Company company, Invoice invoice) throws NotFoundAlertException {
        final String template = environment.getProperty(ApplicationProperties.BATCH_REVALIDATE_INVOICES_URL_QR);
        return String.format(
            template,
            company.getNit(),
            invoice.getCuf(),
            (invoice.getSectorDocumentType().getSiatId() == 24 ? getNoteNumber(invoice.getInvoiceJson()) : invoice.getInvoiceNumber()) //si es nota de débito usar el nro de nota
        );
    }

    private String getNoteNumber(String invoiceJson) throws NotFoundAlertException {
        Map<String, Object> data = null;
        try {
            data = new ObjectMapper().readValue(invoiceJson, HashMap.class);
            LinkedHashMap<String, Object> headerData = (LinkedHashMap<String, Object>) data.get("cabecera");
            return headerData.get("numeroNotaCreditoDebito").toString();
        } catch (JsonProcessingException e) {
            throw new NotFoundAlertException("Obtener número de nota json", "No se obtuvo el nro. de la nota");
        }
    }

    /**
     * Consulta la url de impuestos y por xpath revisa el estado de la factura
     *
     * @param url
     * @return
     */
    private String getStatusFromWeb(String url) {
        try {

            Document doc = Jsoup.connect(url)
                .timeout(20000)
                .get();
            Elements data = doc.selectXpath("//table[1]//tr[1]//td[6]");

            String result = data.get(0).text();

            //Caso especial: Cuando responde la página pero no encuentra el estado, es porque
            // la factura no existe
            return ("".equals(result)) ? InvoiceSiatStatusEnum.RECHAZADA.name() : result;

        } catch (org.jsoup.HttpStatusException ex) {
            //log.error("No se pudo buscar factura en impuestos: {}, {}", url, ex.getMessage());
            return null;
        } catch (Exception ex) {
            //log.error("No se pudo buscar factura en impuestos: {}, {}", url, ex.getMessage());
            return null;
        }
    }


    /**
     * Proceso que verifica las facturas que han fallado. En caso de estar válidas en el SIAT
     * debe eliminarlas
     */
    public void processFailed() {
        //Este proceso necesita conexión con el SIAT
        if (isSiatOffline())
            return;

        log.debug("Entrando a proceso de Anular Facturas");
        if (isSiatOffline()) {
            log.debug("   Sistema fuera de línea. Saliendo.");
        }

        int quantityToCheck = Integer.parseInt(environment.getProperty(ApplicationProperties.BATCH_REVALIDATE_INVOICES_QUANTITY_FAILED));
        List<InvoiceRequest> failedInvoices = invoiceRequestRepository.findFaildRequests(PageRequest.of(0, quantityToCheck));
        failedInvoices.stream().forEach(invoiceRequest -> {
            log.info("InvoiceRequest to check: id={}", invoiceRequest.getId());
            try {
                checkRequestToCancel(invoiceRequest);
            } catch (JsonProcessingException e) {
                notificationService.notifyFailToCheckRequestToRevert(invoiceRequest);
            }
        });
    }

    /**
     * Dado un request fallido, debe revisar el estado en el SIAT y posiblemente revertir.
     * OBS: lo más seguro es que no exista la tupla factura
     *
     * @param invoiceRequest
     */
    private void checkRequestToCancel(InvoiceRequest invoiceRequest) throws JsonProcessingException {
        //Validación por si se llama a este método con un invoice request válido
        if (invoiceRequest.getResponse() || invoiceRequest.getErrorChecked())
            return;

        long diff = Math.abs( Date.from(invoiceRequest.getRequestDate().toInstant()).getTime()- (new Date()).getTime());
        if ((int) TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS) < 15 ){
            log.info("Salgo del request to cancel, factura ha pasado menos de 15 min. id={}", invoiceRequest.getId());
            return ;
        }

        //Si  no ha generado el cuf, no se podrá realizar
        if (Strings.isBlank(invoiceRequest.getCuf())) {
            invoiceRequest.setErrorChecked(true);
            invoiceRequestRepository.save(invoiceRequest);
        }

        Map<String, Object> request = new ObjectMapper().readValue(invoiceRequest.getRequest(), HashMap.class);
        String pointSaleSiat = getValueFromDataRequest(request, "pointSaleSiat");
        String branchOfficeSiat = getValueFromDataRequest(request, "branchOfficeSiat");
        String documentSectorType = getValueFromDataRequest(request, "documentSectorType");

        PointSale pointSale = pointSaleRepository.findByCompanyAndSiatId(
            invoiceRequest.getCompany().getId(),
            Integer.valueOf(pointSaleSiat),
            Integer.valueOf(branchOfficeSiat)
        ).orElseThrow(() -> new NotFoundAlertException("Point Sale no encontrado", "Point Sale no encontrado"));

        Cuis cuis = cuisRepository.findTop1ByActiveTrueAndPointSaleIdOrderByStartDateDesc(
            pointSale.getId()
        ).orElseThrow(() -> new NotFoundAlertException("Cuis no encontrado", "Cuis no encontrado"));

        Cufd cufd = cufdRepository.findTop1ByActiveTrueAndCuisIdOrderByStartDateDesc(
            cuis.getId()
        ).orElseThrow(() -> new NotFoundAlertException("Cufd no encontrado", "Cufd no encontrado"));

        String strResponse = "";
        RespuestaRecepcion siatStatus = invoiceStatusVerification(
            invoiceRequest.getCompany(),
            Integer.valueOf(pointSaleSiat),
            Integer.valueOf(branchOfficeSiat),
            cufd,
            cuis,
            invoiceRequest.getCuf(),
            Integer.valueOf(documentSectorType)
        );
        if (siatStatus.isTransaccion() && "VALIDA".equals(siatStatus.getCodigoDescripcion())) {
            log.debug("Invoice request id={} es VALIDA!", invoiceRequest.getId());
            strResponse = "VALIDA";
        } else {

            //El siat para algunos tipos de documento responde que no está disponible. Hay que verificar por QR
            if (!siatStatus.isTransaccion() &&
                !siatStatus.getMensajesList().isEmpty() &&
                siatStatus.getMensajesList().get(0).getDescripcion().contains("NO DISPONIBLE")) {
                log.debug("Invoice request id={} no puede ser encontrada. Buscar por qr. response={}", invoiceRequest.getId(), siatStatus);
                strResponse = searchStatusInWebFromRequest(invoiceRequest.getCompany(), invoiceRequest.getCuf(), invoiceRequest.getRequest());
                log.debug("Invoice request id={} qr={}", invoiceRequest.getId(), strResponse);
            } else {
                log.debug("Invoice request id={} detectada como no valida. response={}", invoiceRequest.getId(), siatStatus);
            }

        }

        //Si consultamos el estado en impuestos y es válida, significa que impuestos tiene una factura
        //que no tenemos. Hay que revertir.
        if ("VALIDA".equals(strResponse)) {

            //Caso normal esperado: el request no tiene factura.
            if (invoiceRequest.getInvoice() == null) {

                JsonNode result = sflIntegrationService.revertInvoiceForce(
                    invoiceRequest.getCuf(),
                    Integer.valueOf(pointSaleSiat),
                    Integer.valueOf(branchOfficeSiat),
                    Integer.valueOf(documentSectorType),
                    invoiceRequest.getCompany().getId(),
                    "",
                    1
                );

                if (result.get("code").intValue() == 200) {
                    log.info("Anulación de factura: anulada/no existente en SFL pero presente en siat. cuf={} companyId={} name={}",
                        invoiceRequest.getCuf(),
                        invoiceRequest.getCompany().getId(),
                        invoiceRequest.getCompany().getName()
                    );

                    //Marcamos el request como validado
                    markInvoiceRequestResolved(invoiceRequest);
                }

            } else {
                //Flujo no esperado: tiene una factura, o sea que sí fue exitoso
                //Marcamos el request como validado,
                markInvoiceRequestResolved(invoiceRequest);
            }

        } else {
            //La factura no es váldia en impuestos.
            verifyInvoiceIfMustCancel(invoiceRequest.getInvoice());
            markInvoiceRequestResolved(invoiceRequest);
        }

    }

    private String searchStatusInWebFromRequest(Company company, String cuf, String request) throws JsonProcessingException {
        final String template = environment.getProperty(ApplicationProperties.BATCH_REVALIDATE_INVOICES_URL_QR);

        //Busco en el request los datos del request
        Map<String, Object> data = new ObjectMapper().readValue(request, HashMap.class);
        String documentSectorType = getValueFromDataRequest(data, "documentSectorType");
        LinkedHashMap<String, Object> headerData = (LinkedHashMap<String, Object>) data.get("header");
        String invoiceNumber = getValueFromDataRequest(headerData, "numeroFactura");
        String noteNumber = getValueFromDataRequest(headerData, "numeroNotaCreditoDebito");

        //Formo la url
        String url = String.format(
            template,
            company.getNit(),
            cuf,
            "24".equals(documentSectorType) ? noteNumber : invoiceNumber
        );

        for (int i = 0; i < 5; i++) {
            String response = getStatusFromWeb(url);
            if (!Strings.isBlank(response))
                return response;
            else
                log.debug("   intento {} de consultar {}", i, url);
        }

        return "INVALIDA";
    }

    private String getValueFromDataRequest(Map<String, Object> data, String property) {
        try {
            return String.valueOf((Integer) data.get(property));
        } catch (Exception e) {
        }
        return (String) data.get(property);
    }

    /**
     * Verify if the invoice is really reversed or canceled
     *
     * @param invoice
     */
    private void verifyInvoiceIfMustCancel(Invoice invoice) {
        if (invoice != null) {
            if (!InvoiceStatusEnum.REVERSED.equals(invoice.getStatus()) &&
                !InvoiceStatusEnum.CANCELED.equals(invoice.getStatus())) {
                log.info("Facura id={} cuf={} anulada porque se ha detectado que no está en el SIAT", invoice.getId(), invoice.getCuf());
                invoice.setStatus(InvoiceStatusEnum.CANCELED);
                invoiceRepository.save(invoice);
                invoiceRepository.flush();
            }
        }
    }

    /**
     * Marca el request como resuelto
     *
     * @param invoiceRequest
     */
    private void markInvoiceRequestResolved(InvoiceRequest invoiceRequest) {
        invoiceRequest.setErrorChecked(true);
        invoiceRequestRepository.save(invoiceRequest);
        invoiceRequestRepository.flush();
    }

    /**
     * Verifica el estado de una factura contra el SIAT
     * Esta versión recibe diferente parámetros que la anterior, se decidió
     * reimplementar por lo la disposición de cómo se obtenían ciertos datos
     * <p>
     * NOTA:  El siat en ocaciones responde que no tiene implementado este método para cierto tipo de
     * documento. En estos casos se sugiere llamar a la validación por QR
     * (implementada en un método posterior)
     *
     * @param company
     * @param pointSaleSiat
     * @param branchOfficeSiat
     * @param cufd
     * @param cuis
     * @param cuf
     * @param documentSectorType
     * @return
     */
    private RespuestaRecepcion invoiceStatusVerification(Company company,
                                                         Integer pointSaleSiat,
                                                         Integer branchOfficeSiat,
                                                         Cufd cufd,
                                                         Cuis cuis,
                                                         String cuf,
                                                         Integer documentSectorType) {

        InvoiceType invoiceType = invoiceTypeRepository.getBySiatIdAndCompanyIdAndActiveIsTrue(SiatInvoiceType.map.get(documentSectorType), company.getId().intValue());
        //Integer documentSectorTypeId = sectorDocumentTypeRepository.getBySiatIdAndCompanyIdAndActiveIsTrue(documentSectorType, company.getId().intValue()).getId().intValue();


        ServicioFacturacion servicioFacturacion = soapUtil.getBuySellService(environment.getProperty(ApplicationProperties.OFFLINE_APIKEY));
        SolicitudVerificacionEstado request = new SolicitudVerificacionEstado();
        request.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
        request.setCodigoDocumentoSector(documentSectorType);
        request.setCodigoEmision(1); // this field accepts only 1, ONLINE = 1
        request.setCodigoModalidad(company.getModalitySiat().getKey());
        request.setCodigoPuntoVenta(pointSaleSiat);
        request.setCodigoSistema(company.getSystemCode());
        request.setCodigoSucursal(branchOfficeSiat);
        request.setCufd(cufd.getCufd());
        request.setCuis(cuis.getCuis());
        request.setNit(company.getNit());
        request.setTipoFacturaDocumento(invoiceType.getSiatId());
        request.setCuf(cuf);
        return servicioFacturacion.verificacionEstadoFactura(request);
    }


}
