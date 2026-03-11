package bo.com.luminia.sflbilling.msaccount.web.rest.admin;

import bo.com.luminia.sflbilling.msaccount.repository.BranchOfficeRepository;
import bo.com.luminia.sflbilling.msaccount.repository.PointSaleRepository;
import bo.com.luminia.sflbilling.msaccount.repository.PointSaleTypeRepository;
import bo.com.luminia.sflbilling.msaccount.service.sfe.PointSaleService;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec.DefaultTransactionException;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.admin.PointSaleReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.PointSaleCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.CrudRes;
import bo.com.luminia.sflbilling.domain.BranchOffice;
import bo.com.luminia.sflbilling.domain.PointSaleType;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.NotFoundAlertException;
import bo.com.luminia.sflbilling.msaccount.web.rest.util.PaginationUtil;
import bo.com.luminia.sflbilling.msaccount.web.rest.util.ResponseUtil;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_ADMIN + "')")
public class AdminPointSaleResource {

    private final PointSaleService pointSaleService;
    private final BranchOfficeRepository branchOfficeRepository;
    private final PointSaleTypeRepository pointSaleTypeRepository;
    private final PointSaleRepository pointSaleRepository;

    @PostMapping("/point-sales")
    public ResponseEntity<PointSaleReq> create(@Valid @RequestBody PointSaleReq request) throws URISyntaxException {
        log.debug("REST request to create Point Sale : {}", request);
        BranchOffice branchOffice = branchOfficeRepository.findById(request.getBranchOfficeId())
            .orElseThrow(() -> new DefaultTransactionException(
                String.format(ResponseMessages.ERROR_SUCURSAL_NO_ENCONTRADA, request.getBranchOfficeId()),
                ErrorEntities.POINT_SALE,
                ErrorKeys.ERR_RECORD_NOT_FOUND));
        PointSaleType pointSaleType = null;
        if (request.getPointSaleTypeId() != null) {
            pointSaleType = pointSaleTypeRepository.findById(request.getPointSaleTypeId()).orElseThrow(() -> new DefaultTransactionException(
                String.format(ResponseMessages.ERROR_NO_EXISTE_POINT_SALE_TYPE, request.getPointSaleTypeId()),
                ErrorEntities.POINT_SALE_TYPE,
                ErrorKeys.ERR_RECORD_NOT_FOUND));
        }
        CrudRes response = new CrudRes();
        response.setCode(ResponseCodes.WARNING);
        response.setMessage(ResponseMessages.WARNING_OPERACION_NO_EJECUTADA);
        // Verifica si el punto de venta debe crearse en SIAT.
        if (request.getPointSaleSiatId() == null) {
            // Registra del punto de venta en el SIAT.
            PointSaleCreateReq req = new PointSaleCreateReq();
            req.setName(request.getName());
            req.setDescription(request.getDescription());
            response = pointSaleService.registerPointSaleSiat(branchOffice, pointSaleType, req);
            if (!response.getCode().equals(ResponseCodes.SUCCESS)) {
                throw new DefaultTransactionException(
                    String.format("Error al registar punto de venta en el siat: %s codigo de error: %d", response.getMessage(), response.getCode()),
                    ErrorEntities.POINT_SALE,
                    ErrorKeys.ERR_RECORD_NOT_SAVED);
            }
            request.setPointSaleSiatId((Integer) response.getBody());
        }
        Optional.ofNullable(pointSaleRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(
                request.getPointSaleSiatId(), branchOffice.getBranchOfficeSiatId(), branchOffice.getCompany().getId()))
            .ifPresent(list -> {
                if (!list.isEmpty()) throw new DefaultTransactionException(
                    String.format("Ya existe un Punto de Venta: %d, para el La sucursal: %d", request.getPointSaleSiatId(), request.getBranchOfficeId()),
                    ErrorEntities.POINT_SALE,
                    ErrorKeys.ERR_RECORD_ALREADY_EXISTS);
            });
        PointSaleReq result = pointSaleService.save(request, branchOffice, pointSaleType);
        return ResponseEntity
            .created(new URI("/api/admin/point-sales/" + result.getId()))
            .body(result);
    }

    @PutMapping
    public ResponseEntity<PointSaleReq> update(@Valid @RequestBody PointSaleReq request) {
        log.debug("REST request to update Point Sale : {}", request);
        BranchOffice branchOffice = branchOfficeRepository.findById(request.getBranchOfficeId())
            .orElseThrow(() -> new DefaultTransactionException(
                String.format(ResponseMessages.ERROR_SUCURSAL_NO_ENCONTRADA, request.getBranchOfficeId()),
                ErrorEntities.POINT_SALE,
                ErrorKeys.ERR_RECORD_NOT_FOUND));
        PointSaleType pointSaleType = null;
        if (request.getPointSaleTypeId() != null) {
            pointSaleType = pointSaleTypeRepository.findById(request.getPointSaleTypeId()).orElseThrow(() -> new DefaultTransactionException(
                String.format(ResponseMessages.ERROR_NO_EXISTE_POINT_SALE_TYPE, request.getPointSaleTypeId()),
                ErrorEntities.POINT_SALE_TYPE,
                ErrorKeys.ERR_RECORD_NOT_FOUND));
        }
        Optional.ofNullable(pointSaleRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(
                request.getPointSaleSiatId(), branchOffice.getBranchOfficeSiatId(), branchOffice.getCompany().getId()))
            .ifPresent(list -> {
                if (!list.isEmpty()) throw new DefaultTransactionException(
                    String.format("Ya existe un Punto de Venta: %d, para el La sucursal: %d", request.getPointSaleSiatId(), request.getBranchOfficeId()),
                    ErrorEntities.POINT_SALE,
                    ErrorKeys.ERR_RECORD_ALREADY_EXISTS);
            });
        return ResponseUtil.wrapOrNotFound(pointSaleService.partialUpdate(request, branchOffice, pointSaleType));
    }

    @GetMapping("/point-sales")
    public ResponseEntity<List<PointSaleReq>> getAll(Pageable pageable) {
        log.debug("REST request to get Point Sale");
        Page<PointSaleReq> page = pointSaleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.addTotalCountHttpHeaders(page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/point-sales/{id}")
    public ResponseEntity<PointSaleReq> get(@PathVariable Long id) {
        log.debug("REST request to get Point Sale : {}", id);
        Optional<PointSaleReq> req = pointSaleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(req);
    }

    @DeleteMapping("/point-sales/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Point Sale : {}", id);
        if (!pointSaleRepository.existsById(id)) throw new NotFoundAlertException(ErrorKeys.ERR_RECORD_NOT_FOUND,
            ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO);
        pointSaleService.delete(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
