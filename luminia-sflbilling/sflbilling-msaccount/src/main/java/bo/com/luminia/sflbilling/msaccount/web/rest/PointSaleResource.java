package bo.com.luminia.sflbilling.msaccount.web.rest;

import bo.com.luminia.sflbilling.msaccount.service.sfe.PointSaleService;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.PointSaleCloseReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.PointSaleCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.PointSaleUpdateReq;
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
@RequestMapping("/api/point-sales")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '"+ AuthoritiesConstants.COMPANY_ADMIN +"')")
public class PointSaleResource {

    private final PointSaleService pointSaleService;

    @GetMapping
    public ResponseEntity<CrudRes> getAll() {
        log.debug("REST request to get Point Sale");
        return ResponseUtil.wrapOrNotFound(Optional.of(pointSaleService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CrudRes> get(@PathVariable Long id) {
        log.debug("REST request to get Point Sale : {}", id);
        return ResponseUtil.wrapOrNotFound(Optional.of(pointSaleService.get(id)));
    }

    @PostMapping
    public ResponseEntity<CrudRes> create(@RequestBody PointSaleCreateReq request) {
        log.debug("REST request to create Point Sale : {}", request);
        return ResponseUtil.wrapOrNotFound(Optional.of(pointSaleService.create(request)));
    }

    @PutMapping
    public ResponseEntity<CrudRes> update(@RequestBody PointSaleUpdateReq request) {
        log.debug("REST request to update Point Sale : {}", request);
        return ResponseUtil.wrapOrNotFound(Optional.of(pointSaleService.update(request)));
    }

    @PostMapping("/close-operation")
    public ResponseEntity<CrudRes> closeOperation(@RequestBody PointSaleCloseReq request) {
        log.debug("REST request to close operation Point Sale : {}", request);
        return ResponseUtil.wrapOrNotFound(Optional.of(pointSaleService.closeOperation(request.getId())));
    }

    @PostMapping("/close")
    public ResponseEntity<CrudRes> close(@RequestBody PointSaleCloseReq request) {
        log.debug("REST request to close Point Sale : {}", request);
        return ResponseUtil.wrapOrNotFound(Optional.of(pointSaleService.close(request.getId())));
    }
}
