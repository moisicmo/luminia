package bo.com.luminia.sflbilling.msaccount.web.rest;

import bo.com.luminia.sflbilling.msaccount.service.sfe.SignatureService;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.SignatureCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.SignatureUpdateReq;
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
@RequestMapping("/api/signatures")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '"+ AuthoritiesConstants.COMPANY_ADMIN +"')")
public class SignatureResource {

    private final SignatureService service;

    @GetMapping("/{id}")
    public ResponseEntity<CrudRes> get(@PathVariable Long id) {
        log.debug("REST request to get Signature : {}", id);
        return ResponseUtil.wrapOrNotFound(Optional.of(service.get(id)));
    }

    @PostMapping
    public ResponseEntity<CrudRes> create(@RequestBody SignatureCreateReq request) {
        log.debug("REST request to create Signature : {}", request);
        return ResponseUtil.wrapOrNotFound(Optional.of(service.create(request)));
    }

    @PutMapping
    public ResponseEntity<CrudRes> update(@RequestBody SignatureUpdateReq request) {
        log.debug("REST request to update Signature : {}", request);
        return ResponseUtil.wrapOrNotFound(Optional.of(service.update(request)));
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<CrudRes> delete(@PathVariable Long companyId) {
        log.debug("REST request to delete Signature : {}", companyId);
        return ResponseUtil.wrapOrNotFound(Optional.of(service.delete(companyId, 0)));
    }
}
