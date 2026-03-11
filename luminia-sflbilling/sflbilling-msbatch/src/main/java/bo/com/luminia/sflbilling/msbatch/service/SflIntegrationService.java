package bo.com.luminia.sflbilling.msbatch.service;


import bo.com.luminia.sflbilling.msbatch.config.ApplicationProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SflIntegrationService {

    private final Environment environment;

    private static String bearerToken = null;

    /**
     * Reversión forzosa de una factura. Utilizar este método cuando sospecha que la
     * transacción está en el siat pero en el SFL no existe la factura
     *
     * @param cuf
     * @param cancellationReasonSiat
     * @param companyId
     * @return
     */
    public JsonNode revertInvoiceForce(
                String cuf,
                Integer pointSaleSiat,
                Integer branchOfficeSiat,
                Integer sectorDocumentTypeSiat,
                Long companyId,
                String email,
                Integer cancellationReasonSiat
    ) {
        String urlRevert = environment.getProperty(ApplicationProperties.SFL_INTEGRATION_URL_REVERT_FORCE);

        JSONObject body = new JSONObject();
        body.put("cuf", cuf);
        body.put("pointSaleSiat", pointSaleSiat);
        body.put("branchOfficeSiat", branchOfficeSiat);
        body.put("sectorDocumentTypeSiat", sectorDocumentTypeSiat);
        body.put("email", email);
        body.put("cancellationReasonSiat", cancellationReasonSiat);
        body.put("companyId", companyId);

        return callPost(urlRevert, body, 1);
    }

    /**
     * Autentica contra el módulo sflbilling-msaccount
     * Devuelve el token para continuar las operaciones
     *
     * @return Token de autenticación o nulo en caso de fallo
     */
    private String getToken() {
        if (Strings.isBlank(bearerToken)) {

            try {
                String url = environment.getProperty(ApplicationProperties.SFL_INTEGRATION_URL_LOGIN);

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                JSONObject body = new JSONObject();
                body.put("username", environment.getProperty(ApplicationProperties.SFL_INTEGRATION_USER));
                body.put("password", environment.getProperty(ApplicationProperties.SFL_INTEGRATION_PASSWORD));

                HttpEntity<String> requestEmail = new HttpEntity<String>(body.toString(), headers);
                String resultEmail = restTemplate.postForObject(url, requestEmail, String.class);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(resultEmail);
                if (root.get("code").intValue() == 200) {
                    return bearerToken = root.get("body").get("idToken").asText();
                }

                return null;

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        } else {
            return bearerToken;
        }
    }

    /**
     * Invocación genérica de un ws
     *
     * @param url path del recurso
     * @param body json a enviar
     * @param retries número de intentos en caso de fallo por autenticación
     *
     * @return
     */
    private JsonNode callPost(String url, JSONObject body, int retries) {
        try {

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(getToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<String>(body.toString(), headers);
            String wsResult = restTemplate.postForObject(url, request, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(wsResult);

            return json;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.UNAUTHORIZED.equals(e.getStatusCode())) {
                if (retries > 0)
                    return callPost(url, body, retries - 1);
            } else
                throw new RuntimeException(e);
        }
        throw new RuntimeException();
    }


}
