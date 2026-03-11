package bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthTokenGenerationReq {

    protected String client;
    protected String ip;
    @NotBlank
    protected String login;
    protected Long nit;
    @NotBlank
    protected String password;
    protected Integer tipoClienteId;
    protected Integer tipoUsuarioId;

    @SuppressWarnings("static-access")
    public String toString() {
        //TODO add more info
        return "{ \"login\": \"%s\"  }".format(login);
    }

}
