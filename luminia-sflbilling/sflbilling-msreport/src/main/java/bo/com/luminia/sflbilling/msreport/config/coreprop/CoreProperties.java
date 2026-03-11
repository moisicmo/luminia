package bo.com.luminia.sflbilling.msreport.config.coreprop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.cors.CorsConfiguration;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "coreprop", ignoreUnknownFields = false)
@PropertySources({
    @PropertySource(value = "classpath:git.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:META-INF/build-info.properties", ignoreResourceNotFound = true)
})
@Getter
public class CoreProperties {

    private final Security security = new Security();

    private final CorsConfiguration cors = new CorsConfiguration();

    @Setter
    @Getter
    public static class Security {

        private String contentSecurityPolicy = CoreDefaults.Security.contentSecurityPolicy;

        private final Authentication authentication = new Authentication();

        private final RememberMe rememberMe = new RememberMe();

        @Getter
        public static class Authentication {

            private final Jwt jwt = new Jwt();

            @Setter
            @Getter
            public static class Jwt {

                private String secret = CoreDefaults.Security.Authentication.Jwt.secret;

                private String base64Secret = CoreDefaults.Security.Authentication.Jwt.base64Secret;

                private long tokenValidityInSeconds = CoreDefaults.Security.Authentication.Jwt.tokenValidityInSeconds;

                private long tokenValidityInSecondsForRememberMe = CoreDefaults.Security.Authentication.Jwt.tokenValidityInSecondsForRememberMe;

            }
        }

        @Setter
        @Getter
        public static class RememberMe {

            @NotNull
            private String key = CoreDefaults.Security.RememberMe.key;
        }
    }
}
