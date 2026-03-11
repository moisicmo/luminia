package bo.com.luminia.sflbilling.msbatch.task;

import bo.com.luminia.sflbilling.msbatch.siat.service.SiatBuySellService;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.CheckStatusExistsPendingException;
import bo.com.luminia.sflbilling.msbatch.web.rest.errors.NotFoundAlertException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class CheckInvoiceSiatTask {

    private final SiatBuySellService service;


    @Scheduled(cron = "${batch-revalidate-invoices.cron}")
    public void execute() {
        ProcessThread thread = new ProcessThread();
        thread.start();
    }


    class ProcessThread extends Thread{
        @Override
        public void run() {
            try {
                service.process();
            } catch (NotFoundAlertException e) {
                //no hago nada, puede ser intermitencia
            } catch (CheckStatusExistsPendingException e) {
                //no hago nada, puede ser intermitencia
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
