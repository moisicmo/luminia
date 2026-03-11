package bo.com.luminia.sflbilling.msinvoice.certificacion;

import bo.com.luminia.sflbilling.domain.Company;
import bo.com.luminia.sflbilling.domain.Cuis;
import bo.com.luminia.sflbilling.msinvoice.repository.CompanyRepository;
import bo.com.luminia.sflbilling.msinvoice.repository.CuisRepository;
import bo.com.luminia.sflbilling.msinvoice.service.sfe.issue.InvoiceIssueService;
import bo.com.luminia.sflbilling.msinvoice.web.rest.errors.spec.DatetimeNotSync;
import bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe.invoice.InvoiceIssueReq;
import bo.com.luminia.sflbilling.security.AuthoritiesConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@SpringBootTest
public class TestEmisionFacturasIndividualIT {
    @Autowired
    private InvoiceIssueService invoiceIssueService ;

    @Autowired
    private CompanyRepository companyRepository ;
    @Autowired
    private CuisRepository cuisRepository ;

    private HashMap<String,Object> crearFacturaCmpCompraVenta() {

        HashMap<String, Object> map = new HashMap<>();

        return map ;
    }


    @Test
    public void testPaso1 (){
        Optional<Company> companyFromDb = companyRepository.findById(1L);
        if (companyFromDb.isPresent()){
            Company company = companyFromDb.get();
            InvoiceIssueReq request = new InvoiceIssueReq();
            request.setPointSaleSiat(0);
            request.setBranchOfficeSiat(0);
            request.setCompanyId(company.getId());

            Optional<Cuis>cuisFromDb = cuisRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(request.getPointSaleSiat(), request.getBranchOfficeSiat(), request.getCompanyId());
            Cuis cuis = cuisFromDb.get();
            try {

                for(int i = 0 ; i< 50 ; i++) {
                    ZonedDateTime zonedDateTime = invoiceIssueService.obtainDateTimeFromSiat(company, cuis, request);
                    assertThat(zonedDateTime).isNotNull() ;
                }
            } catch (DatetimeNotSync datetimeNotSync) {
                datetimeNotSync.printStackTrace();
            }
        }
    }

    @Test
    public void testPaso4 (){
        Optional<Company> companyFromDb = companyRepository.findById(1L);
        if (companyFromDb.isPresent()){
            Company company = companyFromDb.get();
            InvoiceIssueReq request = new InvoiceIssueReq();
            request.setPointSaleSiat(1);
            request.setBranchOfficeSiat(0);
            request.setCompanyId(company.getId());

            Optional<Cuis>cuisFromDb = cuisRepository.findByPointSaleSiatIdBranchOfficeSiatIdActive(request.getPointSaleSiat(), request.getBranchOfficeSiat(), request.getCompanyId());
            Cuis cuis = cuisFromDb.get();
            try {

                for(int i = 0 ; i< 50 ; i++) {
                    ZonedDateTime zonedDateTime = invoiceIssueService.obtainDateTimeFromSiat(company, cuis, request);
                    assertThat(zonedDateTime).isNotNull() ;
                }
            } catch (DatetimeNotSync datetimeNotSync) {
                datetimeNotSync.printStackTrace();
            }
        }
    }
}
