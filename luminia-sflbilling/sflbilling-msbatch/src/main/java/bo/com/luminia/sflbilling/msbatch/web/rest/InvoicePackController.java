package bo.com.luminia.sflbilling.msbatch.web.rest;

import bo.com.luminia.sflbilling.msbatch.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msbatch.service.InvoicePackService;
import bo.com.luminia.sflbilling.msbatch.service.WrapperLogService;
import bo.com.luminia.sflbilling.msbatch.service.constants.InvoiceMassiveType;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.InvoicePackReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.InvoicePackValidateReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.InvoicePackRes;
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
@PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
public class InvoicePackController {

    private final CompanyRepository companyRepository;

    private final InvoicePackService invoicePackService;
    private final WrapperLogService wrapperLogService;

    @ApiOperation(value = "Método para el empaquetado de facturas Fuera de Linea.", response = InvoicePackRes.class)
    @PostMapping("/packs")
    public ResponseEntity<InvoicePackRes> emitPack(@Valid @RequestBody InvoicePackReq request) {
        log.debug("Recepción paquete de facturas Empresa : {}", request.getCompanyId());

        InvoicePackRes response = new InvoicePackRes();
        if (!companyRepository.findByIdAndActiveTrue(request.getCompanyId()).isPresent()) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, request.getCompanyId()));
        } else {
            ZonedDateTime startDate = ZonedDateTime.now();

            response = invoicePackService.emitPack(request);
            // Registra el log de sincronización.
            wrapperLogService.create(InvoiceMassiveType.SYNC_OFFLINE,
                startDate,
                request.getCompanyId(),
                response.getCode().equals(SiatResponseCodes.SUCCESS),
                response.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Método para la validación de facturas Fuera de Linea.", response = InvoicePackRes.class)
    @PostMapping("/packs/validates")
    public ResponseEntity<InvoicePackRes> validatePack(@Valid @RequestBody InvoicePackValidateReq request) {
        log.debug("Recepción masiva de facturas Empresa : {}", request.getCompanyId());

        InvoicePackRes response = new InvoicePackRes();
        if (!companyRepository.findByIdAndActiveTrue(request.getCompanyId()).isPresent()) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, request.getCompanyId()));
        } else {
            response = invoicePackService.validatePack(request.getCompanyId(),false);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
