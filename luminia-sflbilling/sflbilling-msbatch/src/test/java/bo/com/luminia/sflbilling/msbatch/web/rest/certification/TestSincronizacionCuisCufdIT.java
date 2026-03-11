package bo.com.luminia.sflbilling.msbatch.web.rest.certification;

import bo.com.luminia.sflbilling.msbatch.web.rest.request.SyncCodeReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.TestUtil;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@SpringBootTest
public class TestSincronizacionCuisCufdIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test() throws Exception {

        int numberTests = 100;
        long companyId = 1001;

        // Instancia objeto de sincronización.
        SyncCodeReq request = new SyncCodeReq();
        request.setCompanyId(companyId);

        // Itera el numero de pruebas requeridas.
        for (int number = 1; number <= numberTests; number++) {
            mockMvc
                .perform(post("/api/sync/codes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(true));
        }
    }
}
