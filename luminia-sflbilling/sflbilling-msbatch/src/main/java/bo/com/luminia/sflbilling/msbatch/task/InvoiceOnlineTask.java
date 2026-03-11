package bo.com.luminia.sflbilling.msbatch.task;

import bo.com.luminia.sflbilling.msbatch.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msbatch.service.InvoicePackService;
import bo.com.luminia.sflbilling.msbatch.service.WrapperLogService;
import bo.com.luminia.sflbilling.msbatch.service.constants.InvoiceMassiveType;
import bo.com.luminia.sflbilling.msbatch.service.constants.SiatResponseCodes;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.InvoicePackReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.InvoiceOnlineRes;
import bo.com.luminia.sflbilling.msbatch.web.rest.response.InvoicePackRes;
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
public class InvoiceOnlineTask implements SchedulingConfigurer {

    private final CompanyRepository companyRepository;

    private final InvoicePackService invoicePackService;
    private final WrapperLogService wrapperLogService;

    TaskScheduler taskScheduler;
    private Map<Long, ScheduledFuture<?>> jobMap = new HashMap<>();

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        int poolSize = 10;
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        // Establece la cantidad de hilos en función a la cantidad de empresas habilitadas.
        threadPoolTaskScheduler.setPoolSize(poolSize);
        threadPoolTaskScheduler.setThreadNamePrefix("invoice-offline-thread");
        threadPoolTaskScheduler.initialize();

        this.taskScheduler = threadPoolTaskScheduler;
    }

    /**
     * Método que crea los paquetes para las empresas.
     */
    public InvoiceOnlineRes createWrapperOnline() {
        List<Company> companyList = companyRepository.findAllByEventSendIsTrueAndActiveIsTrue();
        // Itera la lista de empresas habilitadas para la emisión offline.
        for (Company company : companyList) {
            this.createWrappersOnline(company, this.taskScheduler);
        }
        return new InvoiceOnlineRes(SiatResponseCodes.SUCCESS, "Sistema habilitado para emisión En Linea.");
    }

    private void createWrappersOnline(Company company, TaskScheduler taskScheduler) {
        try {
            log.debug(Thread.currentThread().getName() + " Job Wrapper Online " + company.getId() + " " + new Date());
            InvoicePackReq invoiceMassiveReq = new InvoicePackReq();
            invoiceMassiveReq.setCompanyId(company.getId());

            ZonedDateTime startDate = ZonedDateTime.now();
            InvoicePackRes response = invoicePackService.emitPack(invoiceMassiveReq);
            // Registra el log de sincronización.
            wrapperLogService.create(InvoiceMassiveType.SYNC_OFFLINE,
                startDate,
                invoiceMassiveReq.getCompanyId(),
                response.getCode().equals(SiatResponseCodes.SUCCESS),
                response.getMessage());

            Thread.sleep(10000);
            invoicePackService.validatePack(company.getId(), true);

            this.removeJob(company.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que crea un trabajo.
     */
    public InvoiceOnlineRes createJobs() {
        List<Company> companyList = companyRepository.findAllByEventSendIsTrueAndActiveIsTrue();
        // Itera la lista de configuraciones del calendario.
        for (Company company : companyList) {
            this.addJob(company, this.taskScheduler);
        }
        return new InvoiceOnlineRes(SiatResponseCodes.SUCCESS, "Sistema habilitado para emisión En Linea.");
    }

    /**
     * Método que crea un trabajo.
     */
    public InvoiceOnlineRes createJobs(Company company) {
        this.addJob(company, this.taskScheduler);
        return new InvoiceOnlineRes(SiatResponseCodes.SUCCESS, "Sistema habilitado para emisión En Linea.");
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
     * @param company   Programador de tareas.
     * @param scheduler Configuración del horario.
     */
    private void addJob(Company company, TaskScheduler scheduler) {
        this.jobMap.put(company.getId(), scheduler.schedule(() -> {
            try {
                log.debug(Thread.currentThread().getName() + " Job " + company.getId() + " " + new Date());
                InvoicePackReq invoiceMassiveReq = new InvoicePackReq();
                invoiceMassiveReq.setCompanyId(company.getId());

                ZonedDateTime startDate = ZonedDateTime.now();
                InvoicePackRes response = invoicePackService.emitPack(invoiceMassiveReq);
                // Registra el log de sincronización.
                wrapperLogService.create(InvoiceMassiveType.SYNC_OFFLINE,
                    startDate,
                    invoiceMassiveReq.getCompanyId(),
                    response.getCode().equals(SiatResponseCodes.SUCCESS),
                    response.getMessage());

                Thread.sleep(10000);
                invoicePackService.validatePack(company.getId(), false);

                this.removeJob(company.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, triggerContext -> {
            String cronExp = "0 */1 * ? * *";
            return new CronTrigger(cronExp).nextExecutionTime(triggerContext);
        }));
    }
}
