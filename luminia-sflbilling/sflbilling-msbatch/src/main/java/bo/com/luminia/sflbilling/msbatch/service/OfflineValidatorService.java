package bo.com.luminia.sflbilling.msbatch.service;

import bo.com.luminia.sflbilling.msbatch.repository.*;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msbatch.service.dto.InvoiceOffline;
import bo.com.luminia.sflbilling.domain.*;
import bo.com.luminia.sflbilling.msbatch.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msbatch.repository.*;
import bo.com.luminia.sflbilling.msbatch.task.InvoiceOnlineTask;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.BadRequestAlertException;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.NotFoundAlertException;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.CufdNotFoundException;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.OfflineValidatorRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.SyncRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.SoapUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfflineValidatorService extends ConnectivityBase {

    private final EntityManager em;
    private final Environment environment;
    private final SoapUtil soapUtil;
    private static final int NO_INTERNET = 1;
    private static final int INTERNET_OK = 0;
    private static final int SIAT_NO_OK = 2;
    private final EventRepository eventRepository;
    private final OfflineRepository offlineRepository;
    private final CompanyRepository companyRepository;
    private final BranchOfficeRepository branchOfficeRepository;
    private final PointSaleRepository pointSaleRepository;
    private final CufdRepository cufdRepository;
    private final SignificantEventRepository significantEventRepository;
    private final InvoiceOnlineTask invoiceOnlineTask;
    private final SyncCodeService syncCodeService;
    private final NotificationService notificationService;

    public synchronized OfflineValidatorRes process() {
        log.debug("0. Procesando validación online/offline");

        OfflineValidatorRes response = new OfflineValidatorRes();
        response.setCode(SiatResponseCodes.SUCCESS);

        Optional<Offline> previousStatus = offlineRepository.findAll().stream().findFirst();
        StopWatch cron = new StopWatch("Proceso onnline/offline");

        try {
            int tryNumber = Integer.parseInt(environment.getProperty(ApplicationProperties.OFFLINE_TRY_NUMBER));
            long tryTimeConnection = Long.parseLong(environment.getProperty(ApplicationProperties.OFFLINE_TRY_TIME_CONNECTION));
            log.debug("1. intentos: {}. tiempoEntroConexiones:{}", tryNumber, tryTimeConnection);

            int count = 1;
            int resultConnectivity = 0;
            while (count <= tryNumber) {
                int resultConnectivityTemp = resultConnectivity;

                cron.start("   online offline intento: " + count);

                resultConnectivity = checkConnectivity(environment.getProperty(ApplicationProperties.OFFLINE_APIKEY), environment, soapUtil);

                cron.stop();

                // Valida el reinicio del contador.
                count = resultConnectivityTemp == resultConnectivity ? count : 1;

                if (resultConnectivity == SIAT_NO_OK) {
                    log.debug("   intento {}: Conexión con SIAT fallida!!!!", count);
                    Thread.sleep(tryTimeConnection);
                    count++;
                } else {
                    log.debug("   intento {}: Conexión con SIAT ok", count);
                    Thread.sleep(tryTimeConnection);
                    count++;
                }
            }

            //if (resultConnectivity == SIAT_NO_OK) {
            if (resultConnectivity != SiatResponseCodes.SIAT_HAS_CONNECTIVITY) {
                boolean passToOffline = !previousStatus.isPresent() ||
                    (previousStatus.isPresent() && !previousStatus.get().getActive());

                if (passToOffline) {
                    log.debug("3. SIAT no ok, pasando a offline: {}", resultConnectivity);
                    this.offline(createOffLine(SIAT_NO_OK));
                    sendEmailToNotify("Sistema fuera de línea SFL");
                } else {
                    log.debug("3. SIAT no ok, mantenemos fuera de línea");
                }
            } else {
                boolean passToOnline = !previousStatus.isPresent() ||
                    (previousStatus.isPresent() && previousStatus.get().getActive());

                //Vamos a pasar a online
                if (passToOnline) {
                    if(previousStatus.get().getManualStatus()){
                        log.debug("3. SIAT ok, pero flag manual esta activado {}", resultConnectivity);
                        log.debug("3. Estado manual activado, mantenemos fuera de línea");
                    }else{
                        log.debug("3. SIAT ok, pasamos a online {}", resultConnectivity);
                        online();
                        sendEmailToNotify("Sistema en línea SFL");
                    }
                } else {
                    log.debug("3. SIAT ok, se mantiene en modo online");
                }
            }


        } catch (Exception e) {
            log.debug("4. Excepción, pasando a offline: {}", e);
            this.offline(createOffLine(SIAT_NO_OK));
        }

        log.debug("5. Termino verificacion:{}", cron.getTotalTimeMillis());
        if (cron.getTotalTimeSeconds() >= 60) {
            log.debug("Han pasado 60 seg. o más para verificar el online/offline");
            log.debug(cron.prettyPrint());
        }

        return response;
    }

    private InvoiceOffline createOffLine(int value) {
        InvoiceOffline offline = new InvoiceOffline();
        offline.setDescription("Fuera de Linea/Job");
        offline.setSignificantEventSiatId(value);
        return offline;
    }

    public synchronized void online() {
        Optional<Offline> searchOffline = offlineRepository.findAll().stream().findFirst();
        if (!searchOffline.isPresent()) {
            log.error("No existe el registro Fuera de Linea.");
            return;
        }
        Offline updateOffline = searchOffline.get();
        // Verifica si el sistema ya se encuentra en linea.
        if (updateOffline.getActive().equals(false)) {
            return;
        }

        updateOffline.setActive(false);
        Offline offline = offlineRepository.save(updateOffline);
        offlineRepository.flush(); //force to persist, avoid cache
        log.debug("Sistema En Linea");
        log.debug("Lanzando Jobs...");

        this.callCufdForCompanies();
    }

    private void sendEmailToNotify(String msg) {
        log.debug("Entrando a enviar notificación corre. Mensaje: {}", msg);

        String stage = environment.getProperty(ApplicationProperties.NOTIFICATION_ONLINE_OFFLINE_STAGE);
        String email = environment.getProperty(ApplicationProperties.NOTIFICATION_ONLINE_OFFLINE_EMAIL);

        log.debug("stage: {}", stage);
        log.debug("email: {}", email);

        try {
            sendEmailToNotify(
                stage,
                msg + " " + stage,
                ZonedDateTime.now().toString(),
                email
            );
        } catch (Exception e) {
            log.error("Error enviando mail de notificación online/offline {}", e);
            e.printStackTrace();
        }
    }

    /**
     * Se debe llamar nuevamente a sincronizar los cuis y cufd para poder lanzar los jobs
     */
    private void callCufdForCompanies() {
        List<Company> companies = companyRepository.findAllByEventSendIsTrueAndActiveIsTrue();
        companies.forEach(c -> {
            callCufdForCompaniesThread(c);
        });
    }

    private void callCufdForCompaniesThread(Company c) {
        new Thread(() -> {
            try {
                log.info("Sincronizando por hilo cuf/cuis de compañía id={}, name={}", c.getId(), c.getName());

                SyncRes response = null;
                int counter = 0;
                do {
                    response = syncCodeService.synchronize(c);
                    counter++;
                } while (!response.getResponse() && counter < 5);

                if (!response.getResponse()) {
                    log.error("URGENTE: Compañía no pudo sincronizar cufd/cuis no sinc: id={}, name={}", c.getId(), c.getName());
                    notificationService.notifyCufCuisNotSync(c);
                }else{
                    log.info("Exito Sincronizando por hilo cuf/cuis de compañía id={}, name={}", c.getId(), c.getName());
                }
            } catch (Exception e) {
                log.debug("Error sincronizando cuf/cuis de compañía id={}, name={}", c.getId(), c.getName());
            }

        }).start();
    }


    public synchronized void offline(InvoiceOffline invoiceOfflineReq) {
        Optional<Offline> searchOffline = offlineRepository.findAll().stream().findFirst();
        if (!searchOffline.isPresent()) {
            log.info("No existen registros de Fuera de Linea");
            return;
        }

        Offline updateOffline = searchOffline.get();
        // Verifica si el sistema ya se encuentra fuera de linea.
        if (updateOffline.getActive()) {
            log.error("El sistema se encuentra fuera de Linea");
            return;
        }

        // Itera la lista de empresas habilitados para fuera de linea.
        List<Company> listCompany = companyRepository.findAllByEventSendIsTrueAndActiveIsTrue();
        for (Company company : listCompany) {
            // Itera la lista de sucursales.
            List<BranchOffice> listBranchOffice = branchOfficeRepository.findAllByCompanyIdAndActiveIsTrue(company.getId());
            for (BranchOffice branchOffice : listBranchOffice) {
                // Itera la lista de puntos de venta.
                List<PointSale> listPointSale = pointSaleRepository.findAllByBranchOfficeIdAndActiveIsTrue(branchOffice.getId());
                for (PointSale pointSale : listPointSale) {
                    // 4. Obtiene el CUFD activo.
                    Optional<Cufd> cufdOptional = null;
                    try {
                        cufdOptional = this.obtainCufdActive(company.getId(), branchOffice.getBranchOfficeSiatId(), pointSale.getPointSaleSiatId());
                    } catch (CufdNotFoundException e) {
                        log.error("Cuf no encontrado para companyId: {}, branchOfficeSiatId : {}, pointSaleSiatId: {}",
                            company.getId(),
                            branchOffice.getBranchOfficeSiatId(),
                            pointSale.getPointSaleSiatId()
                        );
                        //throw new CufdNotFoundException();
                        continue;
                    } catch (Exception e) {
                        log.error("Error buscando cufd para la companyId: {}, branchOfficeSiatId : {}, pointSaleSiatId: {}",
                            company.getId(),
                            branchOffice.getBranchOfficeSiatId(),
                            pointSale.getPointSaleSiatId()
                        );
                        //throw e;
                        continue;
                    }

                    if (cufdOptional.isPresent()) {
                        Cufd cufdFromDb = cufdOptional.get();
                        log.debug("4. Datos del código CUFD :{} , company:{}-{}, branchOffice:{} , PointSale:{} . siatId:{}",
                            cufdFromDb,
                            company.getId(),
                            company.getName(),
                            branchOffice.getId(),
                            pointSale.getId(),
                            invoiceOfflineReq.getSignificantEventSiatId()
                        );

                        Event newEvent = new Event();
                        newEvent.setCufdEvent(cufdFromDb.getCufd());
                        newEvent.setStartDate(ZonedDateTime.now());
                        newEvent.setEndDate(ZonedDateTime.now());
                        newEvent.setReceptionCode(null);
                        newEvent.setDescription(invoiceOfflineReq.getDescription());
                        newEvent.setActive(true);
                        newEvent.setCafc(null);
                        newEvent.setBranchOffice(branchOffice);
                        newEvent.setPointSale(pointSale);
                        newEvent.setSignificantEvent(significantEventRepository.findByCompanyIdAndSiatIdAndActiveTrue(company.getId().intValue(), invoiceOfflineReq.getSignificantEventSiatId()));

                        eventRepository.save(newEvent);
                        log.debug("Registro evento preliminar: {}", newEvent);
                    }
                }
            }
        }

        updateOffline.setActive(true);
        Offline offline = offlineRepository.save(updateOffline);
        offlineRepository.flush();  //force to persist, avoid cache
        log.debug("Fuera de linea activo: {}", offline);
    }


    public static int getStatus(String url) throws IOException {

        int result = NO_INTERNET;
        try {
            URL urlObj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
            con.setRequestMethod("GET");
            // Set connection timeout
            con.setConnectTimeout(3000);
            con.connect();

            int code = con.getResponseCode();
            if (code == 200) {
                result = INTERNET_OK;
            }
        } catch (Exception e) {
            result = NO_INTERNET;
        }
        return result;
    }

    /**
     * Método que obtiene el código CUFD vigente.
     *
     * @param companyId        Id de la empresa.
     * @param branchOfficeSiat Id de la sucursal Siat.
     * @param pointSaleSiat    Id punto de venta Siat.
     * @return
     * @throws CufdNotFoundException
     */
    protected Optional<Cufd> obtainCufdActive(Long companyId, int branchOfficeSiat, int pointSaleSiat) throws CufdNotFoundException {
        Optional<Cufd> cufdOptional = cufdRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(pointSaleSiat, branchOfficeSiat, companyId);
        if (!cufdOptional.isPresent()) {
            log.error("No hay cufd para la companyId: {}, branchOfficeSiatId : {}, pointSaleSiatId: {}",
                companyId,
                branchOfficeSiat,
                pointSaleSiat
            );
            //throw new CufdNotFoundException();
        }
        return cufdOptional;
    }

    private ZonedDateTime getDate(Integer days) {
        // TODO los formatos de fecha se debe manejar uno solo
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate response = LocalDate.now().plusDays(days);
        ZonedDateTime startDate = response.atStartOfDay(ZoneId.systemDefault());
        return startDate;
    }

    private void sendEmailToNotify(String stage, String msg, String dateTimeStr, String emailTo) {
        new Thread(() -> {

            String emailUrl = environment.getProperty(ApplicationProperties.NOTIFICATION_ENDPOINT);
            String htmlContent = "<p>Cambio de estado SFL:</p>" +
                "<p>" + msg + "</p>" + "<br/>" +
                "<p> Hora:" + dateTimeStr + "</p>";
            String subject = stage.toUpperCase() + " Cambio de estado SFL";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject content = new JSONObject();
            content.put("to", Arrays.asList(emailTo));
            content.put("subject", subject);
            content.put("html", true);
            content.put("message", htmlContent);

            HttpEntity<String> requestEmail = new HttpEntity<String>(content.toString(), headers);
            String resultEmail = restTemplate.postForObject(emailUrl, requestEmail, String.class);

            try {

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(resultEmail);
                if (root.get("code").equals("200")) {
                    log.info(String.format("Envio exitoso al correo electrónico: %s", emailTo));
                }else{
                    log.error("Correo no enviado a {}: {}", emailTo, root.toString());
                }

            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }).start();
    }

    public void goToOfflineManual(InvoiceOffline request){
        Optional<Offline> searchOffline = offlineRepository.findAll().stream().findFirst();
        if (!searchOffline.isPresent()) {
            log.info("No existen registros de Fuera de Linea");
            throw new NotFoundAlertException("No existen registros de Fuera de Linea", null);
        }

        Offline updateOffline = searchOffline.get();
        // Verifica si el sistema ya se encuentra fuera de linea.
        if (updateOffline.getActive()) {
            log.error("El sistema se encuentra fuera de Linea");
            throw new BadRequestAlertException("GO_TO_OFFLINE", "El sistema ya se encuentra fuera de Linea");
        }

        updateOffline.setManualStatus(true);
        Offline offline = offlineRepository.save(updateOffline);
        offlineRepository.flush();
        log.debug("Fuera de linea manual activo: {}", offline);
        this.offline(request);
    }

    public void tryToGoOnlineManual(){
        Optional<Offline> searchOffline = offlineRepository.findAll().stream().findFirst();
        if (!searchOffline.isPresent()) {
            log.info("No existen registros de Fuera de Linea");
            throw new NotFoundAlertException("No existen registros de Fuera de Linea", null);
        }
        Offline updateOffline = searchOffline.get();
        if (updateOffline.getActive().equals(false)) {
            log.error("El sistema se encuentra en Linea");
            throw new BadRequestAlertException("GO_TO_OFFLINE", "El sistema ya se encuentra en Linea");
        }
        updateOffline.setManualStatus(false);
        offlineRepository.save(updateOffline);
        offlineRepository.flush();
        log.debug("Fuera de linea manual desactivado");
    }
}
