package bo.com.luminia.sflbilling.msbatch.service;

import bo.com.luminia.sflbilling.msbatch.repository.CufdRepository;
import bo.com.luminia.sflbilling.msbatch.repository.CuisRepository;
import bo.com.luminia.sflbilling.msbatch.repository.PointSaleRepository;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.Cufd;
import bo.com.luminia.sflbilling.domain.Cuis;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.SyncRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.SoapUtil;
import bo.gob.impuestos.sfe.code.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SyncCodeService {

    private final Environment environment;
    private final SoapUtil soapUtil;

    private final PointSaleRepository pointSaleRepository;
    private final CuisRepository cuisRepository;
    private final CufdRepository cufdRepository;

    /**
     * Método que sincroniza los códigos CUIS y CUFD de una empresa.
     *
     * @param company Empresa.
     * @return
     */
    public SyncRes synchronize(Company company) {
        // Obtiene el TOKEN de autorización del SIAT.
        String token = company.getToken();

        // Sincroniza los códigos CUIS.
        log.debug("   Sincronizando Cuis para compañía id={} nombre={}", company.getId(), company.getName());
        SyncRes response = this.synchronizeCuis(company, token);
        if (!response.getResponse()) {
            log.error("   Falla Sincronizando Cuis para compañía id={} nombre={}", company.getId(), company.getName());
            return response;
        }

        // Sincroniza los códigos CUFD.
        log.debug("   Sincronizando Cufd para compañía id={} nombre={}", company.getId(), company.getName());
        response = this.synchronizeCufd(company, token);
        if (!response.getResponse()) {
            log.debug("   Falla Sincronizando Cufd para compañía id={} nombre={}", company.getId(), company.getName());
            return response;
        }

        return response;
    }

    /**
     * Método que sincroniza los códigos CUIS y CUFD de una empresa.
     *
     * @param company Empresa.
     * @return
     */
    public SyncRes synchronizeIndividual(Company company) {

        // Obtiene el TOKEN de autorización del SIAT.
        String token = company.getToken();

        // Sincroniza los códigos CUIS.
        SyncRes response = this.synchronizeCuis(company, token);
        if (!response.getResponse()) {
            return response;
        }

        // Sincroniza los códigos CUFD.
        response = this.synchronizeCufdIndividual(company, token);
        if (!response.getResponse()) {
            return response;
        }

        return response;
    }

    /**
     * Método que realiza la sincronización de los códigos CUIS.
     *
     * @param company Entidad empresa.
     * @param token   Token de autorización del SIAT.
     * @return
     */
    @Transactional
    public SyncRes synchronizeCuis(Company company, String token) {

        // Obtiene la lista de puntos de venta activos.
        List listPointSale = pointSaleRepository.findAllByCompanyIdAndActive(company.getId());
        if (listPointSale.size() <= 0) {
            return new SyncRes(false, "No existen puntos de venta para la empresa.");
        }

        // Mapea la lista de puntos de venta para la solicitud.
        List<SolicitudListaCuisDto> listDataRequest = ((ArrayList<Object[]>) listPointSale).stream().map(x -> {
            SolicitudListaCuisDto obj = new SolicitudListaCuisDto();
            obj.setCodigoSucursal((int) x[1]);
            obj.setCodigoPuntoVenta((int) x[3]);
            return obj;
        }).collect(Collectors.toList());

        // Crea la solicitud masiva de códigos CUIS.
        SolicitudCuisMasivoSistemas request = new SolicitudCuisMasivoSistemas();
        request.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
        request.setCodigoModalidad(company.getModalitySiat().getKey());
        request.setCodigoSistema(company.getSystemCode());
        request.setNit(company.getNit());
        request.getDatosSolicitud().addAll(listDataRequest);

        log.info("Solicita códigos CUIS al servicio SOAP del SIAT request: {}", request);
        ServicioFacturacionCodigos service = soapUtil.getCodeService(token);
        RespuestaCuisMasivo response;
        try {
            response = service.cuisMasivo(request);
        } catch (Exception e) {
            log.error("Error sincronizando cuid: {}", e.getMessage());
            if (e.getMessage().contains("API KEY NO VALIDO")) {
                return new SyncRes(false, "API KEY NO VALIDO");
            } else {
                return new SyncRes(false, "Error consultando al servicio SIAT");
            }
        }
        log.info("Respuesta del servicio SOAP del SIAT response: {}", response);

        // Verifica si la transacción es correcta.
        if (!response.isTransaccion()) {
            return new SyncRes(false, response.getMensajesList().get(0).getDescripcion());
        }

        // Itera la lista de códigos CUIS.
        for (RespuestaListaRegistroCuisSoapDto objRes : response.getListaRespuestasCuis()) {
            // Verifica si el resultado es valido.
            if (objRes.isTransaccion() || (!objRes.isTransaccion() && null != objRes.getCodigo() && !objRes.getCodigo().isEmpty())) {
                // Obtiene el código CUIS vigente de la sucursal y punto de venta.
                Optional<Cuis> objCuis = cuisRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(objRes.getCodigoPuntoVenta(), objRes.getCodigoSucursal(), company.getId());
                if (!objCuis.isPresent()) {
                    // Registro del código CUIS.
                    this.createCuis(objRes, company.getId());
                } else {
                    if (!objCuis.get().getCuis().equals(objRes.getCodigo())) {

                        // Baja del cuis y cufd activo asociado.
                        Optional<Cufd> objCufd = cufdRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(objRes.getCodigoPuntoVenta(), objRes.getCodigoSucursal(), company.getId());
                        if (objCufd.isPresent()) {
                            objCufd.get().setActive(false);
                            cufdRepository.save(objCufd.get());
                        }
                        objCuis.get().setActive(false);
                        cuisRepository.save(objCuis.get());

                        // Registro del código CUIS.
                        this.createCuis(objRes, company.getId());
                    }
                }
            } else {
                return new SyncRes(false, objRes.getMensajeServicioList().get(0).getDescripcion());
            }
        }
        return new SyncRes(true, "La sincronización terminó correctamente.");
    }

    /**
     * Método para la sincronización de los códigos CUFD.
     *
     * @param company Entidad empresa.
     * @param token   Token de autorización del SIAT.
     * @return
     */
    @Transactional
    public SyncRes synchronizeCufd(Company company, String token) {

        // Obtiene la lista de puntos de venta y CUIS.
        List listCuis = cuisRepository.findAllByCompanyIdAndActive(company.getId());
        if (listCuis.size() <= 0) {
            log.error("   Error sincronizando cuf, sin cuis para id={}, company={}, business={} ",
                company.getId(),
                company.getName(),
                company.getBusinessName()
            );
            return new SyncRes(false, "No existen CUIS activos para la empresa.");
        }

        // Mapea la lista de puntos de venta y CUIS para la solicitud.
        List<SolicitudListaCufdDto> listDataRequest = ((ArrayList<Object[]>) listCuis).stream().map(x -> {
            SolicitudListaCufdDto obj = new SolicitudListaCufdDto();
            obj.setCodigoSucursal((int) x[1]);
            obj.setCodigoPuntoVenta((int) x[3]);
            obj.setCuis((String) x[5]);
            return obj;
        }).collect(Collectors.toList());
        // Crea la solicitud masiva de códigos CUFD.
        SolicitudCufdMasivo request = new SolicitudCufdMasivo();
        request.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
        request.setCodigoModalidad(company.getModalitySiat().getKey());
        request.setCodigoSistema(company.getSystemCode());
        request.setNit(company.getNit());
        request.getDatosSolicitud().addAll(listDataRequest);

        log.info("Solicita códigos CUFD al servicio SOAP del SIAT request: {}", request);
        ServicioFacturacionCodigos service = soapUtil.getCodeService(token);
        RespuestaCufdMasivo response = service.cufdMasivo(request);
        log.info("Respuesta del servicio SOAP del SIAT response: {}", response);

        // Verifica si la transacción es correcta.
        if (!response.isTransaccion()) {
            log.error("   Error sincronizando cuf con SIAT para id={}, company={}, business={} ",
                company.getId(),
                company.getName(),
                company.getBusinessName()
            );
            //throw new SiatException(response.getMensajesList().get(0).getDescripcion());
            return new SyncRes(false, response.getMensajesList().get(0).getDescripcion());
        }

        // Itera la lista de códigos CUFD.
        for (RespuestaListaRegistroCufdSoapDto objRes : response.getListaRespuestasCufd()) {
            // Verifica si el resultado es valido.
            if (objRes.isTransaccion()) {
                // Obtiene el código CUFD vigente de la sucursal y punto de venta.
                Optional<Cufd> objCufd = cufdRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(objRes.getCodigoPuntoVenta(), objRes.getCodigoSucursal(), company.getId());
                if (!objCufd.isPresent()) {
                    // Registro del código CUFD.
                    this.createCufd(objRes, company.getId());
                } else {
                    if (!objCufd.get().getCuis().equals(objRes.getCodigo())) {
                        objCufd.get().setActive(false);
                        cufdRepository.save(objCufd.get());
                        // Registro del código CUFD.
                        this.createCufd(objRes, company.getId());
                    }
                }
            } else {
                log.error("   Cuf no registrado en respuestas para id={}, company={}, business={} ",
                    company.getId(),
                    company.getName(),
                    company.getBusinessName()
                );
                //throw new SiatException(response.getMensajesList().get(0).getDescripcion());
                return new SyncRes(false, objRes.getMensajeServicioList().get(0).getDescripcion());
            }
        }
        return new SyncRes(true, "La sincronización terminó correctamente.");
    }

    /**
     * Método para la sincronización de los códigos CUFD.
     *
     * @param company Entidad empresa.
     * @param token   Token de autorización del SIAT.
     * @return
     */
    @Transactional
    public SyncRes synchronizeCufdIndividual(Company company, String token) {

        // Obtiene la lista de puntos de venta y CUIS.
        List listCuis = cuisRepository.findAllByCompanyIdAndActive(company.getId());
        if (listCuis.size() <= 0) {
            return new SyncRes(false, "No existen CUIS activos para la empresa.");
        }

        // Mapea la lista de puntos de venta y CUIS para la solicitud.
        List<SolicitudListaCufdDto> listDataRequest = ((ArrayList<Object[]>) listCuis).stream().map(x -> {
            SolicitudListaCufdDto obj = new SolicitudListaCufdDto();
            obj.setCodigoSucursal((int) x[1]);
            obj.setCodigoPuntoVenta((int) x[3]);
            obj.setCuis((String) x[5]);
            return obj;
        }).collect(Collectors.toList());

        for (SolicitudListaCufdDto dataCufd : listDataRequest) {
            // Crea la solicitud de códigos CUFD.
            SolicitudCufd request = new SolicitudCufd();
            request.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
            request.setCodigoModalidad(company.getModalitySiat().getKey());
            request.setCodigoSistema(company.getSystemCode());
            request.setNit(company.getNit());
            request.setCodigoSucursal(dataCufd.getCodigoSucursal());
            request.setCodigoPuntoVenta(new ObjectFactory().createSolicitudCufdCodigoPuntoVenta(dataCufd.getCodigoPuntoVenta()));
            request.setCuis(dataCufd.getCuis());

            log.info("Solicita códigos CUFD al servicio SOAP del SIAT request: {}", request);
            ServicioFacturacionCodigos service = soapUtil.getCodeService(token);
            RespuestaCufd response = service.cufd(request);
            log.info("Respuesta del servicio SOAP del SIAT response: {}", response);

            // Verifica si la transacción es correcta.
            if (!response.isTransaccion()) {
                return new SyncRes(false, "Error al solicitar cufd al SIAT.");
                //throw new SiatException(response.getMensajesList().get(0).getDescripcion());
            }

            // Obtiene el código CUFD vigente de la sucursal y punto de venta.
            Optional<Cufd> objCufd = cufdRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(dataCufd.getCodigoPuntoVenta(), dataCufd.getCodigoSucursal(), company.getId());
            if (!objCufd.isPresent()) {
                // Registro del código CUFD.
                this.createCufdIndividual(response, dataCufd, company.getId());
            } else {
                if (!objCufd.get().getCuis().equals(response.getCodigo())) {
                    objCufd.get().setActive(false);
                    cufdRepository.save(objCufd.get());
                    // Registro del código CUFD.
                    this.createCufdIndividual(response, dataCufd, company.getId());
                }
            }
        }
        return new SyncRes(true, "La sincronización terminó correctamente.");
    }

    /**
     * Método para el registro del código CUIS.
     *
     * @param response  Objeto con los datos del código CUIS.
     * @param companyId Id de la empresa.
     */
    @Transactional
    void createCuis(RespuestaListaRegistroCuisSoapDto response, Long companyId) {
        Cuis entity = new Cuis();
        entity.setCuis(response.getCodigo());
        entity.setStartDate(ZonedDateTime.now());
        entity.setEndDate(response.getFechaVigencia().toGregorianCalendar().toZonedDateTime());
        entity.setActive(true);
        entity.setPointSale(pointSaleRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(response.getCodigoPuntoVenta(), response.getCodigoSucursal(), companyId));
        cuisRepository.save(entity);
    }

    /**
     * Método para el registro del código CUIS.
     *
     * @param response  Objeto con los datos del código CUIS.
     * @param companyId Id de la empresa.
     */
    @Transactional
    void createCufd(RespuestaListaRegistroCufdSoapDto response, Long companyId) {
        Cufd entity = new Cufd();
        entity.setCufd(response.getCodigo());
        entity.setControlCode(response.getCodigoControl());
        entity.setAddress(response.getDireccion());
        entity.setStartDate(ZonedDateTime.now());
        entity.setEndDate(response.getFechaVigencia().toGregorianCalendar().toZonedDateTime());
        entity.setActive(true);
        entity.setCuis(cuisRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(response.getCodigoPuntoVenta(), response.getCodigoSucursal(), companyId).get());
        cufdRepository.save(entity);
    }

    /**
     * Método para el registro del código CUIS.
     *
     * @param response  Objeto con los datos del código CUIS.
     * @param companyId Id de la empresa.
     */
    @Transactional
    void createCufdIndividual(RespuestaCufd response, SolicitudListaCufdDto dataCufd, Long companyId) {
        Cufd entity = new Cufd();
        entity.setCufd(response.getCodigo());
        entity.setControlCode(response.getCodigoControl());
        entity.setAddress(response.getDireccion());
        entity.setStartDate(ZonedDateTime.now());
        entity.setEndDate(response.getFechaVigencia().toGregorianCalendar().toZonedDateTime());
        entity.setActive(true);
        entity.setCuis(cuisRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(dataCufd.getCodigoPuntoVenta(), dataCufd.getCodigoSucursal(), companyId).get());
        cufdRepository.save(entity);
    }
}
