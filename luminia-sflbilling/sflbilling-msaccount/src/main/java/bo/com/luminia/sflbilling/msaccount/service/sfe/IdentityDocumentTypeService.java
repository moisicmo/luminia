package bo.com.luminia.sflbilling.msaccount.service.sfe;

import bo.com.luminia.sflbilling.msaccount.repository.IdentityDocumentTypeRepository;
import bo.com.luminia.sflbilling.domain.IdentityDocumentType;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.DomainRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.IdentityDocumentTypeRes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class IdentityDocumentTypeService {

    private final IdentityDocumentTypeRepository repository;

    public DomainRes getIdentityDocumentType(final Integer idCompany) {
        List<IdentityDocumentType> responseFromDb = repository.findAllByCompanyIdAndActiveIsTrue(idCompany);
        DomainRes response = new DomainRes();

        if (!responseFromDb.isEmpty()) {
            List<IdentityDocumentTypeRes> responseList = responseFromDb.stream().map(e -> convert(e)).collect(Collectors.toList());
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

    private IdentityDocumentTypeRes convert(IdentityDocumentType e) {
        IdentityDocumentTypeRes response = new IdentityDocumentTypeRes();
        BeanUtils.copyProperties(e, response);
        return response;
    }

}
