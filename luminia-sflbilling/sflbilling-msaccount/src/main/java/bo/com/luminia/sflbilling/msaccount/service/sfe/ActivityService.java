package bo.com.luminia.sflbilling.msaccount.service.sfe;

import bo.com.luminia.sflbilling.msaccount.repository.ActivityRepository;
import bo.com.luminia.sflbilling.domain.Activity;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.ActivityRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.DomainRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ActivityService {

    private final ActivityRepository repository;

    public DomainRes getActivity(final Integer idCompany) {
        List<Activity> responseFromDb = repository.findAllByCompanyIdAndActiveIsTrue(idCompany);
        DomainRes response = new DomainRes();

        if (!responseFromDb.isEmpty()) {
            List<ActivityRes> responseList = responseFromDb.stream().map(e -> convert(e)).collect(Collectors.toList());
            response.setCode(ResponseCodes.SUCCESS);
            response.setMessage(ResponseMessages.SUCCESS_OPERACION_EXITOSA);
            response.setBody(responseList);
            return response;
        } else {
            response.setBody(new ArrayList<>());
            response.setCode(ResponseCodes.WARNING);
            response.setMessage(ResponseMessages.WARNING_LISTA_VACIA);
        }

        return response;
    }

    private ActivityRes convert(Activity e) {
        ActivityRes response = new ActivityRes();
        BeanUtils.copyProperties(e, response);
        return response;
    }
}
