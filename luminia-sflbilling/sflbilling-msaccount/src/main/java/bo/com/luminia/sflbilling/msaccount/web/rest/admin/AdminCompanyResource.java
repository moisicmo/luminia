package bo.com.luminia.sflbilling.msaccount.web.rest.admin;

import bo.com.luminia.sflbilling.msaccount.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msaccount.service.sfe.CompanyService;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.admin.CompanyReq;
import bo.com.luminia.sflbilling.domain.Company;
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
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
public class AdminCompanyResource {

    private final CompanyService companyService;

    private final CompanyRepository companyRepository;

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody CompanyReq company) throws URISyntaxException {
        log.debug("REST request to save Company : {}", company);
        Company result = companyService.save(company);
        return ResponseEntity
            .created(new URI("/api/admin/companies/" + result.getId()))
            .body(result);
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody CompanyReq company) {
        log.debug("REST request to update Company : {}", company);
        return ResponseUtil.wrapOrNotFound(companyService.partialUpdate(company));
    }

    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getAllCompanies(Pageable pageable) {
        log.debug("REST request to get a page of Companies");
        Page<Company> page = companyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.addTotalCountHttpHeaders(page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompany(@PathVariable Long id) {
        log.debug("REST request to get Company : {}", id);
        Optional<Company> company = companyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(company);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        log.debug("REST request to delete Company : {}", id);
        if (!companyRepository.existsById(id)) throw new NotFoundAlertException(ErrorKeys.ERR_RECORD_NOT_FOUND,
            ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO);
        companyService.deleteCompany(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
