package bo.com.luminia.sflbilling.msbatch.service;


import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msbatch.config.ApplicationProperties;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.SoapUtil;
import bo.gob.impuestos.sfe.synchronization.MensajeServicio;
import bo.gob.impuestos.sfe.synchronization.RespuestaComunicacion;
import bo.gob.impuestos.sfe.synchronization.ServicioFacturacionSincronizacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public abstract class ConnectivityBase {

    private final int SIAT_NO_OK = 2;


    /**
     * Método para verificar la conexión del Siat, sntralizado para todos los servicios
     *
     * @param token
     * @param environment
     * @param soapUtil
     * @return
     */
    public int checkConnectivity(String token, Environment environment, SoapUtil soapUtil) {
        int checkMethod = Integer.parseInt(environment.getProperty(ApplicationProperties.CONNECTIVITY_CHECK_METHOD));
        try {

            switch (checkMethod) {
                case 2:
                    return checkConnectivityCheckMethod2(environment);
                case 3:
                    return checkConnectivityCheckMethod3(token, environment, soapUtil);
                default: //1 o el default
                    return checkConnectivityCheckMethod1(token, soapUtil);
            }

        } catch (Exception e) {
            log.error("Error verificando conexion{}: ", e);
            return SIAT_NO_OK;
        }
    }


    /**
     * Método de conexión en el que se intenta llamar al método de impuestos para verificar conexión
     *
     * @param token token del aplicativo
     * @return SIAT_OK | SIAT_NO_OK
     */
    public int checkConnectivityCheckMethod1(String token, SoapUtil soapUtil) {
        log.debug("Verificar conexión por método 1: verificarComunicación ws SIAT");
        try {

            ServicioFacturacionSincronizacion serviceConectividad = soapUtil.getSyncService(token);
            RespuestaComunicacion response = serviceConectividad.verificarComunicacion();

            if (response.getMensajesList().isEmpty())
                return SIAT_NO_OK; //no hay conectividad

            for (MensajeServicio ms : response.getMensajesList()) {
                return ms.getCodigo().intValue(); // es el codigo que devuelve el SIAT
            }

            return SIAT_NO_OK;
        } catch (Exception ex) {
            //ex.printStackTrace();
            log.error("Error on method verificarComunicacion: {}", ex.getMessage());
            return SIAT_NO_OK;
        }
    }


    /**
     * Método de conexión 2, mediante sockets
     *
     * @return
     */
    public int checkConnectivityCheckMethod2(Environment environment) {
        log.debug("Verificar conexión por método 2: Socket");

        String host = environment.getProperty(ApplicationProperties.CONNECTIVITY_HOST);
        int port = Integer.parseInt(environment.getProperty(ApplicationProperties.CONNECTIVITY_PORT));
        int timeout = Integer.parseInt(environment.getProperty(ApplicationProperties.CONNECTIVITY_TIMEOUT));

        return (pingHost(host, port, timeout)) ? SiatResponseCodes.SIAT_HAS_CONNECTIVITY : SIAT_NO_OK;
    }

    /**
     * Método de conexión 3, utilizando tanto el ws del siat como el socket
     *
     * @return
     */
    private int checkConnectivityCheckMethod3(String token, Environment environment, SoapUtil soapUtil) {
        log.debug("Verificar conexión por método 3: Socket y WS SIAT");
        boolean allOk = checkConnectivityCheckMethod2(environment) != SIAT_NO_OK &&
            checkConnectivityCheckMethod1(token, soapUtil) != SIAT_NO_OK;
        return allOk ? SiatResponseCodes.SIAT_HAS_CONNECTIVITY : SIAT_NO_OK;
    }


    /**
     * Verifica la conexión a través de sockets
     *
     * @param host
     * @param port
     * @param timeout
     * @return
     */
    private boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }


}
