package bo.com.luminia.sflbilling.msbatch.service.sync.base;

import bo.com.luminia.sflbilling.msbatch.service.dto.RequestSync;
import bo.gob.impuestos.sfe.synchronization.ObjectFactory;
import bo.gob.impuestos.sfe.synchronization.SolicitudSincronizacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSynService<R> {

    protected abstract Optional<R> callSiatWsdl(SolicitudSincronizacion request, String token);

    protected SolicitudSincronizacion createRequest(RequestSync requestSync) {

        SolicitudSincronizacion request = new SolicitudSincronizacion();
        request.setCodigoAmbiente(requestSync.getEnvironmentSiat());
        request.setCodigoSistema(requestSync.getSystemCode());
        request.setCuis(requestSync.getCuis());
        request.setNit(requestSync.getNit());
        request.setCodigoPuntoVenta((new ObjectFactory().createSolicitudSincronizacionCodigoPuntoVenta(requestSync.getPointSaleSiat())));
        request.setCodigoSucursal(requestSync.getBranchOfficeSiat());

        return request;
    }
}
