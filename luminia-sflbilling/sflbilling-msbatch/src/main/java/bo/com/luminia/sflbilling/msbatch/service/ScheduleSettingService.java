package bo.com.luminia.sflbilling.msbatch.service;

import bo.com.luminia.sflbilling.msbatch.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msbatch.repository.ScheduleSettingRepository;
import bo.com.luminia.sflbilling.domain.ScheduleSetting;
import bo.com.luminia.sflbilling.msbatch.task.InvoiceMassiveTask;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.DefaultTransactionException;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.ScheduleSettingCreateReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.ScheduleSettingUpdateReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.CrudRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.ScheduleSettingRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.ResponseMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduleSettingService {

    private final ScheduleSettingRepository scheduleSettingRepository;
    private final CompanyRepository companyRepository;

    private final InvoiceMassiveTask invoiceMassiveTask;

    @Transactional
    public ScheduleSetting create(ScheduleSettingCreateReq request) {
        ScheduleSetting entity = new ScheduleSetting();
        entity.setCronDate(request.getCronDate());
        entity.setDescription(request.getDescription());
        entity.setActive(true);
        entity.setCompany(companyRepository.getById(request.getCompanyId()));
        ScheduleSetting entityNew = scheduleSettingRepository.save(entity);
        // Inicia la tarea de sincronización.
        invoiceMassiveTask.createJob(entityNew);

        log.info("Información registrada para scheduleSetting: {}", entityNew);
        return entityNew;
    }

    @Transactional
    public Optional<ScheduleSetting> update(ScheduleSettingUpdateReq request) {
        return Optional
            .of(scheduleSettingRepository.findById(request.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(entity -> {
                // Elimina la tarea de sincronización.
                invoiceMassiveTask.removeJob(entity.getId());

                entity.setCronDate(request.getCronDate());
                entity.setDescription(request.getDescription());
                entity.setActive(request.getActive());
                scheduleSettingRepository.save(entity);

                if (request.getActive()) {
                    // Inicia la tarea de sincronización.
                    invoiceMassiveTask.createJob(entity);
                }

                log.info("Información actualizada para scheduleSetting: {}", entity);
                return entity;
            });
    }

    @Transactional
    public CrudRes getAll() {
        List<ScheduleSetting> result = scheduleSettingRepository.findAll();
        CrudRes response = new CrudRes();
        List<ScheduleSettingRes> scheduleSettingResList = new ArrayList<>();

        for (ScheduleSetting scheduleSetting : result) {
            ScheduleSettingRes scheduleSettingRes = new ScheduleSettingRes();
            BeanUtils.copyProperties(scheduleSetting, scheduleSettingRes);
            scheduleSettingRes.setCompanyId(scheduleSetting.getCompany().getId());
            scheduleSettingResList.add(scheduleSettingRes);
        }

        response.setResponse(true);
        response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
        response.setBody(scheduleSettingResList);
        return response;
    }

    @Transactional
    public CrudRes get(Long id) {

        Optional<ScheduleSetting> result = scheduleSettingRepository.findById(id);
        CrudRes response = new CrudRes();

        if (result.isPresent()) {
            ScheduleSetting scheduleSetting = result.get();
            ScheduleSettingRes scheduleSettingRes = new ScheduleSettingRes();
            BeanUtils.copyProperties(scheduleSetting, scheduleSettingRes);
            scheduleSettingRes.setCompanyId(scheduleSetting.getCompany().getId());

            response.setResponse(true);
            response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
            response.setBody(scheduleSettingRes);
            return response;

        } else {
            throw new DefaultTransactionException(
                ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO,
                ErrorEntities.SCHEDULE_SETTING,
                ErrorKeys.ERR_RECORD_NOT_FOUND);
        }
    }
}
