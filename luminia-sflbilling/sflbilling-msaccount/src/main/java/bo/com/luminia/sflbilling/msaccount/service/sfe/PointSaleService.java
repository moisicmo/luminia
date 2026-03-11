package bo.com.luminia.sflbilling.msaccount.service.sfe;

import bo.com.luminia.sflbilling.msaccount.repository.*;
import bo.com.luminia.sflbilling.domain.*;
import bo.com.luminia.sflbilling.msaccount.repository.*;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.NotFoundAlertException;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec.DefaultTransactionException;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.admin.PointSaleReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.PointSaleCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.PointSaleUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.CrudRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.PointSaleRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.util.SoapUtil;
import bo.gob.impuestos.sfe.operation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class PointSaleService implements BaseCrudService<PointSaleCreateReq, PointSaleUpdateReq, CrudRes> {

    private final SoapUtil soapUtil;
    private final PointSaleRepository pointSaleRepository;
    private final BranchOfficeRepository branchOfficeRepository;
    private final PointSaleTypeRepository pointSaleTypeRepository;
    private final CuisRepository cuisRepository;
    private final CufdRepository cufdRepository;

    @Override
    public CrudRes create(PointSaleCreateReq request) {
        CrudRes response = new CrudRes();
        response.setCode(ResponseCodes.WARNING);
        response.setMessage(ResponseMessages.WARNING_OPERACION_NO_EJECUTADA);

        if (request.getBranchOfficeId() != null) {
            Optional<BranchOffice> branchOfficeFromDb = branchOfficeRepository.findById(request.getBranchOfficeId());
            if (!branchOfficeFromDb.isPresent()) {
                throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_SUCURSAL_NO_ENCONTRADA, request.getBranchOfficeId()),
                    ErrorEntities.POINT_SALE,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
                //return createResponse(ResponseCodes.ERROR, String.format(ResponseMessages.ERROR_SUCURSAL_NO_ENCONTRADA, request.getBranchOfficeId()), null);
            }

            PointSale entity = new PointSale();

            // Obtiene el tipo de punto de venta.
            if (request.getPointSaleTypeId() != null) {
                Optional<PointSaleType> fromDb = pointSaleTypeRepository.findById(request.getPointSaleTypeId());
                if (fromDb.isPresent()) {
                    entity.setPointSaleType(fromDb.get());
                } else {
                    throw new DefaultTransactionException(
                        String.format(ResponseMessages.ERROR_NO_EXISTE_POINT_SALE_TYPE, request.getPointSaleTypeId()),
                        ErrorEntities.POINT_SALE_TYPE,
                        ErrorKeys.ERR_RECORD_NOT_FOUND);
                    //return createResponse(ResponseCodes.ERROR, String.format(ResponseMessages.ERROR_NO_EXISTE_POINT_SALE_TYPE, request.getPointSaleTypeId()), null);
                }
            } else {
                entity.setPointSaleType(null);
            }

            // Verifica si el punto de venta debe crearse en SIAT.
            if (request.getPointSaleSiatId() != null) {
                entity.setPointSaleSiatId(request.getPointSaleSiatId());
            } else {
                // Registra del punto de venta en el SIAT.
                response = this.registerPointSaleSiat(branchOfficeFromDb.get(), entity.getPointSaleType(), request);
                if (!response.getCode().equals(ResponseCodes.SUCCESS))
                    return response;
                entity.setPointSaleSiatId((Integer) response.getBody());
            }

            List<PointSale> pointSaleFromDb = pointSaleRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(request.getPointSaleSiatId()
                , branchOfficeFromDb.get().getBranchOfficeSiatId(), branchOfficeFromDb.get().getCompany().getId());

            if (!pointSaleFromDb.isEmpty()) {
                throw new DefaultTransactionException(
                    String.format("Ya existe un Punto de Venta: %d, para el La sucursal: %d", request.getPointSaleSiatId(), request.getBranchOfficeId()),
                    ErrorEntities.POINT_SALE,
                    ErrorKeys.ERR_RECORD_ALREADY_EXISTS);

            }

            entity.setName(request.getName());
            entity.setDescription(request.getDescription());
            entity.setBranchOffice(branchOfficeFromDb.get());
            entity.setActive(request.getActive() != null ? request.getActive() : true);

            pointSaleRepository.save(entity);
            pointSaleRepository.flush();

            response.setCode(ResponseCodes.SUCCESS);
            response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
            response.setBody(new Object() {
                public final long id = entity.getId();
            });
        }
        return response;
    }

    @Override
    public CrudRes update(PointSaleUpdateReq request) {
        CrudRes response = new CrudRes();

        if (request.getId() != null) {
            Optional<PointSale> result = pointSaleRepository.findById(request.getId());
            if (result.isPresent()) {
                PointSale entity = result.get();

                if (request.getName() != null) {
                    entity.setName(request.getName());
                }
                if (request.getDescription() != null) {
                    entity.setDescription(request.getDescription());
                }
                if (request.getActive() != null) {
                    entity.setActive(request.getActive());
                }

                pointSaleRepository.save(entity);
                pointSaleRepository.flush();

                response = createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_ACTUALIZACION_EXITOSA,
                    new Object() {
                        public final long id = entity.getId();
                    });
            } else {
                throw new NotFoundAlertException(ErrorKeys.ERR_RECORD_NOT_FOUND, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO);
                //response = createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
        } else {
            throw new DefaultTransactionException(
                ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID,
                ErrorEntities.POINT_SALE,
                ErrorKeys.ERR_REQUIRED_VALUE);
            //response = createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID, null);
        }
        return response;
    }

    public CrudRes close(Long id) {
        if (id != null) {
            Optional<PointSale> pointSale = pointSaleRepository.findById(id);
            if (!pointSale.isPresent()) {
                throw new RecordNotFoundException();
            }
            PointSale entity = pointSale.get();

            // Cierre del punto de venta en el SIAT.
            CrudRes response = this.closePointSaleSiat(entity.getBranchOffice(), entity);
            if (!response.getCode().equals(ResponseCodes.SUCCESS))
                return response;

            // Desactiva los códigos asociados.
            this.disableCodesCuisCufd(entity);

            entity.setActive(false);
            pointSaleRepository.save(entity);
            pointSaleRepository.flush();

            return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, null);
        }
        throw new DefaultTransactionException(
            ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID,
            ErrorEntities.POINT_SALE,
            ErrorKeys.ERR_REQUIRED_VALUE);
        //return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID, null);
    }

    public CrudRes closeOperation(Long id) {
        if (id != null) {
            Optional<PointSale> pointSale = pointSaleRepository.findById(id);
            if (!pointSale.isPresent()) {
                return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
            PointSale entity = pointSale.get();

            // Cierre de operaciones del punto de venta en el SIAT.
            CrudRes response = this.closeOperationPointSaleSiat(entity.getBranchOffice(), entity);
            if (!response.getCode().equals(ResponseCodes.SUCCESS))
                return response;

            // Desactiva los códigos asociados.
            this.disableCodesCuisCufd(entity);

            return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, null);
        }
        return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_DEBE_ESPECIFICAR_ID, null);
    }

    public CrudRes get(Long id) {
        if (id != null) {
            Optional<PointSale> entity = pointSaleRepository.findById(id);
            if (entity.isPresent()) {
                PointSaleRes body = new PointSaleRes();
                BeanUtils.copyProperties(entity.get(), body);
                if (entity.get().getPointSaleType() != null)
                    body.setPointSaleTypeId(entity.get().getPointSaleType().getId());
                body.setBranchOfficeId(entity.get().getBranchOffice().getId());
                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, body);
            } else {
                return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
            }
        } else {
            return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
        }
    }

    public CrudRes getAllByBranchOffice(Long branchOfficeId) {
        if (branchOfficeId != null) {
            List<PointSale> pointSaleList = pointSaleRepository.findAllByCompanyId(branchOfficeId);
            if (!pointSaleList.isEmpty()) {
                List<PointSaleRes> toBody = new ArrayList<>();
                for (PointSale pointSale : pointSaleList) {
                    PointSaleRes body = new PointSaleRes();
                    BeanUtils.copyProperties(pointSale, body);
                    if (pointSale.getPointSaleType() != null) {
                        body.setPointSaleTypeId(pointSale.getPointSaleType().getId());
                        body.setPointSaleType(pointSale.getPointSaleType().getDescription());
                    }
                    body.setBranchOfficeId(pointSale.getBranchOffice().getId());
                    toBody.add(body);
                }
                return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, toBody);
            } else {
                return createResponse(ResponseCodes.WARNING, ResponseMessages.WARNING_LISTA_VACIA, null);
            }
        } else {
            return createResponse(ResponseCodes.ERROR, ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO, null);
        }
    }

    public CrudRes getAll() {
        List<PointSale> pointSaleList = pointSaleRepository.findAll();
        if (!pointSaleList.isEmpty()) {
            List<PointSaleRes> toBody = new ArrayList<>();
            for (PointSale pointSale : pointSaleList) {
                PointSaleRes body = new PointSaleRes();
                BeanUtils.copyProperties(pointSale, body);
                if (pointSale.getPointSaleType() != null) {
                    body.setPointSaleTypeId(pointSale.getPointSaleType().getId());
                    body.setPointSaleType(pointSale.getPointSaleType().getDescription());
                }
                body.setBranchOfficeId(pointSale.getBranchOffice().getId());
                toBody.add(body);
            }
            return createResponse(ResponseCodes.SUCCESS, ResponseMessages.SUCCESS_OPERACION_EXITOSA, toBody);
        } else {
            return createResponse(ResponseCodes.WARNING, ResponseMessages.WARNING_LISTA_VACIA, null);
        }
    }

    @Override
    public CrudRes delete(Long companyId, Integer id) {
        throw new NotImplementedException("Metodo no soportado. Consulte al administrador");
    }

    @Override
    public CrudRes get(Long companyId, Integer id) {
        throw new NotImplementedException("Metodo no soportado. Consulte al administrador");
    }

    public CrudRes registerPointSaleSiat(BranchOffice branchOffice, PointSaleType pointSaleType, PointSaleCreateReq pointSaleCreateReq) {
        CrudRes result = new CrudRes();
        Company company = branchOffice.getCompany();

        Optional<Cuis> cuisFromDb = this.obtainCuisActive(company.getId(), branchOffice.getBranchOfficeSiatId(), 0);
        if (!cuisFromDb.isPresent()) {
            result.setCode(ResponseCodes.WARNING);
            result.setMessage("Codigo Cuis no encontrado o no ha sido generado");
            return result;
        }

        SolicitudRegistroPuntoVenta request = new SolicitudRegistroPuntoVenta();
        request.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
        request.setCodigoModalidad(company.getModalitySiat().getKey());
        request.setCodigoSistema(company.getSystemCode());
        request.setNit(company.getNit());
        request.setCodigoSucursal(branchOffice.getBranchOfficeSiatId());
        request.setCodigoTipoPuntoVenta(pointSaleType.getSiatId());
        request.setCuis(cuisFromDb.get().getCuis());
        request.setNombrePuntoVenta(pointSaleCreateReq.getName());
        request.setDescripcion(pointSaleCreateReq.getDescription());

        ServicioFacturacionOperaciones service = soapUtil.getOperationService(company.getToken());
        RespuestaRegistroPuntoVenta response = service.registroPuntoVenta(request);

        log.debug("Resultado registro punto de venta: {}", response);

        if (!response.isTransaccion()) {
            result.setCode(ResponseCodes.WARNING);
            result.setMessage(response.getMensajesList().get(0).getDescripcion());
            return result;
        }

        result.setCode(ResponseCodes.SUCCESS);
        result.setBody(response.getCodigoPuntoVenta());
        return result;
    }

    private CrudRes closePointSaleSiat(BranchOffice branchOffice, PointSale pointSale) {
        CrudRes result = new CrudRes();
        Company company = branchOffice.getCompany();

        Optional<Cuis> cuisFromDb = this.obtainCuisActive(company.getId(), branchOffice.getBranchOfficeSiatId(), 0);
        if (!cuisFromDb.isPresent()) {
            result.setCode(ResponseCodes.WARNING);
            result.setMessage("Codigo Cuis no encontrado o no ha sido generado");
            return result;
        }

        SolicitudCierrePuntoVenta request = new SolicitudCierrePuntoVenta();
        request.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
        request.setCodigoSistema(company.getSystemCode());
        request.setNit(company.getNit());
        request.setCodigoSucursal(branchOffice.getBranchOfficeSiatId());
        request.setCodigoPuntoVenta(pointSale.getPointSaleSiatId());
        request.setCuis(cuisFromDb.get().getCuis());

        ServicioFacturacionOperaciones service = soapUtil.getOperationService(company.getToken());
        RespuestaCierrePuntoVenta response = service.cierrePuntoVenta(request);

        log.debug("Resultado cierre del punto de venta: {}", response);

        if (!response.isTransaccion()) {
            result.setCode(ResponseCodes.WARNING);
            result.setMessage(response.getMensajesList().get(0).getDescripcion());
            return result;
        }
        result.setCode(ResponseCodes.SUCCESS);
        return result;
    }

    private CrudRes closeOperationPointSaleSiat(BranchOffice branchOffice, PointSale pointSale) {
        CrudRes result = new CrudRes();
        Company company = branchOffice.getCompany();

        Optional<Cuis> cuisFromDb = this.obtainCuisActive(company.getId(), branchOffice.getBranchOfficeSiatId(), pointSale.getPointSaleSiatId());
        if (!cuisFromDb.isPresent()) {
            result.setCode(ResponseCodes.WARNING);
            result.setMessage("Codigo Cuis no encontrado o no ha sido generado");
            return result;
        }

        SolicitudOperaciones request = new SolicitudOperaciones();
        request.setCodigoAmbiente(company.getEnvironmentSiat().getKey());
        request.setCodigoModalidad(company.getModalitySiat().getKey());
        request.setCodigoSistema(company.getSystemCode());
        request.setNit(company.getNit());
        request.setCodigoSucursal(branchOffice.getBranchOfficeSiatId());
        request.setCodigoPuntoVenta(new ObjectFactory().createSolicitudOperacionesCodigoPuntoVenta(pointSale.getPointSaleSiatId()));
        request.setCuis(cuisFromDb.get().getCuis());

        ServicioFacturacionOperaciones service = soapUtil.getOperationService(company.getToken());
        RespuestaCierreSistemas response = service.cierreOperacionesSistema(request);

        log.debug("Resultado cierre de operaciones del punto de venta: {}", response);

        if (!response.isTransaccion()) {
            result.setCode(ResponseCodes.WARNING);
            result.setMessage(response.getMensajesList().get(0).getDescripcion());
            return result;
        }
        result.setCode(ResponseCodes.SUCCESS);
        return result;
    }

    private Optional<Cuis> obtainCuisActive(Long companyId, int branchOfficeSiat, int pointSaleSiat) {
        Optional<Cuis> cuisOptional = cuisRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(pointSaleSiat, branchOfficeSiat, companyId);
        return cuisOptional;
    }

    private void disableCodesCuisCufd(PointSale pointSale) {
        // Baja del cuis y cufd activo asociado.
        Optional<Cufd> objCufd = cufdRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(pointSale.getPointSaleSiatId(),
            pointSale.getBranchOffice().getBranchOfficeSiatId(), pointSale.getBranchOffice().getCompany().getId());
        if (objCufd.isPresent()) {
            objCufd.get().setActive(false);
            cufdRepository.save(objCufd.get());
        }

        // Obtiene el código CUIS vigente de la sucursal y punto de venta.
        Optional<Cuis> objCuis = cuisRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(pointSale.getPointSaleSiatId(),
            pointSale.getBranchOffice().getBranchOfficeSiatId(), pointSale.getBranchOffice().getCompany().getId());
        if (objCuis.isPresent()) {
            objCuis.get().setActive(false);
            cuisRepository.save(objCuis.get());
        }
    }

    private CrudRes createResponse(Integer code, String message, Object body) {
        CrudRes response = new CrudRes();
        response.setCode(code);
        response.setMessage(message);
        response.setBody(body);
        return response;
    }

    @Transactional
    public PointSaleReq save(PointSaleReq request, BranchOffice branchOffice, PointSaleType pointSaleType) {
        PointSale entity = this.toEntity(request, branchOffice, pointSaleType);
        entity = pointSaleRepository.save(entity);
        return this.toDto(entity);
    }

    @Transactional
    public Optional<PointSaleReq> partialUpdate(PointSaleReq request, BranchOffice branchOffice, PointSaleType pointSaleType) {
        return pointSaleRepository
            .findById(request.getId())
            .map(entity -> {
                if (request.getName() != null) {
                    entity.setName(request.getName());
                }
                if (request.getDescription() != null) {
                    entity.setDescription(request.getDescription());
                }
                if (request.getActive() != null) {
                    entity.setActive(request.getActive());
                }
                if (request.getPointSaleSiatId() != null) {
                    entity.setPointSaleSiatId(request.getPointSaleSiatId());
                }
                if (request.getBranchOfficeId() != null) {
                    entity.setBranchOffice(branchOffice);
                }
                if (request.getPointSaleTypeId() != null) {
                    entity.setPointSaleType(pointSaleType);
                }
                log.debug("Request to partially update PointSale : {}", request);
                return entity;
            })
            .map(pointSaleRepository::save)
            .map(this::toDto);
    }

    public Page<PointSaleReq> findAll(Pageable pageable) {
        log.debug("Request to get all PointSale");
        return pointSaleRepository.findAll(pageable).map(this::toDto);
    }

    public Optional<PointSaleReq> findOne(Long id) {
        log.debug("Request to get PointSale : {}", id);
        return pointSaleRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete PointSale : {}", id);
        pointSaleRepository.deleteById(id);
    }

    public PointSale toEntity(PointSaleReq dto, BranchOffice branchOffice, PointSaleType pointSaleType) {
        if (dto == null) {
            return null;
        }

        PointSale entity = new PointSale();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPointSaleSiatId(dto.getPointSaleSiatId());
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);
        entity.setBranchOffice(branchOffice);
        entity.setPointSaleType(pointSaleType);

        return entity;
    }

    public PointSaleReq toDto(PointSale dto) {
        if (dto == null) {
            return null;
        }

        PointSaleReq entity = new PointSaleReq();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPointSaleSiatId(dto.getPointSaleSiatId());
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);
        entity.setBranchOfficeId(dto.getBranchOffice().getId());
        if (dto.getPointSaleType() != null) {
            entity.setPointSaleTypeId(dto.getPointSaleType().getId());
        }
        return entity;
    }

}
