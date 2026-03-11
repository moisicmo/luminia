package bo.com.luminia.sflbilling.msbatch.web.rest;

import bo.com.luminia.sflbilling.msbatch.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msbatch.service.SyncSettingService;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.SyncSettingCreateReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.SyncSettingUpdateReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.CrudRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.ResponseMessages;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.ResponseUtil;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.SyncSetting;
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
@RequestMapping("/api/sync-settings")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_ADMIN + "')")
public class SyncSettingResource {

    private final CompanyRepository companyRepository;

    private final SyncSettingService syncSettingService;

    @ApiOperation(value = "Método para el registro de configuración de sincronizaciones.", response = CrudRes.class)
    @PostMapping
    public ResponseEntity<CrudRes> create(@Valid @RequestBody SyncSettingCreateReq request) {
        log.debug("Iniciando registro para syncSetting : {}", request);

        Optional<Company> company = companyRepository.findByIdAndActiveTrue(request.getCompanyId());
        if (!company.isPresent()) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, request.getCompanyId()));
            //return new ResponseEntity<>(new SyncSettingRes(false, String.format(ResponseMessage.ERROR_COMPANY_NOT_FOUND, request.getCompanyId()), null), HttpStatus.NOT_FOUND);
        } else {
            SyncSetting entity = syncSettingService.create(request);
            return new ResponseEntity<>(new CrudRes(true, "El registro terminó correctamente.", new Object() {
                public final long id = entity.getId();
            }), HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Método para la actualización de configuración de sincronizaciones.", response = CrudRes.class)
    @PutMapping
    public ResponseEntity<CrudRes> update(@Valid @RequestBody SyncSettingUpdateReq request) {
        log.debug("Iniciando la actualización para syncSetting : {}", request);

        Optional<Company> company = companyRepository.findByIdAndActiveTrue(request.getCompanyId());
        if (!company.isPresent()) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, request.getCompanyId()));
            //return new ResponseEntity<>(new SyncSettingRes(false, String.format(ResponseMessage.ERROR_COMPANY_NOT_FOUND, request.getCompanyId()), null), HttpStatus.NOT_FOUND);
        } else {
            Optional<SyncSetting> entity = syncSettingService.update(request);
            if (!entity.isPresent()) {
                throw new RecordNotFoundException();
                //return new ResponseEntity<>(new SyncSettingRes(false, "El registro no existe.", null), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(new CrudRes(true, "La actualización terminó correctamente.", new Object() {
                public final long id = entity.get().getId();
            }), HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CrudRes> get(@PathVariable Long id) {
        log.debug("Obtiene el registro para syncSetting : {}", id);
        return ResponseUtil.wrapOrNotFound(Optional.of(syncSettingService.get(id)));
    }

    @GetMapping
    public ResponseEntity<CrudRes> getAll() {
        log.debug("Obtiene la lista de registros para syncSetting");
        return ResponseUtil.wrapOrNotFound(Optional.of(syncSettingService.getAll()));
    }
}
