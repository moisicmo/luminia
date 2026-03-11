package bo.com.luminia.sflbilling.msbatch.web.rest;

import bo.com.luminia.sflbilling.domain.Offline;
import bo.com.luminia.sflbilling.msbatch.repository.OfflineRepository;
import bo.com.luminia.sflbilling.msbatch.service.OfflineValidatorService;
import bo.com.luminia.sflbilling.msbatch.service.dto.InvoiceOffline;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.BadRequestAlertException;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.NotFoundAlertException;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.SyncCodeReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.SyncRes;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/manualconnection")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
public class ManualConnectionController {
    private final OfflineValidatorService offlineValidatorService;

    @PostMapping("/gotooffline")
    public ResponseEntity<Void> goToOffline(@RequestBody InvoiceOffline request) {
        offlineValidatorService.goToOfflineManual(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/trygotoonline")
    public ResponseEntity<Void> tryGoToOnline() {
        offlineValidatorService.tryToGoOnlineManual();
        return ResponseEntity.ok().build();
    }
}
