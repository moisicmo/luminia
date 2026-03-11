package bo.com.luminia.sflbilling.msaccount.web.rest;

import bo.com.luminia.sflbilling.msaccount.service.sfe.BranchOfficeService;
import bo.com.luminia.sflbilling.msaccount.service.sfe.PointSaleService;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.BranchOfficeCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.BranchOfficeUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.CrudRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.util.ResponseUtil;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/branch-offices")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '"+ AuthoritiesConstants.COMPANY_ADMIN +"')")
public class BranchOfficeResource {

    private final BranchOfficeService service;
    private final PointSaleService pointSaleService;

    @GetMapping
    public ResponseEntity<CrudRes> get() {
        log.debug("REST request to get BranchOffice");
        return ResponseUtil.wrapOrNotFound(Optional.of(service.getAll()));
    }

    @GetMapping("/{branchId}")
    public ResponseEntity<CrudRes> getById(@PathVariable Long branchId) {
        log.debug("REST request to get BranchOffice : {}", branchId);
        return ResponseUtil.wrapOrNotFound(Optional.of(service.get(branchId)));
    }

    @PostMapping
    public ResponseEntity<CrudRes> create(@RequestBody BranchOfficeCreateReq request) {
        log.debug("REST request to create BranchOffice : {}", request);
        return ResponseUtil.wrapOrNotFound(Optional.of(service.create(request)));
    }

    @PutMapping
    public ResponseEntity<CrudRes> update(@RequestBody BranchOfficeUpdateReq request) {
        log.debug("REST request to update BranchOffice : {}", request);
        return ResponseUtil.wrapOrNotFound(Optional.of(service.update(request)));
    }

    @DeleteMapping("/{companyId}/{branchId}")
    public ResponseEntity<CrudRes> delete(@PathVariable Long companyId, @PathVariable Integer branchId) {
        log.debug("REST request to delete BranchOffice : {}", companyId);
        return ResponseUtil.wrapOrNotFound(Optional.of(service.delete(companyId, branchId)));
    }

    @GetMapping("/{branchOfficeId}/point-sales")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "', '" + AuthoritiesConstants.COMPANY_ADMIN + "')")
    public ResponseEntity<CrudRes> getByPointSale(@PathVariable Long branchOfficeId) {
        log.debug("REST request to get Point Sale : {}", branchOfficeId);
        return ResponseUtil.wrapOrNotFound(Optional.of(pointSaleService.getAllByBranchOffice(branchOfficeId)));
    }
}
