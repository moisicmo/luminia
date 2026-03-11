package bo.com.luminia.sflbilling.msinvoice.web.rest;

import bo.com.luminia.sflbilling.msinvoice.service.sfe.authentication.AuthTokenGenerationService;
import bo.com.luminia.sflbilling.msinvoice.service.sfe.issue.InvoiceUniqueCodeCufService;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.AuthTokenGenerationReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.InvoiceUniqueCodeGeneratorCufReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.InvoiceUniqueCodeGeneratorCufRes;
import bo.com.luminia.sflbilling.msinvoice.web.rest.response.sfe.AuthTokenGenerationRes;
import bo.com.luminia.sflbilling.msinvoice.web.rest.utils.ResponseUtil;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sfe/token")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
public class AuthTokenGenerationResource {

    private final AuthTokenGenerationService service;

    private final InvoiceUniqueCodeCufService invoiceUniqueCodeService = new InvoiceUniqueCodeCufService();

    @PostMapping("/generate")
    public ResponseEntity<AuthTokenGenerationRes> generate(
        @RequestBody(required = true) AuthTokenGenerationReq request) {
        log.debug("REST request to save AuthTokenGenerationReq: {}", request);

        //TODO pude ser que se necesite mas validations
        Optional<AuthTokenGenerationRes> response = null;
        //Optional<AuthTokenGenerationRes> response = service.callAuthenticationService(request) ;

        return ResponseUtil.wrapOrNotFound(response);
    }

    @PostMapping("/invoice/generate")
    public ResponseEntity<InvoiceUniqueCodeGeneratorCufRes> generateCode(
        @RequestBody(required = true) InvoiceUniqueCodeGeneratorCufReq request) {
        log.debug("REST request to save AuthTokenGenerationReq: {}", request);

        //TODO pude ser que se necesite mas validations
        Optional<InvoiceUniqueCodeGeneratorCufRes> response = invoiceUniqueCodeService.generateCuf(request);

        return ResponseUtil.wrapOrNotFound(response);
    }

}
