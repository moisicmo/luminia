package bo.com.luminia.sflbilling.msaccount.service.sfe;

import bo.com.luminia.sflbilling.msaccount.repository.MeasurementUnitRepository;
import bo.com.luminia.sflbilling.domain.MeasurementUnit;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.DomainRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.MeasurementUnitRes;
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
public class MeasurementUnitService {

    private final MeasurementUnitRepository repository;

    public DomainRes getMeasurementUnit(final Integer companyId) {

        List<MeasurementUnit> responseFromDb = repository.findAllByCompanyIdAndActiveIsTrue(companyId);
        DomainRes response = new DomainRes();

        if (!responseFromDb.isEmpty()) {
            List<MeasurementUnitRes> responseList = responseFromDb.stream().map(e -> convert(e)).collect(Collectors.toList());
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

    private MeasurementUnitRes convert(MeasurementUnit e) {
        MeasurementUnitRes response = new MeasurementUnitRes();
        BeanUtils.copyProperties(e, response);
        return response;
    }
}
