package bo.com.luminia.sflbilling.msaccount.web.rest;

import bo.com.luminia.sflbilling.msaccount.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msaccount.service.sfe.ApprovedProductsService;
import bo.com.luminia.sflbilling.msaccount.service.sfe.BranchOfficeService;
import bo.com.luminia.sflbilling.msaccount.service.sfe.CompanyService;
import bo.com.luminia.sflbilling.msaccount.service.sfe.SignatureService;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.BadRequestAlertException;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.CompanyCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.CompanyUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.CrudRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.util.ResponseUtil;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
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
@RequestMapping("/api/companies")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
public class CompanyResource {

    private final CompanyService companyService;

    private final BranchOfficeService branchOfficeService;

    private final CompanyRepository companyRepository;

    private final SignatureService signatureService;

    private final ApprovedProductsService approvedProductsService;

    @GetMapping
    public ResponseEntity<CrudRes> getAll(@RequestParam("nit") Optional<Long> nit) {

        if (nit.isPresent()) {
            log.debug("REST request to get Company : {}", nit);
            return ResponseUtil.wrapOrNotFound(Optional.of(companyService.getByNit(nit.get())));
        } else {
            log.debug("REST request to get Companies");
            return ResponseUtil.wrapOrNotFound(Optional.of(companyService.getAll()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CrudRes> get(@PathVariable Long id) {
        log.debug("REST request to get Company : {}", id);
        return ResponseUtil.wrapOrNotFound(Optional.of(companyService.get(id)));
    }

    @PostMapping
    public ResponseEntity<CrudRes> create(@RequestBody CompanyCreateReq request) {
        // log.debug("REST request to create Company : {}", request);
        if (companyRepository.existsByNitAndModalitySiat(request.getNit(), ModalitySiatEnum.get(request.getModalitySiat())))
            throw new BadRequestAlertException("Nit and Modality Exists", "Company", "NIT_MODALITY_EXISTS");

        return ResponseUtil.wrapOrNotFound(Optional.of(companyService.create(request)));
    }

    @PutMapping
    public ResponseEntity<CrudRes> update(@RequestBody CompanyUpdateReq request) {
        // log.debug("REST request to update Company : {}", request);
        return ResponseUtil.wrapOrNotFound(Optional.of(companyService.update(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CrudRes> delete(@PathVariable Long id) {
        log.debug("REST request to delete Company : {}", id);
        return ResponseUtil.wrapOrNotFound(Optional.of(companyService.delete(id)));
    }

    @GetMapping("/{businessCode}/branch-offices")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<CrudRes> getByCompanyBusinessCode(@PathVariable String businessCode) {
        log.debug("REST request to get BranchOffice by Company: {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return ResponseUtil.wrapOrNotFound(Optional.of(branchOfficeService.getByCompanyBusinessCode(businessCode)));
    }

    @GetMapping("/{companyId}/signatures")
    public ResponseEntity<CrudRes> getBySignature(@PathVariable Long companyId) {
        log.debug("REST request to get Signature : {}", companyId);
        return ResponseUtil.wrapOrNotFound(Optional.of(signatureService.getByCompany(companyId)));
    }

    @GetMapping("/{businessCode}/approved-products")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<CrudRes> getByApprovedProduct(@PathVariable String businessCode) {
        log.debug("REST request to get ApprovedProduct : {}", businessCode);
        Optional<Company> company = companyRepository.findByBusinessCodeAndActiveTrue(businessCode);
        if (!company.isPresent())
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, businessCode));
        else
            return ResponseUtil.wrapOrNotFound(Optional.of(approvedProductsService.getAllByCompanyId(company.get().getId())));
    }
}
