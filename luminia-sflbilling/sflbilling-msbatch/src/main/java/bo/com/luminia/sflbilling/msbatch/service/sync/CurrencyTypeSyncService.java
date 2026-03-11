package bo.com.luminia.sflbilling.msbatch.service.sync;

import bo.com.luminia.sflbilling.msbatch.repository.CurrencyTypeRepository;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msbatch.service.sync.base.BaseSynService;
import bo.com.luminia.sflbilling.msbatch.service.sync.base.IBaseSync;
import bo.com.luminia.sflbilling.domain.CurrencyType;
import bo.com.luminia.sflbilling.msbatch.service.dto.RequestSync;
import bo.com.luminia.sflbilling.msbatch.service.dto.ResponseSync;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.SoapUtil;
import bo.gob.impuestos.sfe.synchronization.RespuestaListaParametricas;
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
public class CurrencyTypeSyncService extends BaseSynService<RespuestaListaParametricas> implements IBaseSync {

    private final SoapUtil soapUtil;
    private final CurrencyTypeRepository currencyTypeRepository;

    @Override
    protected Optional<RespuestaListaParametricas> callSiatWsdl(SolicitudSincronizacion request, String token) {
        log.info("Solicita sincronizar Tipo Moneda al servicio SOAP del SIAT request: {}", request);

        ServicioFacturacionSincronizacion service = soapUtil.getSyncService(token);
        RespuestaListaParametricas response = service.sincronizarParametricaTipoMoneda(request);

        log.info("Respuesta del servicio SOAP del SIAT response: {}", response);
        return Optional.of(response);
    }

    @Transactional
    @Override
    public ResponseSync synchronize(RequestSync requestSync) {
        // Realiza la llamada al servicio SOAP para obtener los parámetros.
        Optional<RespuestaListaParametricas> result = this.callSiatWsdl(createRequest(requestSync), requestSync.getToken());
        if (result.isPresent()) {
            RespuestaListaParametricas response = result.get();

            // Verifica si la transacción es correcta.
            if (!response.isTransaccion()) {
                return new ResponseSync(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion(), null);
            }

            // Mapea la lista de resultados en la entidad de base de datos.
            List<CurrencyType> listSiat = response.getListaCodigos().stream().map(x -> {
                CurrencyType obj = new CurrencyType();
                obj.setSiatId(x.getCodigoClasificador());
                obj.setDescription(x.getDescripcion());
                return obj;
            }).collect(Collectors.toList());

            // Obtiene la lista de parámetros de la BD por empresa.
            List<CurrencyType> listFromDb = currencyTypeRepository.findAllByCompanyIdAndActiveIsTrue(requestSync.getCompanyId());

            // Itera la lista de la BD y verifica si existen cambios.
            for (CurrencyType objFromDb : listFromDb) {
                Optional<CurrencyType> objSiat = listSiat.stream().filter(x -> x.getSiatId().equals(objFromDb.getSiatId())).findFirst();
                if (objSiat.isPresent()) {
                    if (!objFromDb.getDescription().equals(objSiat.get().getDescription())) {
                        objFromDb.setDescription(objSiat.get().getDescription());
                        currencyTypeRepository.save(objFromDb);
                    }
                } else {
                    objFromDb.setActive(false);
                    currencyTypeRepository.save(objFromDb);
                }
            }

            // Itera la lista del servicio SOAP y verifica si existe.
            for (CurrencyType objSiat : listSiat) {
                Optional<CurrencyType> objLocal = listFromDb.stream().filter(x -> x.getSiatId().equals(objSiat.getSiatId())).findFirst();
                if (!objLocal.isPresent()) {
                    objSiat.setCompanyId(requestSync.getCompanyId());
                    objSiat.setActive(true);
                    currencyTypeRepository.save(objSiat);
                }
            }
        }

        return new ResponseSync(SiatResponseCodes.SUCCESS, "", null);
    }
}
