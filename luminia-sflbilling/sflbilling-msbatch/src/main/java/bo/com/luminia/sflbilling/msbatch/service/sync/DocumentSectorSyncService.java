package bo.com.luminia.sflbilling.msbatch.service.sync;

import bo.com.luminia.sflbilling.msbatch.repository.DocumentSectorRepository;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msbatch.service.sync.base.BaseSynService;
import bo.com.luminia.sflbilling.msbatch.service.sync.base.IBaseSync;
import bo.com.luminia.sflbilling.domain.DocumentSector;
import bo.com.luminia.sflbilling.msbatch.service.dto.RequestSync;
import bo.com.luminia.sflbilling.msbatch.service.dto.ResponseSync;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.SoapUtil;
import bo.gob.impuestos.sfe.synchronization.RespuestaListaActividadesDocumentoSector;
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
public class DocumentSectorSyncService extends BaseSynService<RespuestaListaActividadesDocumentoSector> implements IBaseSync {

    private final SoapUtil soapUtil;
    private final DocumentSectorRepository documentSectorRepository;

    @Override
    protected Optional<RespuestaListaActividadesDocumentoSector> callSiatWsdl(SolicitudSincronizacion request, String token) {
        log.info("Solicita sincronizar Documento Sector al servicio SOAP del SIAT request: {}", request);

        ServicioFacturacionSincronizacion service = soapUtil.getSyncService(token);
        RespuestaListaActividadesDocumentoSector response = service.sincronizarListaActividadesDocumentoSector(request);

        log.info("Respuesta del servicio SOAP del SIAT response: {}", response);
        return Optional.of(response);
    }

    @Override
    @Transactional
    public ResponseSync synchronize(RequestSync requestSync) {
        // Realiza la llamada al servicio SOAP para obtener los parámetros.
        Optional<RespuestaListaActividadesDocumentoSector> result = this.callSiatWsdl(createRequest(requestSync), requestSync.getToken());
        if (result.isPresent()) {
            RespuestaListaActividadesDocumentoSector response = result.get();

            // Verifica si la transacción es correcta.
            if (!response.isTransaccion()) {
                return new ResponseSync(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion(), null);
            }

            // Mapea la lista de resultados en la entidad de base de datos.
            List<DocumentSector> listSiat = response.getListaActividadesDocumentoSector().stream().map(x -> {
                DocumentSector obj = new DocumentSector();
                obj.setSiatId(x.getCodigoDocumentoSector());
                obj.setActivityCode(Integer.parseInt(x.getCodigoActividad()));
                obj.setDocumentSectorType(x.getTipoDocumentoSector());
                return obj;
            }).collect(Collectors.toList());

            // Obtiene la lista de parámetros de la BD por empresa.
            List<DocumentSector> listFromDb = documentSectorRepository.findAllByCompanyIdAndActiveIsTrue(requestSync.getCompanyId());

            // Itera la lista de la BD y verifica si existen cambios.
            for (DocumentSector objFromDb : listFromDb) {
                Optional<DocumentSector> objSiat = listSiat.stream().filter(x -> x.getSiatId().equals(objFromDb.getSiatId())).findFirst();
                if (objSiat.isPresent()) {
                    if (!objFromDb.getDocumentSectorType().equals(objSiat.get().getDocumentSectorType())) {
                        objFromDb.setDocumentSectorType(objSiat.get().getDocumentSectorType());
                        documentSectorRepository.save(objFromDb);
                    }

                    if (!objFromDb.getActivityCode().equals(objSiat.get().getActivityCode())) {
                        objFromDb.setActivityCode(objSiat.get().getActivityCode());
                        documentSectorRepository.save(objFromDb);
                    }

                } else {
                    objFromDb.setActive(false);
                    documentSectorRepository.save(objFromDb);
                }
            }

            // Itera la lista del servicio SOAP y verifica si existe.
            for (DocumentSector objSiat : listSiat) {
                Optional<DocumentSector> objLocal = listFromDb.stream().filter(x -> x.getSiatId().equals(objSiat.getSiatId())).findFirst();
                if (!objLocal.isPresent()) {
                    objSiat.setCompanyId(requestSync.getCompanyId());
                    objSiat.setActive(true);
                    documentSectorRepository.save(objSiat);
                }
            }
        }

        return new ResponseSync(SiatResponseCodes.SUCCESS, "", null);
    }

}
