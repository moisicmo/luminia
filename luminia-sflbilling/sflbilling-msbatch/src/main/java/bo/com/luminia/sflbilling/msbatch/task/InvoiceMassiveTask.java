package bo.com.luminia.sflbilling.msbatch.task;

import bo.com.luminia.sflbilling.msbatch.repository.ScheduleSettingRepository;
import bo.com.luminia.sflbilling.msbatch.service.InvoiceMassiveService;
import bo.com.luminia.sflbilling.msbatch.service.WrapperLogService;
import bo.com.luminia.sflbilling.msbatch.service.constants.InvoiceMassiveType;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.domain.ScheduleSetting;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.InvoiceMassiveReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.InvoiceMassiveRes;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class InvoiceMassiveTask implements SchedulingConfigurer {

    private final ScheduleSettingRepository scheduleSettingRepository;
    private final InvoiceMassiveService invoiceMassiveService;
    private final WrapperLogService wrapperLogService;

    TaskScheduler taskScheduler;
    private Map<Long, ScheduledFuture<?>> jobMap = new HashMap<>();

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {

        List<ScheduleSetting> scheduleSettingList = scheduleSettingRepository.findAllByActiveTrue();
        int poolSize = scheduleSettingList.size() > 0 ? scheduleSettingList.size() : 1;
        log.debug("Cantidad de hilos invoice-massive-thread: {}", poolSize);
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        // Establece la cantidad de hilos en función a la cantidad de empresas.
        threadPoolTaskScheduler.setPoolSize(poolSize);
        threadPoolTaskScheduler.setThreadNamePrefix("invoice-massive-thread");
        threadPoolTaskScheduler.initialize();

        // Itera la lista de configuraciones del calendario.
        for (ScheduleSetting schedule : scheduleSettingList) {
            this.addJob(schedule, threadPoolTaskScheduler);
        }

        this.taskScheduler = threadPoolTaskScheduler;
    }

    /**
     * Método que crea un trabajo.
     *
     * @param schedule Configuración del horario.
     */
    public void createJob(ScheduleSetting schedule) {
        this.addJob(schedule, taskScheduler);
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
     * @param schedule  Programador de tareas.
     * @param scheduler Configuración del horario.
     */
    private void addJob(ScheduleSetting schedule, TaskScheduler scheduler) {
        this.jobMap.put(schedule.getId(), scheduler.schedule(() -> {
            try {
                log.debug(Thread.currentThread().getName() + " Job " + schedule.getId() + " " + new Date());
                InvoiceMassiveReq invoiceMassiveReq = new InvoiceMassiveReq();
                invoiceMassiveReq.setCompanyId(schedule.getCompany().getId());
                invoiceMassiveReq.setScheduleSettingId(schedule.getId());

                ZonedDateTime startDate = ZonedDateTime.now();
                InvoiceMassiveRes response = invoiceMassiveService.emitMassive(invoiceMassiveReq);
                // Registra el log de sincronización.
                wrapperLogService.create(InvoiceMassiveType.SYNC_MASSIVE,
                    startDate,
                    invoiceMassiveReq.getCompanyId(),
                    response.getCode().equals(SiatResponseCodes.SUCCESS),
                    response.getMessage());

                Thread.sleep(10000);
                invoiceMassiveService.validateMassive(schedule.getCompany().getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, triggerContext -> {
            String cronExp = schedule.getCronDate();
            return new CronTrigger(cronExp).nextExecutionTime(triggerContext);
        }));
    }
}
