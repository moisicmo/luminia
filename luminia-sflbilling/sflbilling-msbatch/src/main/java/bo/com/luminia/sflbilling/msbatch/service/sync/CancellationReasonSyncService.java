package bo.com.luminia.sflbilling.msbatch.service.sync;

import bo.com.luminia.sflbilling.msbatch.repository.CancellationReasonRepository;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msbatch.service.sync.base.BaseSynService;
import bo.com.luminia.sflbilling.msbatch.service.sync.base.IBaseSync;
import bo.com.luminia.sflbilling.domain.CancellationReason;
import bo.com.luminia.sflbilling.msbatch.service.dto.RequestSync;
import bo.com.luminia.sflbilling.msbatch.service.dto.ResponseSync;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.SoapUtil;
import bo.gob.impuestos.sfe.synchronization.RespuestaListaParametricas;
import bo.gob.impuestos.sfe.synchronization.ServicioFacturacionSincronizacion;
import bo.gob.impuestos.sfe.synchronization.SolicitudSincronizacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CancellationReasonSyncService extends BaseSynService<RespuestaListaParametricas> implements IBaseSync {

    private final SoapUtil soapUtil;
    private final CancellationReasonRepository cancellationReasonRepository;

    @Override
    protected Optional<RespuestaListaParametricas> callSiatWsdl(SolicitudSincronizacion request, String token) {
        log.info("Solicita sincronizar Leyendas de Facturas al servicio SOAP del SIAT request: {}", request);

        ServicioFacturacionSincronizacion service = soapUtil.getSyncService(token);
        RespuestaListaParametricas response = service.sincronizarParametricaMotivoAnulacion(request);

        log.info("Respuesta del servicio SOAP del SIAT response: {}", response);
        return Optional.of(response);
    }

    @Override
    public ResponseSync synchronize(RequestSync requestSync) {
        // Realiza la llamada al servicio SOAP para obtener los parámetros.
        Optional<RespuestaListaParametricas> result = this.callSiatWsdl(this.createRequest(requestSync), requestSync.getToken());
        if (result.isPresent()) {
            RespuestaListaParametricas response = result.get();

            // Verifica si la transacción es correcta.
            if (!response.isTransaccion()) {
                return new ResponseSync(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion(), null);
            }

            // Mapea la lista de resultados en la entidad de base de datos.
            List<CancellationReason> listSiat = response.getListaCodigos().stream().map(x -> {
                CancellationReason obj = new CancellationReason();
                obj.setSiatId(x.getCodigoClasificador());
                obj.setDescription(x.getDescripcion());
                return obj;
            }).collect(Collectors.toList());

            // Obtiene la lista de parámetros de la BD por empresa.
            List<CancellationReason> listFromDb = cancellationReasonRepository.findAllByCompanyIdAndActiveIsTrue(requestSync.getCompanyId());

            // Itera la lista de la BD y verifica si existen cambios.
            for (CancellationReason objFromDb : listFromDb) {
                Optional<CancellationReason> objSiat = listSiat.stream().filter(x -> x.getSiatId().equals(objFromDb.getSiatId())).findFirst();
                if (objSiat.isPresent()) {
                    if (!objFromDb.getDescription().equals(objSiat.get().getDescription())) {
                        objFromDb.setDescription(objSiat.get().getDescription());
                        cancellationReasonRepository.save(objFromDb);
                    }
                } else {
                    objFromDb.setActive(false);
                    cancellationReasonRepository.save(objFromDb);
                }
            }

            // Itera la lista del servicio SOAP y verifica si existe.
            for (CancellationReason objSiat : listSiat) {
                Optional<CancellationReason> objLocal = listFromDb.stream().filter(x -> x.getSiatId().equals(objSiat.getSiatId())).findFirst();
                if (!objLocal.isPresent()) {
                    objSiat.setCompanyId(requestSync.getCompanyId());
                    objSiat.setActive(true);
                    cancellationReasonRepository.save(objSiat);
                }
            }
        }

        return new ResponseSync(SiatResponseCodes.SUCCESS, "", null);
    }
}
