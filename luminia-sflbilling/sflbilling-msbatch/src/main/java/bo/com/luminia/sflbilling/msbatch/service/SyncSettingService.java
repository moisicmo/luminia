package bo.com.luminia.sflbilling.msbatch.service;

import bo.com.luminia.sflbilling.msbatch.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msbatch.repository.SyncSettingRepository;
import bo.com.luminia.sflbilling.msbatch.service.constants.SyncType;
import bo.com.luminia.sflbilling.domain.SyncSetting;
import bo.com.luminia.sflbilling.msbatch.task.SyncCodesTask;
import bo.com.luminia.sflbilling.msbatch.task.SyncParametersTask;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorEntities;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.ErrorKeys;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.spec.DefaultTransactionException;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.SyncSettingCreateReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.SyncSettingUpdateReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.CrudRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.SyncSettingRes;
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
public class SyncSettingService {

    private final SyncSettingRepository syncSettingRepository;
    private final CompanyRepository companyRepository;

    private final SyncParametersTask syncParametersTask;
    private final SyncCodesTask syncCodesTask;

    @Transactional
    public SyncSetting create(SyncSettingCreateReq request) {
        SyncSetting entity = new SyncSetting();
        entity.setSyncType(request.getSyncType());
        entity.setCronDate(request.getCronDate());
        entity.setDescription(request.getDescription());
        entity.setActive(true);
        entity.setCompany(companyRepository.getById(request.getCompanyId()));

        SyncSetting entityNew = syncSettingRepository.save(entity);

        // Inicia la tarea de sincronización.
        if (request.getSyncType().equals(SyncType.SYNC_CODES)) {
            syncCodesTask.createJob(entityNew);
        } else {
            syncParametersTask.createJob(entityNew);
        }
        log.info("Información registrada para syncSetting: {}", entityNew);
        return entityNew;
    }

    @Transactional
    public Optional<SyncSetting> update(SyncSettingUpdateReq request) {
        return Optional
            .of(syncSettingRepository.findById(request.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(entity -> {
                // Elimina la tarea de sincronización.
                if (entity.getSyncType().equals(SyncType.SYNC_CODES)) {
                    syncCodesTask.removeJob(entity.getId());
                } else {
                    syncParametersTask.removeJob(entity.getId());
                }

                entity.setSyncType(request.getSyncType());
                entity.setCronDate(request.getCronDate());
                entity.setDescription(request.getDescription());
                entity.setActive(request.getActive());
                syncSettingRepository.save(entity);

                if (request.getActive()) {
                    // Inicia la tarea de sincronización.
                    if (request.getSyncType().equals(SyncType.SYNC_CODES)) {
                        syncCodesTask.createJob(entity);
                    } else {
                        syncParametersTask.createJob(entity);
                    }
                }

                log.info("Información actualizada para syncSetting: {}", entity);
                return entity;
            });
    }

    @Transactional
    public CrudRes getAll() {
        List<SyncSetting> result = syncSettingRepository.findAll();
        CrudRes response = new CrudRes();
        List<SyncSettingRes> syncSettingResList = new ArrayList<>();

        for (SyncSetting syncSetting : result) {
            SyncSettingRes syncSettingRes = new SyncSettingRes();
            BeanUtils.copyProperties(syncSetting, syncSettingRes);
            syncSettingRes.setCompanyId(syncSetting.getCompany().getId());
            syncSettingResList.add(syncSettingRes);
        }

        response.setResponse(true);
        response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
        response.setBody(syncSettingResList);
        return response;
    }

    @Transactional
    public CrudRes get(Long id) {
        Optional<SyncSetting> result = syncSettingRepository.findById(id);
        CrudRes response = new CrudRes();

        if (result.isPresent()) {
            SyncSetting syncSetting = result.get();
            SyncSettingRes syncSettingRes = new SyncSettingRes();
            BeanUtils.copyProperties(syncSetting, syncSettingRes);
            syncSettingRes.setCompanyId(syncSetting.getCompany().getId());

            response.setResponse(true);
            response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
            response.setBody(syncSettingRes);
            return response;

        } else {
            throw new DefaultTransactionException(
                ResponseMessages.ERROR_REGISTRO_NO_ENCONTRADO,
                ErrorEntities.SYNC_SETTING,
                ErrorKeys.ERR_RECORD_NOT_FOUND);
        }
    }
}
