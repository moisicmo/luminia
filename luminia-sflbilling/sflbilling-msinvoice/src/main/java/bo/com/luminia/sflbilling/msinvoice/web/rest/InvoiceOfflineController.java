package bo.com.luminia.sflbilling.msinvoice.web.rest;

import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.msinvoice.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msinvoice.service.sfe.issue.InvoiceOfflineService;
import bo.com.luminia.sflbilling.msinvoice.service.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceOfflineManualReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceOfflineReq;
import bo.com.luminia.sflbilling.msinvoice.web.rest.response.sfe.invoice.InvoiceOfflineRes;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/invoices")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
public class InvoiceOfflineController {

    private final CompanyRepository companyRepository;

    private final InvoiceOfflineService invoiceOfflineService;

    @ApiOperation(value = "Método para obtener el registro Fuera de Linea.", response = InvoiceOfflineRes.class)
    @GetMapping("/offline")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<InvoiceOfflineRes> get() {
        log.debug("Obtiene el registro Fuera de Linea : {}");

        InvoiceOfflineRes response = invoiceOfflineService.get();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método para activar al Sistema Fuera de Linea.", response = InvoiceOfflineRes.class)
    @PostMapping("/offline")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_ADMIN + "')")
    public ResponseEntity<InvoiceOfflineRes> offline(@Valid @RequestBody InvoiceOfflineReq invoiceOfflineReq) {
        log.debug("Sistema Fuera de Linea : {}", invoiceOfflineReq);

        InvoiceOfflineRes response = invoiceOfflineService.offline(invoiceOfflineReq);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método para activar al Sistema Fuera de Linea.", response = InvoiceOfflineRes.class)
    @PostMapping("/offline/manuals")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_INTEGRATION + "')")
    public ResponseEntity<InvoiceOfflineRes> offlineManual(@Valid @RequestBody InvoiceOfflineManualReq invoiceOfflineManualReq) {
        log.debug("Sistema Fuera de Linea : {}", invoiceOfflineManualReq);

        InvoiceOfflineRes response = new InvoiceOfflineRes();
        Optional<Company> companyFromDb = companyRepository.findByBusinessCodeAndActiveTrue(invoiceOfflineManualReq.getBusinessCode());

        if (!companyFromDb.isPresent()) {
            throw new RecordNotFoundException(String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, invoiceOfflineManualReq.getBusinessCode()));
        } else {
            invoiceOfflineManualReq.setCompanyId(companyFromDb.get().getId());
            response = invoiceOfflineService.offlineManual(invoiceOfflineManualReq);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Método para activar al Sistema En Linea.", response = InvoiceOfflineRes.class)
    @PostMapping("/online")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_ADMIN + "')")
    public ResponseEntity<InvoiceOfflineRes> online() {
        log.debug("Sistema En de Linea");

        InvoiceOfflineRes response = invoiceOfflineService.online();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
