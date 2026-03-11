package bo.com.luminia.sflbilling.msbatch.service.sync;

import bo.com.luminia.sflbilling.msbatch.repository.ActivityRepository;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msbatch.service.sync.base.BaseSynService;
import bo.com.luminia.sflbilling.msbatch.service.sync.base.IBaseSync;
import bo.com.luminia.sflbilling.domain.Activity;
import bo.com.luminia.sflbilling.msbatch.service.dto.RequestSync;
import bo.com.luminia.sflbilling.msbatch.service.dto.ResponseSync;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.SoapUtil;
import bo.gob.impuestos.sfe.synchronization.RespuestaListaActividades;
import bo.gob.impuestos.sfe.synchronization.ServicioFacturacionSincronizacion;
import bo.gob.impuestos.sfe.synchronization.SolicitudSincronizacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ActivitySyncService extends BaseSynService<RespuestaListaActividades> implements IBaseSync {

    private final SoapUtil soapUtil;
    private final ActivityRepository activityRepository;

    protected Optional<RespuestaListaActividades> callSiatWsdl(SolicitudSincronizacion request, String token) {
        log.info("Solicita sincronizar Actividades al servicio SOAP del SIAT request:{}", request);

        ServicioFacturacionSincronizacion service = soapUtil.getSyncService(token);
        RespuestaListaActividades response = service.sincronizarActividades(request);

        log.info("Respuesta del servicio SOAP del SIAT response: {}", response);
        return Optional.of(response);
    }

    @Transactional
    public ResponseSync synchronize(RequestSync requestSync) {
        // Realiza la llamada al servicio SOAP para obtener los parámetros.
        Optional<RespuestaListaActividades> result = this.callSiatWsdl(createRequest(requestSync), requestSync.getToken());
        if (result.isPresent()) {
            RespuestaListaActividades response = result.get();

            // Verifica si la transacción es correcta.
            if (!response.isTransaccion()) {
                return new ResponseSync(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion(), null);
            }

            // Mapea la lista de resultados en la entidad de base de datos.
            List<Activity> listSiat = response.getListaActividades().stream().map(x -> {
                Activity obj = new Activity();
                obj.setSiatId(Integer.parseInt(x.getCodigoCaeb()));
                obj.setActivityType(x.getTipoActividad());
                obj.setDescription(x.getDescripcion());
                return obj;
            }).collect(Collectors.toList());

            // Obtiene la lista de parámetros de la BD por empresa.
            List<Activity> listFromDb = activityRepository.findAllByCompanyIdAndActiveIsTrue(requestSync.getCompanyId());

            // Itera la lista de la BD y verifica si existen cambios.
            for (Activity objFromDb : listFromDb) {
                Optional<Activity> objSiat = listSiat.stream().filter(x -> x.getSiatId().equals(objFromDb.getSiatId())).findFirst();
                if (objSiat.isPresent()) {
                    if (!objFromDb.getDescription().equals(objSiat.get().getDescription()) || !objFromDb.getActivityType().equals(objSiat.get().getActivityType())) {
                        objFromDb.setDescription(objSiat.get().getDescription());
                        objFromDb.setActivityType(objSiat.get().getActivityType());
                        activityRepository.save(objFromDb);
                    }
                } else {
                    objFromDb.setActive(false);
                    activityRepository.save(objFromDb);
                }
            }

            // Itera la lista del servicio SOAP y verifica si existe.
            for (Activity objSiat : listSiat) {
                Optional<Activity> objLocal = listFromDb.stream().filter(x -> x.getSiatId().equals(objSiat.getSiatId())).findFirst();
                if (!objLocal.isPresent()) {
                    objSiat.setCompanyId(requestSync.getCompanyId());
                    objSiat.setActive(true);
                    activityRepository.save(objSiat);
                }
            }
        }

        return new ResponseSync(SiatResponseCodes.SUCCESS, "", null);
    }
}
