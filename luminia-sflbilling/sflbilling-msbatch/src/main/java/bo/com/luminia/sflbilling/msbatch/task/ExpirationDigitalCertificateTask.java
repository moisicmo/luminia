package bo.com.luminia.sflbilling.msbatch.task;

import bo.com.luminia.sflbilling.msbatch.service.ExpirationDigitalCertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class ExpirationDigitalCertificateTask {

    private final ExpirationDigitalCertificateService service;

    @Scheduled(cron = "${expiration.cron}")
    public void execute() {
        log.info("Ejecutando Cron de Expiracion de Certificados...");
        service.process();
    }

}
