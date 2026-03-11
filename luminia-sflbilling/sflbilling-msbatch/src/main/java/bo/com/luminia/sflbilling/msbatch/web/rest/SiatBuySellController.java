package bo.com.luminia.sflbilling.msbatch.web.rest;

import bo.com.luminia.sflbilling.msbatch.repository.UserRepository;
import bo.com.luminia.sflbilling.msbatch.siat.service.SiatBuySellService;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.NotFoundAlertException;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.siat.SiatBuySellInvoiceStatusVerificationRes;
import bo.com.luminia.sflbilling.domain.*;
import bo.com.luminia.sflbilling.msbatch.repository.*;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/siat")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_ADMIN + "')")
public class SiatBuySellController {

    private final SiatBuySellService siatBuySellService;

    private final UserRepository userRepository;


    @GetMapping("/check-status-siat/{cuf}")
    public ResponseEntity<SiatBuySellInvoiceStatusVerificationRes> queryStatusByCuf(@PathVariable String cuf) {
        log.debug("Buscando el estado de la factura en el SIAT por cuf: '{}'", cuf);

        //Obtengo el usuario principal
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        log.debug("usuario principal: {}", currentPrincipalName);

        //User user = userRepository.findCurrentUserCompany().orElseThrow(()-> new NotFoundAlertException("Usuario de compañía no econtrado", "Usuario de compañía no econtrado: cuf="+cuf));
        User userCompany = userRepository.findCurrentUserCompany().orElse(null);
        log.debug("usuario de compañía: {}", userCompany);

        //Si el usuario es el admin, lo dejaremos pasar
        if (userCompany == null && !"admin".equals(currentPrincipalName))
            throw new NotFoundAlertException("Usuario de compañía no econtrado", "Usuario de compañía no econtrado: cuf=" + cuf);

        SiatBuySellInvoiceStatusVerificationRes response = siatBuySellService.checkInvoiceStatus(cuf, userCompany);
        return ResponseEntity.ok(response);
    }





}
