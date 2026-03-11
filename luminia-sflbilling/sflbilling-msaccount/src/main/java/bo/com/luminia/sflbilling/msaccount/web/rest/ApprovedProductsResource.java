package bo.com.luminia.sflbilling.msaccount.web.rest;

import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.msaccount.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msaccount.service.sfe.ApprovedProductsService;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.ApprovedProductCreateExternalReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.ApprovedProductCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.ApprovedProductUpdateExternalReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.ApprovedProductUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.CrudRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.util.ResponseUtil;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/approved-products")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_ADMIN + "')")
public class ApprovedProductsResource {

    private final ApprovedProductsService service;
    private final CompanyRepository companyRepository;

    @GetMapping("/{companyId}/product-code/{productCode}")
    public ResponseEntity<CrudRes> getByProductCode(@PathVariable Long companyId, @PathVariable String productCode) {
        log.debug("REST request to get ApprovedProduct : {}", companyId);
        return ResponseUtil.wrapOrNotFound(Optional.of(service.getByProductCode(companyId, productCode)));
    }

    @GetMapping("/{companyId}/measurements/{measurementUnitId}")
    public ResponseEntity<CrudRes> getByMeasurement(@PathVariable Long companyId, @PathVariable Long measurementUnitId) {
        log.debug("REST request to get ApprovedProduct : {}", companyId);
        return ResponseUtil.wrapOrNotFound(Optional.of(service.getByMeasurementUnit(companyId, measurementUnitId)));
    }

    @GetMapping("/{companyId}/product-services/{productServiceId}")
    public ResponseEntity<CrudRes> getByProductService(@PathVariable Long companyId, @PathVariable Long productServiceId) {
        log.debug("REST request to get ApprovedProduct : {}", companyId);
        return ResponseUtil.wrapOrNotFound(Optional.of(service.getByProductService(companyId, productServiceId)));
    }

    @PostMapping
    public ResponseEntity<CrudRes> create(@RequestBody ApprovedProductCreateReq request) {
        log.debug("REST request to create Approved Product : {}", request);
        return ResponseUtil.wrapOrNotFound(Optional.of(service.create(request)));
    }

    @PutMapping
    public ResponseEntity<CrudRes> update(@RequestBody ApprovedProductUpdateReq request) {
        log.debug("REST request to update Approved Product : {}", request);
        return ResponseUtil.wrapOrNotFound(Optional.of(service.update(request)));
    }

    @DeleteMapping
    public ResponseEntity<CrudRes> delete(@PathVariable Long id) {
        log.debug("REST request to delete Approved Product : {}", id);
        return ResponseUtil.wrapOrNotFound(Optional.of(service.delete(id)));
    }

    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    @GetMapping("/{businessCode}/approved-products/{id}")
    public ResponseEntity<CrudRes> getExternal(@PathVariable String businessCode, @PathVariable Long id) {
        log.debug("REST request to get ApprovedProduct : {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return new ResponseEntity<>(service.getExternal(company.get().getId(), id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    @PostMapping("/{businessCode}")
    public ResponseEntity<CrudRes> createExternal(@PathVariable String businessCode, @RequestBody ApprovedProductCreateExternalReq request) {
        log.debug("REST request to create Approved Product : {}", request);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return ResponseUtil.wrapOrNotFound(Optional.of(service.createExternal(company.get(), request)));
    }

    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    @PostMapping("/{businessCode}/massive")
    public ResponseEntity<CrudRes> createExternal(@PathVariable String businessCode, @RequestBody List<ApprovedProductCreateExternalReq> request) {
        log.debug("REST request to create Approved Product : {}", request);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return ResponseUtil.wrapOrNotFound(Optional.of(service.createExternal(company.get(), request)));
    }

    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    @PutMapping("/{businessCode}")
    public ResponseEntity<CrudRes> updateExternal(@PathVariable String businessCode, @RequestBody ApprovedProductUpdateExternalReq request) {
        log.debug("REST request to update Approved Product : {}", request);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return ResponseUtil.wrapOrNotFound(Optional.of(service.updateExternal(company.get(), request)));
    }

    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    @DeleteMapping("/{businessCode}/approved-products/{id}")
    public ResponseEntity<CrudRes> deleteExternal(@PathVariable String businessCode, @PathVariable Long id) {
        log.debug("REST request to delete Approved Product : {}", id);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return ResponseUtil.wrapOrNotFound(Optional.of(service.deleteExternal(company.get(), id)));
    }
}
