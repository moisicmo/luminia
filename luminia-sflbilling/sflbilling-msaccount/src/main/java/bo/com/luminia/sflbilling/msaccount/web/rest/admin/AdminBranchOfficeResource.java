package bo.com.luminia.sflbilling.msaccount.web.rest.admin;

import bo.com.luminia.sflbilling.msaccount.repository.BranchOfficeRepository;
import bo.com.luminia.sflbilling.msaccount.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msaccount.service.sfe.BranchOfficeService;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.NotFoundAlertException;
import bo.com.luminia.sflbilling.msaccount.web.rest.errors.spec.DefaultTransactionException;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.admin.BranchOfficeReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.util.PaginationUtil;
import bo.com.luminia.sflbilling.msaccount.web.rest.util.ResponseUtil;
import bo.com.luminia.sflbilling.domain.Company;
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
public class AdminBranchOfficeResource {

    private final BranchOfficeService service;
    private final BranchOfficeRepository repository;
    private final CompanyRepository companyRepository;

    @PostMapping("/branch-offices")
    public ResponseEntity<BranchOfficeReq> createBranchOffice(@Valid @RequestBody BranchOfficeReq request) throws URISyntaxException {
        log.debug("REST request to create BranchOffice : {}", request);
        Company company = companyRepository.findByIdAndActiveTrue(request.getCompanyId()).orElseThrow(() -> new NotFoundAlertException(ErrorKeys.ERR_RECORD_NOT_FOUND,
            ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO));
        Optional.ofNullable(repository.findByCompanyIdAndBranchOfficeSiatId(request.getCompanyId(), request.getBranchOfficeSiatId()))
            .ifPresent(list -> {
                if (!list.isEmpty()) throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_EXISTE_BRANCH_ID, request.getBranchOfficeSiatId()),
                    ErrorEntities.BRANCH_OFFICE,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
            });
        BranchOfficeReq result = service.save(request, company);
        return ResponseEntity
            .created(new URI("/api/admin/branch-offices/" + result.getId()))
            .body(result);
    }

    @PutMapping("/branch-offices")
    public ResponseEntity<BranchOfficeReq> update(@Valid @RequestBody BranchOfficeReq request) {
        log.debug("REST request to update BranchOffice : {}", request);
        Company company = companyRepository.findByIdAndActiveTrue(request.getCompanyId()).orElseThrow(() -> new NotFoundAlertException(ErrorKeys.ERR_RECORD_NOT_FOUND,
            ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO));
        Optional.ofNullable(repository.findByCompanyIdAndBranchOfficeSiatId(request.getCompanyId(), request.getBranchOfficeSiatId()))
            .ifPresent(list -> {
                if (!list.isEmpty()) throw new DefaultTransactionException(
                    String.format(ResponseMessages.ERROR_EXISTE_BRANCH_ID, request.getBranchOfficeSiatId()),
                    ErrorEntities.BRANCH_OFFICE,
                    ErrorKeys.ERR_RECORD_NOT_FOUND);
            });
        return ResponseUtil.wrapOrNotFound(service.partialUpdate(request, company));
    }

    @GetMapping("/branch-offices")
    public ResponseEntity<List<BranchOfficeReq>> getAll(Pageable pageable) {
        log.debug("REST request to get BranchOffice");
        Page<BranchOfficeReq> page = service.findAll(pageable);
        HttpHeaders headers = PaginationUtil.addTotalCountHttpHeaders(page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/branch-offices/{branchId}")
    public ResponseEntity<BranchOfficeReq> getById(@PathVariable Long branchId) {
        log.debug("REST request to get BranchOffice : {}", branchId);
        Optional<BranchOfficeReq> req = service.findOne(branchId);
        return ResponseUtil.wrapOrNotFound(req);
    }

    @DeleteMapping("/branch-offices/{branchId}")
    public ResponseEntity<Void> delete(@PathVariable Long branchId) {
        log.debug("REST request to delete BranchOffice : {}", branchId);
        if (!repository.existsById(branchId)) throw new NotFoundAlertException(ErrorKeys.ERR_RECORD_NOT_FOUND,
            ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO);
        service.delete(branchId);
        return ResponseEntity
            .noContent()
            .build();
    }
}
