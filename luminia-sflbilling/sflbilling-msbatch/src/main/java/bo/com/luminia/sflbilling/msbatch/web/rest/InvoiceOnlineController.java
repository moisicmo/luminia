package bo.com.luminia.sflbilling.msbatch.web.rest;

import bo.com.luminia.sflbilling.msbatch.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msbatch.task.InvoiceOnlineTask;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.InvoiceOnlineManualReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.InvoiceOnlineRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.ResponseMessages;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/invoices")
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
public class InvoiceOnlineController {

    private final CompanyRepository companyRepository;
    private final InvoiceOnlineTask invoiceOnlineTask;

    @ApiOperation(value = "Método para activar al Sistema En Linea.", response = InvoiceOnlineRes.class)
    @PostMapping("/online/init")
    public ResponseEntity<InvoiceOnlineRes> online() {
        log.debug("Sistema habilitado para emisión En Linea");

        InvoiceOnlineRes response = invoiceOnlineTask.createJobs();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método para activar al Sistema En Linea por Empresa.", response = InvoiceOnlineRes.class)
    @PostMapping("/online/init/manuals")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '"+ AuthoritiesConstants.COMPANY_INTEGRATION +"')")
    public ResponseEntity<InvoiceOnlineRes> onlineManual(@Valid @RequestBody InvoiceOnlineManualReq invoiceOnlineManualReq) {
        log.debug("Sistema habilitado para emisión En Linea");

        InvoiceOnlineRes response = new InvoiceOnlineRes();
        Optional<Company> companyFromDb = companyRepository.findByBusinessCodeAndActiveTrue(invoiceOnlineManualReq.getBusinessCode());
        if (!companyFromDb.isPresent()) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_CODE_NOT_FOUND, invoiceOnlineManualReq.getBusinessCode()));
        } else {
            Company company = companyFromDb.get();
            response = invoiceOnlineTask.createJobs(company);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
