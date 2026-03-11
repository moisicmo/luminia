package bo.com.luminia.sflbilling.msbatch.web.rest;

import bo.com.luminia.sflbilling.msbatch.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msbatch.service.InvoiceMassiveService;
import bo.com.luminia.sflbilling.msbatch.service.WrapperLogService;
import bo.com.luminia.sflbilling.msbatch.service.constants.InvoiceMassiveType;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.InvoiceMassiveReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.InvoiceMassiveValidateReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.InvoiceMassiveRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.ResponseMessages;
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
import java.time.ZonedDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/invoices")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '"+ AuthoritiesConstants.COMPANY_ADMIN +"')")
public class InvoiceMassiveController {

    private final CompanyRepository companyRepository;

    private final InvoiceMassiveService invoiceMassiveService;
    private final WrapperLogService wrapperLogService;

    @ApiOperation(value = "Método para el empaquetado de facturas Masivas.", response = InvoiceMassiveRes.class)
    @PostMapping("/massive")
    public ResponseEntity<InvoiceMassiveRes> emitMassive(@Valid @RequestBody InvoiceMassiveReq request) {
        log.debug("Recepción masiva de facturas Empresa : {}", request.getCompanyId());

        InvoiceMassiveRes response = new InvoiceMassiveRes();
        if (!companyRepository.findByIdAndActiveTrue(request.getCompanyId()).isPresent()) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, request.getCompanyId()));
        } else {
            ZonedDateTime startDate = ZonedDateTime.now();

            response = invoiceMassiveService.emitMassive(request);
            // Registra el log de sincronización.
            wrapperLogService.create(InvoiceMassiveType.SYNC_MASSIVE,
                startDate,
                request.getCompanyId(),
                response.getCode().equals(SiatResponseCodes.SUCCESS),
                response.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Método para la validación de facturas Masivas.", response = InvoiceMassiveRes.class)
    @PostMapping("/massive/validates")
    public ResponseEntity<InvoiceMassiveRes> validateMassive(@Valid @RequestBody InvoiceMassiveValidateReq request) {
        log.debug("Recepción masiva de facturas Empresa : {}", request.getCompanyId());

        InvoiceMassiveRes response = new InvoiceMassiveRes();
        if (!companyRepository.findByIdAndActiveTrue(request.getCompanyId()).isPresent()) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, request.getCompanyId()));
        } else {
            response = invoiceMassiveService.validateMassive(request.getCompanyId());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
