package bo.com.luminia.sflbilling.msbatch.service;

import bo.com.luminia.sflbilling.msbatch.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msbatch.repository.WrapperLogRepository;
import bo.com.luminia.sflbilling.domain.WrapperLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class WrapperLogService {

    private final WrapperLogRepository wrapperLogRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public WrapperLog create(Integer syncType, ZonedDateTime startDate, Long companyId, Boolean response, String description) {

        WrapperLog entity = new WrapperLog();
        entity.setWrapperType(syncType);
        entity.setStartDate(startDate);
        entity.setEndDate(ZonedDateTime.now());
        entity.setCompany(companyRepository.getById(companyId));
        entity.setResponse(response);
        entity.setDescription(description);

        entity = wrapperLogRepository.save(entity);
        log.info("Log creado para el envio masivo: {}", entity);

        return entity;
    }
}
