package bo.com.luminia.sflbilling.msbatch.task;

import bo.com.luminia.sflbilling.msbatch.service.OfflineValidatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class OfflineValidatorTask {
    private final OfflineValidatorService service;

    @Scheduled(cron = "${offline.cron}")
    public void execute() {
        ProcessThread thread = new ProcessThread();
        thread.start();
    }


    class ProcessThread extends Thread {
        @Override
        public void run() {
            service.process();
        }
    }

}
