package bo.com.luminia.sflbilling.msbatch.service.sync;

import bo.com.luminia.sflbilling.msbatch.repository.ProductServiceRepository;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msbatch.service.sync.base.BaseSynService;
import bo.com.luminia.sflbilling.msbatch.service.sync.base.IBaseSync;
import bo.com.luminia.sflbilling.domain.ProductService;
import bo.com.luminia.sflbilling.msbatch.service.dto.RequestSync;
import bo.com.luminia.sflbilling.msbatch.service.dto.ResponseSync;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.SoapUtil;
import bo.gob.impuestos.sfe.synchronization.RespuestaListaProductos;
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
public class ProductServiceSyncService extends BaseSynService<RespuestaListaProductos> implements IBaseSync {

    private final SoapUtil soapUtil;
    private final ProductServiceRepository productServiceRepository;

    @Override
    protected Optional<RespuestaListaProductos> callSiatWsdl(SolicitudSincronizacion request, String token) {
        log.info("Solicita sincronizar Productos Servicios al servicio SOAP del SIAT request:{}", request);

        ServicioFacturacionSincronizacion service = soapUtil.getSyncService(token);
        RespuestaListaProductos response = service.sincronizarListaProductosServicios(request);

        log.info("Respuesta del servicio SOAP del SIAT response: {}", response);
        return Optional.of(response);
    }

    @Transactional
    @Override
    public ResponseSync synchronize(RequestSync requestSync) {
        // Realiza la llamada al servicio SOAP para obtener los parámetros.
        Optional<RespuestaListaProductos> result = this.callSiatWsdl(createRequest(requestSync), requestSync.getToken());
        if (result.isPresent()) {
            RespuestaListaProductos response = result.get();

            // Verifica si la transacción es correcta.
            if (!response.isTransaccion()) {
                return new ResponseSync(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion(), null);
            }

            // Mapea la lista de resultados en la entidad de base de datos.
            List<ProductService> listSiat = response.getListaCodigos().stream().map(x -> {
                ProductService obj = new ProductService();
                obj.setSiatId(x.getCodigoProducto().intValue());
                obj.setActivityCode(Integer.parseInt(x.getCodigoActividad()));
                obj.setDescription(x.getDescripcionProducto());
                return obj;
            }).collect(Collectors.toList());

            // Obtiene la lista de parámetros de la BD por empresa.
            List<ProductService> listFromDb = productServiceRepository.findAllByCompanyIdAndActiveIsTrue(requestSync.getCompanyId());

            // Itera la lista de la BD y verifica si existen cambios.
            for (ProductService objFromDb : listFromDb) {
                Optional<ProductService> objSiat = listSiat.stream().filter(x -> x.getSiatId().equals(objFromDb.getSiatId())).findFirst();
                if (objSiat.isPresent()) {
                    if (!objFromDb.getDescription().equals(objSiat.get().getDescription()) || !objFromDb.getActivityCode().equals(objSiat.get().getActivityCode())) {
                        objFromDb.setDescription(objSiat.get().getDescription());
                        objFromDb.setActivityCode(objSiat.get().getActivityCode());
                        productServiceRepository.save(objFromDb);
                    }
                } else {
                    objFromDb.setActive(false);
                    productServiceRepository.save(objFromDb);
                }
            }

            // Itera la lista del servicio SOAP y verifica si existe.
            for (ProductService objSiat : listSiat) {
                Optional<ProductService> objLocal = listFromDb.stream().filter(x -> x.getSiatId().equals(objSiat.getSiatId())).findFirst();
                if (!objLocal.isPresent()) {
                    objSiat.setCompanyId(requestSync.getCompanyId());
                    objSiat.setActive(true);
                    productServiceRepository.save(objSiat);
                }
            }
        }

        return new ResponseSync(SiatResponseCodes.SUCCESS, "", null);
    }
}
