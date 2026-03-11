package bo.com.luminia.sflbilling.msbatch.task;

import bo.com.luminia.sflbilling.msbatch.service.OnlinePackValidateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class OnlinePackValidateTask {

    private final OnlinePackValidateService service;

    @Scheduled(cron = "${online.pack-cron}")
    public void executePack() {
        ProcessPackThread thread = new ProcessPackThread();
        thread.start();
    }

    @Scheduled(cron = "${online.validate-cron}")
    public void executeValidate() {
        ProcessValidateThread thread = new ProcessValidateThread();
        thread.start();
    }



    class ProcessPackThread extends Thread {
        @Override
        public void run() {
            service.reduceEventstSize();
            service.processPack();
        }
    }

    class ProcessValidateThread extends Thread {
        @Override
        public void run() {
            service.processValidate();
        }
    }



}
