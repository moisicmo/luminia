package bo.com.luminia.sflbilling.msaccount.web.rest;

import bo.com.luminia.sflbilling.domain.CurrencyType;
import bo.com.luminia.sflbilling.msaccount.repository.CurrencyTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CurrencyTestIT {

    @Autowired
    private CurrencyTypeRepository repository ;

    @Test
    public void test(){
        List<CurrencyType> fromDb = repository.findAll();

        assertThat(fromDb.size()).isGreaterThan(0);
    }
}
