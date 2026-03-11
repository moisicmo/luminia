package bo.com.luminia.sflbilling.msaccount.web.rest;

import bo.com.luminia.sflbilling.msaccount.security.jwt.JWTFilter;
import bo.com.luminia.sflbilling.msaccount.security.jwt.TokenProvider;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.LoginReq;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserJWTController {

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authorize(@Valid @RequestBody LoginReq loginReq) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginReq.getUsername(),
            loginReq.getPassword()
        );

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.createToken(authentication, loginReq.isRememberMe());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

            HashMap<String, Object> token = new HashMap<>();
            AuthenticateRes body = new AuthenticateRes();
            token.put("idToken", jwt);

            body.setBody(token);
            body.setCode(ResponseCodes.SUCCESS);
            body.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);

            return new ResponseEntity(body, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {

            AuthenticateRes body = new AuthenticateRes();
            body.setCode(ResponseCodes.ERROR);
            body.setMessage((ResponseMessages.ERROR_CREDENCIALES_INVALIDOS));

            return new ResponseEntity<>(body, null, HttpStatus.FORBIDDEN);
        }
    }

    @Data
    @AllArgsConstructor
    static class JWTToken {

        public JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        private String idToken;

        @JsonProperty("code")
        private Integer code;

        @JsonProperty("message")
        private String message;
    }
}
