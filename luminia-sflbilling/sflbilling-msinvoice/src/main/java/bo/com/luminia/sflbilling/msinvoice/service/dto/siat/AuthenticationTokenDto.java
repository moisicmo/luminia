package bo.com.luminia.sflbilling.msinvoice.service.dto.siat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationTokenDto {

    private String token;
    private String estado;
    private String siglaSistema;
}
