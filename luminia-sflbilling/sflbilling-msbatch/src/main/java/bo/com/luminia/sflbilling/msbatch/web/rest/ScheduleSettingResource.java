package bo.com.luminia.sflbilling.msbatch.web.rest;

import bo.com.luminia.sflbilling.msbatch.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msbatch.service.ScheduleSettingService;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.RecordNotFoundException;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.ScheduleSettingCreateReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.ScheduleSettingUpdateReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.CrudRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.ResponseMessages;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.ResponseUtil;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.ScheduleSetting;
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
@RequestMapping("/api/schedule-settings")
@PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.COMPANY_ADMIN + "')")
public class ScheduleSettingResource {

    private final CompanyRepository companyRepository;

    private final ScheduleSettingService scheduleSettingService;

    @ApiOperation(value = "Método para el registro de configuración de envío masivo.", response = ScheduleSettingCreateReq.class)
    @PostMapping
    public ResponseEntity<CrudRes> create(@Valid @RequestBody ScheduleSettingCreateReq request) {
        log.debug("Iniciando registro para scheduleSetting : {}", request);

        Optional<Company> company = companyRepository.findByIdAndActiveTrue(request.getCompanyId());
        if (!company.isPresent()) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, request.getCompanyId()));
        } else {
            ScheduleSetting entity = scheduleSettingService.create(request);

            return new ResponseEntity<>(new CrudRes(true, "El registro terminó correctamente.", new Object() {
                public final long id = entity.getId();
            }), HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Método para la actualización de configuración de sincronizaciones.", response = ScheduleSettingUpdateReq.class)
    @PutMapping
    public ResponseEntity<CrudRes> update(@Valid @RequestBody ScheduleSettingUpdateReq request) {
        log.debug("Iniciando la actualización para scheduleSetting : {}", request);

        Optional<Company> company = companyRepository.findByIdAndActiveTrue(request.getCompanyId());
        if (!company.isPresent()) {
            throw new RecordNotFoundException(
                String.format(ResponseMessages.ERROR_COMPANY_NOT_FOUND, request.getCompanyId()));
        } else {
            Optional<ScheduleSetting> entity = scheduleSettingService.update(request);
            if (!entity.isPresent()) {
                throw new RecordNotFoundException();
            }
            return new ResponseEntity<>(new CrudRes(true, "La actualización terminó correctamente.", new Object() {
                public final long id = entity.get().getId();
            }), HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CrudRes> get(@PathVariable Long id) {
        log.debug("Obtiene el registro para scheduleSetting : {}", id);
        return ResponseUtil.wrapOrNotFound(Optional.of(scheduleSettingService.get(id)));
    }

    @GetMapping
    public ResponseEntity<CrudRes> getAll() {
        log.debug("Obtiene la lista de registros para scheduleSetting");
        return ResponseUtil.wrapOrNotFound(Optional.of(scheduleSettingService.getAll()));
    }
}
