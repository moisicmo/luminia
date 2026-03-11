package bo.com.luminia.sflbilling.msaccount.service.sfe;

import bo.com.luminia.sflbilling.msaccount.repository.CurrencyTypeRepository;
import bo.com.luminia.sflbilling.domain.CurrencyType;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.CurrencyTypeRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.DomainRes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class CurrencyTypeService {
    private final CurrencyTypeRepository repository;

    public DomainRes getCurrencyType(final Integer idCompany) {
        List<CurrencyType> responseFromDb = repository.findAllByCompanyIdAndActiveIsTrue(idCompany);
        DomainRes response = new DomainRes();

        if (!responseFromDb.isEmpty()) {
            List<CurrencyTypeRes> responseList = responseFromDb.stream()
                .map(e -> convert(e)).collect(Collectors.toList());
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

    private CurrencyTypeRes convert(CurrencyType e) {
        CurrencyTypeRes response = new CurrencyTypeRes();
        BeanUtils.copyProperties(e, response);
        return response;
    }
}
