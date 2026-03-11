package bo.com.luminia.sflbilling.msbatch.task;

import bo.com.luminia.sflbilling.msbatch.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msbatch.repository.SyncSettingRepository;
import bo.com.luminia.sflbilling.msbatch.service.SyncLogService;
import bo.com.luminia.sflbilling.msbatch.service.SyncParameterService;
import bo.com.luminia.sflbilling.msbatch.service.constants.SyncType;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.SyncSetting;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.SyncParameterReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.SyncRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class SyncParametersTask implements SchedulingConfigurer {

    private final SyncSettingRepository syncSettingRepository;
    private final CompanyRepository companyRepository;

    private final SyncParameterService syncParameterService;
    private final SyncLogService syncLogService;

    TaskScheduler taskScheduler;
    private Map<Long, ScheduledFuture<?>> jobMap = new HashMap<>();

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        List<SyncSetting> syncSettingList = syncSettingRepository.findAllBySyncTypeAndActiveTrue(SyncType.SYNC_PARAMETERS);
        int poolSize = syncSettingList.size() > 0 ? syncSettingList.size() : 1;

        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        // Establece la cantidad de hilos en función a la cantidad de registros.
        threadPoolTaskScheduler.setPoolSize(poolSize);
        threadPoolTaskScheduler.setThreadNamePrefix("sync-parameters-thread");
        threadPoolTaskScheduler.initialize();

        // Itera la lista de configuraciones del calendario.
        for (SyncSetting syncSetting : syncSettingList) {
            this.addJob(syncSetting, threadPoolTaskScheduler);
        }

        this.taskScheduler = threadPoolTaskScheduler;
    }

    /**
     * Método que crea un trabajo.
     *
     * @param syncSetting Configuración del horario.
     */
    public void createJob(SyncSetting syncSetting) {
        this.addJob(syncSetting, taskScheduler);
    }

    /**
     * Método que elimina un trabajo.
     *
     * @param id Id de la tarea.
     */
    public void removeJob(Long id) {
        if (this.jobMap.get(id) != null) {
            this.jobMap.get(id).cancel(true);
            this.jobMap.remove(id);
        }
    }

    /**
     * Método que adiciona un trabajo al programador de tareas.
     *
     * @param syncSetting Programador de tareas.
     * @param scheduler   Configuración del horario.
     */
    private void addJob(SyncSetting syncSetting, TaskScheduler scheduler) {
        this.jobMap.put(syncSetting.getId(), scheduler.schedule(() -> {
            try {
                log.debug(Thread.currentThread().getName() + " Job " + syncSetting.getId() + " " + new Date());
                ZonedDateTime startDate = ZonedDateTime.now();

                Optional<Company> company = companyRepository.findById(syncSetting.getCompany().getId());
                SyncParameterReq syncParameterReq = new SyncParameterReq();
                syncParameterReq.setCompanyId(company.get().getId());
                syncParameterReq.setBranchOfficeSiat(0);
                syncParameterReq.setPointSaleSiat(0);
                SyncRes syncRes = syncParameterService.synchronize(syncParameterReq, company.get());

                // Registra el log de sincronización.
                syncLogService.create(SyncType.SYNC_PARAMETERS,
                    startDate,
                    syncSetting.getCompany().getId(),
                    syncRes.getResponse(),
                    syncRes.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, triggerContext -> {
            String cronExp = syncSetting.getCronDate();
            return new CronTrigger(cronExp).nextExecutionTime(triggerContext);
        }));
    }
}
