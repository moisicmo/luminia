package bo.com.luminia.sflbilling.msbatch.web.rest;

import bo.com.luminia.sflbilling.msbatch.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msbatch.service.SyncCodeService;
import bo.com.luminia.sflbilling.msbatch.service.SyncLogService;
import bo.com.luminia.sflbilling.msbatch.service.SyncParameterService;
import bo.com.luminia.sflbilling.msbatch.service.constants.SyncType;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.SyncCodeReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.SyncParameterReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.SyncRes;
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
import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sync")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '"+ AuthoritiesConstants.COMPANY_ADMIN +"')")
public class SyncController {

    private final CompanyRepository companyRepository;

    private final SyncParameterService syncParameterService;
    private final SyncCodeService syncCodeService;
    private final SyncLogService syncLogService;

    @ApiOperation(value = "Método para la sincronización de códigos CUIS y CUFD.", response = SyncRes.class)
    @PostMapping("/codes")
    public ResponseEntity<SyncRes> syncCodes(@Valid @RequestBody SyncCodeReq syncCodeReq) {
        log.debug("Iniciando sincronización de Códigos CUIS, CUFD : {}", syncCodeReq);

        Optional<Company> company = companyRepository.findByIdAndActiveTrue(syncCodeReq.getCompanyId());
        if (!company.isPresent()) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, syncCodeReq.getCompanyId()));
            //return new ResponseEntity<>(new SyncRes(false, String.format(ResponseMessage.ERROR_COMPANY_NOT_FOUND, syncCodeReq.getCompanyId())), HttpStatus.NOT_FOUND);
        } else {
            ZonedDateTime startDate = ZonedDateTime.now();

            SyncRes syncRes = syncCodeService.synchronize(company.get());
            // Registra el log de sincronización.
            syncLogService.create(SyncType.SYNC_CODES,
                startDate,
                company.get().getId(),
                syncRes.getResponse(),
                syncRes.getMessage());

            return new ResponseEntity<>(syncRes, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Método para la sincronización de códigos CUIS y CUFD.", response = SyncRes.class)
    @PostMapping("/codes/individuals")
    public ResponseEntity<SyncRes> syncCodesIndividual(@Valid @RequestBody SyncCodeReq syncCodeReq) {
        log.debug("Iniciando sincronización de Códigos CUIS, CUFD : {}", syncCodeReq);

        Optional<Company> company = companyRepository.findByIdAndActiveTrue(syncCodeReq.getCompanyId());
        if (!company.isPresent()) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, syncCodeReq.getCompanyId()));
        } else {
            ZonedDateTime startDate = ZonedDateTime.now();

            SyncRes syncRes = syncCodeService.synchronizeIndividual(company.get());
            // Registra el log de sincronización.
            syncLogService.create(SyncType.SYNC_CODES,
                startDate,
                company.get().getId(),
                syncRes.getResponse(),
                syncRes.getMessage());

            return new ResponseEntity<>(syncRes, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Método para la sincronización de parámetros.", response = SyncRes.class)
    @PostMapping("/parameters")
    public ResponseEntity<SyncRes> syncParameters(@Valid @RequestBody SyncParameterReq syncParameterReq) {
        log.debug("Iniciando sincronización de Parámetros : {}", syncParameterReq);

        Optional<Company> company = companyRepository.findByIdAndActiveTrue(syncParameterReq.getCompanyId());
        if (!company.isPresent()) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, syncParameterReq.getCompanyId()));
            //return new ResponseEntity<>(new SyncRes(false, String.format(ResponseMessage.ERROR_COMPANY_NOT_FOUND, syncParameterReq.getCompanyId())), HttpStatus.NOT_FOUND);
        } else {
            ZonedDateTime startDate = ZonedDateTime.now();

            SyncRes syncRes = syncParameterService.synchronize(syncParameterReq, company.get());
            // Registra el log de sincronización.
            syncLogService.create(SyncType.SYNC_PARAMETERS,
                startDate,
                company.get().getId(),
                syncRes.getResponse(),
                syncRes.getMessage());

            return new ResponseEntity<>(syncRes, HttpStatus.OK);
        }
    }
}
