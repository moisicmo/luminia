package bo.com.luminia.sflbilling.msinvoice.service.sfe.authentication;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio de llamada Impuestos Internos para generar token
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthTokenGenerationService {

    /**
     * Llama al servicio SOAP de impuestos para obtener token de acuerdo a una empresa
     * @param request
     * @return
     */
/*
    public Optional<AuthTokenGenerationRes> callAuthenticationService (AuthTokenGenerationReq request) {

        log.info("Solicitando token al servicio de Impuestos login:{}",request.getLogin());

        ServicioAutenticacionSoap_Service service = new ServicioAutenticacionSoap_Service();
        ServicioAutenticacionSoap serviceToken = service.getAutenticacionSoapServiceImplPort() ;

        DatosUsuarioRequest userRequest = new DatosUsuarioRequest();
        userRequest.setNit(request.getNit());
        userRequest.setLogin(request.getLogin());
        userRequest.setPassword(request.getPassword());

        UsuarioAutenticadoDto soapResponse = serviceToken.token(userRequest);
        Optional<UsuarioAutenticadoDto> result = Optional.of(soapResponse);

        AuthTokenGenerationRes response  = new AuthTokenGenerationRes();

        //TODO add more info in response
        if (!soapResponse.isOk() ) {
            response.setCode(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT);
            response.setMessage(soapResponse.getMensajes().get(0).getDescripcionUi());

            log.info("Respuesta del Servicio de Impuesto para login: {}, respuesta: {}",request.getLogin(), soapResponse);
            return Optional.of(response);
        }

        AuthTokenGenerationDto dto = new AuthTokenGenerationDto();
        dto.setToken(result.get().getToken());
        response.setBody(dto);
        response.setCode(SiatResponseCodes.SUCCESS);
        response.setMessage("");

        log.info("Respuesta del Servicio de Impuesto para login: {}, respuesta: {}",request.getLogin(), soapResponse);

        return Optional.of(response) ;
    }

 */

}
