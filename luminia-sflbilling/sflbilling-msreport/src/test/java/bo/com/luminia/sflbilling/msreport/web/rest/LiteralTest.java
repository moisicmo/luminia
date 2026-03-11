package bo.com.luminia.sflbilling.msreport.web.rest;

import bo.com.luminia.sflbilling.msreport.siat.pdf.literal.Spanish;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LiteralTest {

    @Test
    public void convertLiteral() {
        Spanish word = new Spanish();

        assertThat(word.convert(100)).isNotNull();
        assertThat(word.convert(100)).isEqualTo("cien") ;
    }
}
