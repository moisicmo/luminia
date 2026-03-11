package bo.com.luminia.sflbilling.msbatch.web.rest.certification;

import bo.com.luminia.sflbilling.msbatch.web.rest.request.SyncParameterReq;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@SpringBootTest
public class TestSincronizacionCatalogosCmpIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPunto1() throws Exception {

        int numberTests = 50;
        long companyId = 2;

        // Sincroniza los códigos CUIS y CUFD.
        SyncParameterReq request = new SyncParameterReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(1);

        // Itera el numero de pruebas requeridas.
        for (int number = 1; number <= numberTests; number++) {
            mockMvc
                .perform(post("/api/sync/parameters")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isOk());
        }
    }

    @Test
    void testPunto2() throws Exception {

        int numberTests = 50;
        long companyId = 2;

        // Sincroniza los códigos CUIS y CUFD.
        SyncParameterReq request = new SyncParameterReq();
        request.setCompanyId(companyId);
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(0);

        // Itera el numero de pruebas requeridas.
        for (int number = 1; number <= numberTests; number++) {
            mockMvc
                .perform(post("/api/sync/parameters")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isOk());
        }
    }
}
