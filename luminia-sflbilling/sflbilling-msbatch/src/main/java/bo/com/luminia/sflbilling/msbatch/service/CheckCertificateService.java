package bo.com.luminia.sflbilling.msbatch.service;

import bo.com.luminia.sflbilling.msbatch.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msbatch.repository.SignatureRepository;
import bo.com.luminia.sflbilling.msbatch.service.dto.TokenCertificateDto;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.Signature;
import bo.com.luminia.sflbilling.msbatch.config.ApplicationProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CheckCertificateService {

    private final CompanyRepository companyRepository;

    private final SignatureRepository signatureRepository;

    private final Environment environment;

    public void process() {
        List<TokenCertificateDto> companyInfo = new ArrayList<>();
        companyRepository.findAllByActiveTrue().stream()
            //.filter(c -> c.getId() == 1651)
            .forEach(company -> {
                checkCompanyStatus(company, companyInfo);
            });
        notifyExpirations(companyInfo);
    }

    private void notifyExpirations(List<TokenCertificateDto> companyInfo) {
        int daysToWarn = Integer.valueOf(environment.getProperty(ApplicationProperties.CHECK_TOKEN_CERTIFICATE_DAYS_TO_NOTIFY));
        List<TokenCertificateDto> toWarn = companyInfo.stream()
            .filter(comp -> (comp.getDaysToExpireCertificate() <= daysToWarn || comp.getDaysToExpireToken() <= daysToWarn))
            .collect(Collectors.toList());
        if (!toWarn.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("<h3>Notificaci&oacute;n validez de Certificado Digital y token</h3>").append("\n");
            builder.append("Favor verificar los siguientes certificados y tokens, tome en cuenta que si vencen no podrán emitir facturas.");
            builder.append("<br/>");
            builder.append("<ul>");
            toWarn.forEach(comp -> {
                builder.append("<li>").append("\n");
                builder.append("id=").append(comp.getId()).append(" nombre=").append(comp.getCompany()).append("\n");
                builder.append("   <ul>");
                if (comp.getEndToken() == null) {
                    builder.append("      <li><span style=\"color:red\">Sin token</span></li>").append("\n");
                } else {
                    if (comp.getDaysToExpireToken() < daysToWarn) {
                        builder.append("      <li>").append("\n");
                        builder.append("        Token vence en: ")
                            .append(comp.getDaysToExpireToken()).append(" días - ")
                            .append(comp.getEndToken());
                        if (comp.getDaysToExpireToken() < 5)
                            builder.append("<span style=\"color:orange\">Urgente</span></li>");
                        builder.append("      </li>").append("\n");
                    }
                }
                if (comp.getEndCertificate() == null || comp.getDaysToExpireCertificate() < 0) {
                    builder.append("      <li><span style=\"color:red\">Sin certificado o ya vencido</span></li>").append("\n");
                } else {
                    if (comp.getDaysToExpireCertificate() < daysToWarn) {
                        builder.append("      <li>").append("\n");

                        builder.append("        Certificado vence en: ")
                            .append(comp.getDaysToExpireCertificate()).append(" días - ")
                            .append(comp.getEndCertificate());
                        if (comp.getDaysToExpireCertificate() < 5)
                            builder.append("<span style=\"color:orange\">Urgente</span></li>");
                        builder.append("      </li>").append("\n");
                        builder.append("   </ul>");
                    }
                }
                builder.append("</li>").append("\n");
            });
            builder.append("</ul>");
            notifyEmail(builder.toString());
        } else {
            log.info("Todas las compañías activas tienen token y certififcado superior a los {} días", daysToWarn);
        }
    }

    private void notifyEmail(String msg) {
        log.debug("Enviando correo, aviso de certificados y tokens a vencer. Mensaje: \n{}", msg);

        String stage = environment.getProperty(ApplicationProperties.NOTIFICATION_ONLINE_OFFLINE_STAGE);
        String email = environment.getProperty(ApplicationProperties.NOTIFICATION_ONLINE_OFFLINE_EMAIL);
        log.debug("stage: {}", stage);
        log.debug("email: {}", email);

        sendEmailToNotify(stage,
            msg,
            ZonedDateTime.now().toString(),
            email
        );
    }


    //Si funciona a refactorizar
    private void sendEmailToNotify(String stage, String msg, String dateTimeStr, String emailTo) {
        new Thread(() -> {

            String emailUrl = environment.getProperty(ApplicationProperties.NOTIFICATION_ENDPOINT);
            String subject = stage.toUpperCase() + " SFL - Aviso de validez Tokens y Certificados";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject content = new JSONObject();
            content.put("to", Arrays.asList(emailTo));
            content.put("subject", subject);
            content.put("html", true);
            content.put("message", msg);

            HttpEntity<String> requestEmail = new HttpEntity<String>(content.toString(), headers);
            String resultEmail = restTemplate.postForObject(emailUrl, requestEmail, String.class);

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(resultEmail);

                if (root.get("code").equals("200")) {
                    log.info(String.format("Envio exitoso al correo electrónico: %s", emailTo));
                } else {
                    log.error("Correo no enviado a {}: {}", emailTo, root.toString());
                }

            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }).start();
    }

    private void checkCompanyStatus(Company company, List<TokenCertificateDto> companyInfo) {
        TokenCertificateDto data = new TokenCertificateDto();
        data.setId(company.getId());
        data.setCompany(company.getName());
        data.setEndCertificate(getEndCertificate(company));
        data.setEndToken(getEndToken(company));
        companyInfo.add(data);
    }

    private Date getEndCertificate(Company company) {
        Optional<Signature> signature = signatureRepository.findByCompanyActive(company.getId());
        if (signature.isPresent()) {
            try {
                String cetificate = signature.get().getCertificate();
                CertificateFactory factory = CertificateFactory.getInstance("X.509");
                X509Certificate cert = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(cetificate.getBytes()));
                return cert.getNotBefore();
            } catch (Exception e) {
                log.error("No se pudo obtener el certificado para id={}, compalía={}", company.getId(), company.getToken());
                return null;
            }
        }
        return null;
    }

    private Date getEndToken(Company company) {
        try {
            byte[] decoded = Base64.decodeBase64(company.getToken());
            String json = new String(decoded, StandardCharsets.UTF_8);
            json = json.substring(json.indexOf("{\"sub"));

            Map<String, Object> data = new ObjectMapper().readValue(json, HashMap.class);
            String strEpoch = String.valueOf(data.get("exp"));

            return new Date(Long.parseLong(strEpoch + "000"));

        } catch (Exception e) {
            log.error("No se pudo calcular el final del token para id={}, compalía={}", company.getId(), company.getToken());
            return null;
        }
    }

}
