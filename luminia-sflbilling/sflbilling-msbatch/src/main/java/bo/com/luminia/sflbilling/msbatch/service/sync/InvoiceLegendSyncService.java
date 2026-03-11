package bo.com.luminia.sflbilling.msbatch.service.sync;

import bo.com.luminia.sflbilling.msbatch.repository.InvoiceLegendRepository;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msbatch.service.sync.base.BaseSynService;
import bo.com.luminia.sflbilling.msbatch.service.sync.base.IBaseSync;
import bo.com.luminia.sflbilling.domain.InvoiceLegend;
import bo.com.luminia.sflbilling.msbatch.service.dto.RequestSync;
import bo.com.luminia.sflbilling.msbatch.service.dto.ResponseSync;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.SoapUtil;
import bo.gob.impuestos.sfe.synchronization.RespuestaListaParametricasLeyendas;
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
public class InvoiceLegendSyncService extends BaseSynService<RespuestaListaParametricasLeyendas> implements IBaseSync {

    private final SoapUtil soapUtil;
    private final InvoiceLegendRepository invoiceLegendRepository;

    @Override
    protected Optional<RespuestaListaParametricasLeyendas> callSiatWsdl(SolicitudSincronizacion request, String token) {
        log.info("Solicita sincronizar Leyendas de Facturas al servicio SOAP del SIAT request: {}", request);

        ServicioFacturacionSincronizacion service = soapUtil.getSyncService(token);
        RespuestaListaParametricasLeyendas response = service.sincronizarListaLeyendasFactura(request);

        log.info("Respuesta del servicio SOAP del SIAT response: {}", response);
        return Optional.of(response);
    }

    @Override
    @Transactional
    public ResponseSync synchronize(RequestSync requestSync) {
        // Realiza la llamada al servicio SOAP para obtener los parámetros.
        Optional<RespuestaListaParametricasLeyendas> result = this.callSiatWsdl(this.createRequest(requestSync), requestSync.getToken());
        if (result.isPresent()) {
            RespuestaListaParametricasLeyendas response = result.get();

            // Verifica si la transacción es correcta.
            if (!response.isTransaccion()) {
                return new ResponseSync(SiatResponseCodes.ERROR_MESSAGE_FROM_SIAT, response.getMensajesList().get(0).getDescripcion(), null);
            }

            // Mapea la lista de resultados en la entidad de base de datos.
            List<InvoiceLegend> listSiat = response.getListaLeyendas().stream().map(x -> {
                InvoiceLegend obj = new InvoiceLegend();
                obj.setActivityCode(Integer.parseInt(x.getCodigoActividad()));
                obj.setDescription(x.getDescripcionLeyenda());
                return obj;
            }).collect(Collectors.toList());

            // Obtiene la lista de parámetros de la BD por empresa.
            List<InvoiceLegend> listFromDb = invoiceLegendRepository.findAllByCompanyIdAndActiveIsTrue(requestSync.getCompanyId());

            // Itera la lista de la BD y verifica si existen cambios.
            for (InvoiceLegend objFromDb : listFromDb) {
                Optional<InvoiceLegend> objSiat = listSiat.stream().filter(x -> x.getActivityCode().equals(objFromDb.getActivityCode())
                    && x.getDescription().equals(objFromDb.getDescription())).findFirst();
                if (!objSiat.isPresent()) {
                    objFromDb.setActive(false);
                    invoiceLegendRepository.save(objFromDb);
                }
            }

            // Itera la lista del servicio SOAP y verifica si existe.
            for (InvoiceLegend objSiat : listSiat) {
                Optional<InvoiceLegend> objLocal = listFromDb.stream().filter(x -> x.getActivityCode().equals(objSiat.getActivityCode())
                    && x.getDescription().equals(objSiat.getDescription())).findFirst();
                if (!objLocal.isPresent()) {
                    objSiat.setCompanyId(requestSync.getCompanyId());
                    objSiat.setActive(true);
                    invoiceLegendRepository.save(objSiat);
                }
            }
        }

        return new ResponseSync(SiatResponseCodes.SUCCESS, "", null);
    }
}
