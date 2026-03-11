package bo.com.luminia.sflbilling.msaccount.service.sfe.crud;

import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.enumeration.EnvironmentSiatEnum;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import bo.com.luminia.sflbilling.msaccount.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msaccount.service.sfe.CompanyService;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.CompanyCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.CompanyUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.CrudRes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CompanyCrudTestIT {
    @Autowired
    private CompanyService service;

    @Autowired
    private CompanyRepository repository ;

    private CompanyCreateReq create() {
        CompanyCreateReq response = new CompanyCreateReq();
        response.setNit(1028675024L);
        response.setName("LUMINIA");
        response.setBusinessName("LUMINIA");
        response.setCity("Santa Cruz");
        response.setAddress("2do Anillo Av Alemana");
        response.setPhone("34343434");
        response.setSystemCode("6CE0F4E1DC78AA3DBC498EF");
        response.setPackageSend(true);
        response.setEventSend(true);
        response.setEmailNotification("empresa@mail.com");
        response.setToken("eyJ0eXAiOiJKV1Qi");
        response.setEnvironmentSiat(EnvironmentSiatEnum.TEST.getKey());
        response.setModalitySiat(ModalitySiatEnum.ELECTRONIC_BILLING.getKey());

        return response;

    }

    private CompanyUpdateReq update() {
        Optional<Company> c = repository.findById(2L);

        CompanyUpdateReq response = new CompanyUpdateReq();
        response.setId(c.get().getId());
        /*response.setNit(1028675024L);
        response.setName("LUMINIA");
        response.setBusinessName("LUMINIA");
        response.setCity("Santa Cruz");
        response.setAddress("2do Anillo Av Alemana Esq Yomomo");
        response.setPhone("34343434");
        response.setSystemCode("6CE0F4E1DC78AA3DBC498EF");
        response.setPackageSend(true);
        response.setEventSend(true);
        response.setEmailNotification("empresa@mail.com");
        response.setUserSiat("LUMINIA2020");*/
        response.setToken("eyJ0eXAiOiJKV1Qi");

        //response.setEnvironmentSiat(EnvironmentSiatEnum.TEST.getKey());
        //response.setModalitySiat(ModalitySiatEnum.ELECTRONIC_BILLING.getKey());*/

        return response;
    }

    @Test
    public void testGet() {
        CrudRes response = service.get(new Long(1)) ;

        assertThat(response.getCode()).isEqualTo(ResponseCodes.SUCCESS);
    }

    @Test
    public void testPost() {
        CrudRes response = service.create(create()) ;
        assertThat(response.getCode()).isEqualTo(ResponseCodes.SUCCESS) ;
    }

    @Test
    public void testPut () {

        CompanyUpdateReq updateReq = update() ;

        CrudRes response = service.update(updateReq) ;
        assertThat(response.getCode()).isEqualTo(ResponseCodes.SUCCESS) ;
    }

}
