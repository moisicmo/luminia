package bo.com.luminia.sflbilling.msaccount.service.sfe;

import bo.com.luminia.sflbilling.msaccount.repository.InvoiceLegendRepository;
import bo.com.luminia.sflbilling.domain.InvoiceLegend;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseMessages;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.DomainRes;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe.InvoiceLegendRes;
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
public class InvoiceLegendService {

    private final InvoiceLegendRepository repository;

    public DomainRes getInvoiceLegend(final Integer idCompany, final Integer activityCode) {
        List<InvoiceLegend> responseFromDb = repository.findAllByCompanyIdAndActivityCodeAndActiveIsTrue(idCompany, activityCode);
        DomainRes response = new DomainRes();

        if (!responseFromDb.isEmpty()) {
            List<InvoiceLegendRes> responseList = responseFromDb.stream().map(e -> convert(e)).collect(Collectors.toList());
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

    private InvoiceLegendRes convert(InvoiceLegend e) {
        InvoiceLegendRes response = new InvoiceLegendRes();
        BeanUtils.copyProperties(e, response);
        return response;
    }
}
