package bo.com.luminia.sflbilling.msbatch.service;

import bo.com.luminia.sflbilling.msbatch.repository.SyncLogRepository;
import bo.com.luminia.sflbilling.domain.SyncLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class SyncLogService {

    private final SyncLogRepository syncLogRepository;

    @Transactional
    public SyncLog create(Integer syncType, ZonedDateTime startDate, Long companyId, Boolean response, String description) {
        SyncLog entity = new SyncLog();
        entity.setSyncType(syncType);
        entity.setStartDate(startDate);
        entity.setEndDate(ZonedDateTime.now());
        entity.setCompanyId(companyId);
        entity.setResponse(response);
        entity.setDescription(description);

        entity = syncLogRepository.save(entity);
        log.info("Log creado para la sincronización: {}", entity);

        return entity;
    }
}
