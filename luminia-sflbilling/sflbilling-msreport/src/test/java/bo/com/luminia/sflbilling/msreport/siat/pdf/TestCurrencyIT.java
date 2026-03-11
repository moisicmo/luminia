package bo.com.luminia.sflbilling.msreport.siat.pdf;

import bo.com.luminia.sflbilling.domain.CurrencyType;
import bo.com.luminia.sflbilling.msreport.repository.CurrencyTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
public class TestCurrencyIT {

    @Autowired
    CurrencyTypeRepository repository ;

    @Test
    public void testCurrency(){

        Optional<CurrencyType> result = repository.findCurrencyTypeBySiatIdAndActiveIsTrue(2);
        assertThat(result.get()).isNotNull();

    }
}
