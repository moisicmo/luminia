package bo.com.luminia.sflbilling.msbatch.task;

import bo.com.luminia.sflbilling.msbatch.service.InvoiceBatchValidatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class InvoiceBatchValidatorTask {

    private final InvoiceBatchValidatorService service;

    @Scheduled(cron = "${batch-validate.cron}")
    public void execute() {
        service.batchValidate();
    }
}
