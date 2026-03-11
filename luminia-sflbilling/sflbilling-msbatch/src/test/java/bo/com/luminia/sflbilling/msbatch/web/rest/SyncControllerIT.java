package bo.com.luminia.sflbilling.msbatch.web.rest;

import bo.com.luminia.sflbilling.msbatch.repository.BranchOfficeRepository;
import bo.com.luminia.sflbilling.msbatch.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msbatch.repository.PointSaleRepository;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.SyncCodeReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.request.SyncParameterReq;
import bo.com.luminia.sflbilling.msbatch.web.rest.util.TestUtil;
import bo.com.luminia.sflbilling.domain.BranchOffice;
import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.PointSale;
import bo.com.luminia.sflbilling.domain.enumeration.EnvironmentSiatEnum;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import bo.com.luminia.sflbilling.msbatch.IntegrationTest;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@IntegrationTest
public class SyncControllerIT {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BranchOfficeRepository branchOfficeRepository;
    @Autowired
    private PointSaleRepository pointSaleRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSyncParameters() throws Exception {
        // Registra la empresa por defecto.
        Company entity = new Company();
        entity.setId(1L);
        entity.setNit(1028675024L);
        entity.setName("LUMINIA");
        entity.setBusinessName("LUMINIA");
        entity.setCity("BOLIVIA");
        entity.setPhone("78859964");
        entity.setAddress("CALLE 123");
        entity.setSystemCode("6D16CF15469A277372938EF");
        entity.setPackageSend(false);
        entity.setEventSend(true);
        entity.setToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTSU5URVNJMjAyMCIsImNvZGlnb1Npc3RlbWEiOiI2RDE2Q0YxNTQ2OUEyNzczNzI5MzhFRiIsIm5pdCI6Ikg0c0lBQUFBQUFBQUFETTBNTEl3TXpjMU1ESUJBSFl6bHZnS0FBQUEiLCJpZCI6MTA3NzE5MCwiZXhwIjoxNjQwOTA4ODAwLCJpYXQiOjE2MzQyNTgzNDMsIm5pdERlbGVnYWRvIjoxMDI4Njc1MDI0LCJzdWJzaXN0ZW1hIjoiU0ZFIn0._oK7_1p_blcnEMzw0GM8Ps0cO2o0ZL4VtXakQZG9VM9pQbH8o1b3h58l3dzcIiKPz6sYyGxrnNQ18kwKNr2Cwg");
        entity.setEnvironmentSiat(EnvironmentSiatEnum.TEST);
        entity.setModalitySiat(ModalitySiatEnum.ELECTRONIC_BILLING);
        Company result = companyRepository.saveAndFlush(entity);

        // Sincroniza las parametricas.
        SyncParameterReq request = new SyncParameterReq();
        request.setCompanyId(result.getId());
        request.setBranchOfficeSiat(0);
        request.setPointSaleSiat(0);

        mockMvc
            .perform(post("/api/sync/parameters").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(request)))
            .andExpect(status().isOk());
    }

    @Test
    void testSyncCodes() throws Exception {
        // Registra la empresa por defecto.
        Company company = new Company();
        company.setId(1L);
        company.setNit(1028675024L);
        company.setName("LUMINIA");
        company.setBusinessName("LUMINIA");
        company.setCity("BOLIVIA");
        company.setPhone("78859964");
        company.setAddress("CALLE 123");
        company.setSystemCode("6CE0F4E55826C2DB97038EF");
        company.setPackageSend(false);
        company.setEventSend(true);
        company.setToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTSU5URVNJMjAyMCIsImNvZGlnb1Npc3RlbWEiOiI2RDE2Q0YxNTQ2OUEyNzczNzI5MzhFRiIsIm5pdCI6Ikg0c0lBQUFBQUFBQUFETTBNTEl3TXpjMU1ESUJBSFl6bHZnS0FBQUEiLCJpZCI6MTA3NzE5MCwiZXhwIjoxNjQwOTA4ODAwLCJpYXQiOjE2MzQyNTgzNDMsIm5pdERlbGVnYWRvIjoxMDI4Njc1MDI0LCJzdWJzaXN0ZW1hIjoiU0ZFIn0._oK7_1p_blcnEMzw0GM8Ps0cO2o0ZL4VtXakQZG9VM9pQbH8o1b3h58l3dzcIiKPz6sYyGxrnNQ18kwKNr2Cwg");
        company.setEnvironmentSiat(EnvironmentSiatEnum.TEST);
        company.setModalitySiat(ModalitySiatEnum.ELECTRONIC_BILLING);
        Company companyNew = companyRepository.saveAndFlush(company);

        // Registra la sucursal matriz.
        BranchOffice branchOffice = new BranchOffice();
        branchOffice.setId(1L);
        branchOffice.setBranchOfficeSiatId(0);
        branchOffice.setName("SUCURSAL MATRIZ");
        branchOffice.setDescription("LUMINIA SUCURSAL MATRIZ");
        branchOffice.setActive(true);
        branchOffice.setCompany(companyNew);
        BranchOffice branchOfficeNew = branchOfficeRepository.saveAndFlush(branchOffice);

        // Registro de los puntos de venta.
        PointSale pointSale0 = new PointSale();
        pointSale0.setId(1L);
        pointSale0.setPointSaleSiatId(0);
        pointSale0.setName("PUNTO VENTA 0");
        pointSale0.setDescription("PUNTO VENTA 0");
        pointSale0.setActive(true);
        pointSale0.setBranchOffice(branchOfficeNew);
        pointSaleRepository.saveAndFlush(pointSale0);

        PointSale pointSale1 = new PointSale();
        pointSale1.setId(2L);
        pointSale1.setPointSaleSiatId(1);
        pointSale1.setName("PUNTO VENTA 1");
        pointSale1.setDescription("PUNTO VENTA 1");
        pointSale1.setActive(true);
        pointSale1.setBranchOffice(branchOfficeNew);
        pointSaleRepository.saveAndFlush(pointSale1);

        PointSale pointSale2 = new PointSale();
        pointSale2.setId(3L);
        pointSale2.setPointSaleSiatId(2);
        pointSale2.setName("PUNTO VENTA 2");
        pointSale2.setDescription("PUNTO VENTA 2");
        pointSale2.setActive(true);
        pointSale2.setBranchOffice(branchOfficeNew);
        pointSaleRepository.saveAndFlush(pointSale2);

        // Sincroniza los códigos CUIS y CUFD.
        SyncCodeReq request = new SyncCodeReq();
        request.setCompanyId(companyNew.getId());

        mockMvc
            .perform(post("/api/sync/codes").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(request)))
            .andExpect(status().isOk());
    }
}
