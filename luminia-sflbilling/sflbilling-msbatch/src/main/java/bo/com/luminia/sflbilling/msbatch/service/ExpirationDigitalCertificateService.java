package bo.com.luminia.sflbilling.msbatch.service;

import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.domain.Signature;
import bo.com.luminia.sflbilling.msbatch.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.ExpirationDigitalCertificateResp;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpirationDigitalCertificateService {

    private final Environment environment;
    private final EntityManager em;

    public ExpirationDigitalCertificateResp process() {
        String days = environment.getProperty(ApplicationProperties.EXPIRATION_DAYS);

        //TODO se debe manejar un solo formato de fecha
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(days));

        String sql = "select s from Signature s " +
            String.format(" where s.endDate = :myDate ", days) +
            " and s.active = true ";
        Query query = em.createQuery(sql).setParameter("myDate", getDate(Integer.parseInt(days)));

        List<Signature> list = query.getResultList();
        for (Signature s : list) {
            sendNotification(s);
        }

        ExpirationDigitalCertificateResp response = new ExpirationDigitalCertificateResp();
        response.setCode(SiatResponseCodes.SUCCESS);
        response.setMessage("Operacion realizada con exito.");
        return response;
    }

    private void sendNotification(final Signature s) {
        String luminiaUrl = environment.getProperty(ApplicationProperties.NOTIFICATION_ENDPOINT);
        log.info(String.format("Certificado Expirado : %s", s.getCompany().getBusinessName()));
        String htmlContent = "<h3>Notificaci&oacute;n de Expiraci&oacute;n de Certificado Digital</h3>\n" +
            "<p>Su certificado digital esta a punto de expirar. La fecha registrada de expiraci&oacute;n es : <strong>%s</strong></p>\n" +
            "<p>Por favor debe realizar la actualizaci&oacute;n de la misma ya que caso contrario no podra realizar transacciones. <br /><br />Muchas Gracias<br />Luminia S.A.</p>";

        String subject = "Advertencia de Expiracion de Certificado Digital SFE - Luminia";

        String date = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(s.getEndDate());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject content = new JSONObject();
        content.put("to", Arrays.asList(s.getCompany().getEmailNotification()));
        content.put("subject", subject);
        content.put("html", true);
        content.put("message", String.format(htmlContent, date));

        HttpEntity<String> request =
            new HttpEntity<String>(content.toString(), headers);

        ObjectMapper objectMapper = new ObjectMapper();

        String personResultAsJsonStr = restTemplate.postForObject(luminiaUrl, request, String.class);
        try {
            JsonNode root = objectMapper.readTree(personResultAsJsonStr);
            if (root.get("code").equals("200")) {
                log.info(String.format("Envio correcto a %s notificando expiracion de certificado digital", s.getCompany().getEmailNotification()));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
    }

    private ZonedDateTime getDate(Integer days) {
        // TODO los formatos de fecha se debe manejar uno solo
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate response = LocalDate.now().plusDays(days);
        ZonedDateTime startDate = response.atStartOfDay(ZoneId.systemDefault());
        return startDate;
    }

}
