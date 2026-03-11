package bo.com.luminia.sflbilling.msaccount.service.sfe;

import bo.com.luminia.sflbilling.msaccount.repository.PaymentMethodTypeRepository;
import bo.com.luminia.sflbilling.domain.PaymentMethodType;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.DomainRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.PaymentMethodTypeRes;
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
public class PaymentMethodTypeService {
    private final PaymentMethodTypeRepository repository;

    public DomainRes getPaymentMethodType(final Integer idCompany) {
        List<PaymentMethodType> responseFromDb = repository.findAllByCompanyIdAndActiveIsTrue(idCompany);
        DomainRes response = new DomainRes();

        if (!responseFromDb.isEmpty()) {
            List<PaymentMethodTypeRes> responseList = responseFromDb.stream().map(e -> convert(e)).collect(Collectors.toList());
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

    private PaymentMethodTypeRes convert(PaymentMethodType e) {
        PaymentMethodTypeRes response = new PaymentMethodTypeRes();
        BeanUtils.copyProperties(e, response);
        return response;
    }
}
