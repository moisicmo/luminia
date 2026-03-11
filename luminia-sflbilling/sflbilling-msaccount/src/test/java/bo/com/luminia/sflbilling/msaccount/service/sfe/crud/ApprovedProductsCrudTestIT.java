package bo.com.luminia.sflbilling.msaccount.service.sfe.crud;

import bo.com.luminia.sflbilling.msaccount.service.sfe.ApprovedProductsService;
import bo.com.luminia.sflbilling.msaccount.utils.ResponseCodes;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.ApprovedProductCreateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe.ApprovedProductUpdateReq;
import bo.com.luminia.sflbilling.msaccount.web.rest.response.CrudRes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
public class ApprovedProductsCrudTestIT {

    @Autowired
    private ApprovedProductsService service ;

    private ApprovedProductCreateReq create() {
        ApprovedProductCreateReq result = new ApprovedProductCreateReq();
        result.setProductCode("P0003");
        result.setDescription("PRODUCTO 3");
        result.setCompanyId(1001L);
        result.setProductServiceId(1001L);
        result.setMeasurementUnitId(1058L);
        return result ;
    }

    private ApprovedProductUpdateReq update() {
        ApprovedProductUpdateReq result = new ApprovedProductUpdateReq();
        result.setId(1001L);
        //result.setProductCode("P0003");
        result.setDescription("PRODUCTO 3 Up");
        result.setCompanyId(1001L);
        //result.setProductServiceId(1001L);
        //result.setMeasurementUnitId(1058L);
        return result ;
    }

    @Test
    public void testPost(){
        CrudRes response = service.create(create());
        assertThat(response.getCode()).isEqualTo(ResponseCodes.SUCCESS);
    }

    @Test
    public void testGet() {
        CrudRes response = service.get(1L,0);
        assertThat(response.getCode()).isEqualTo(ResponseCodes.SUCCESS);
    }

    @Test
    public void testUpdate(){
        CrudRes response = service.update(update());
        assertThat(response.getCode()).isEqualTo(ResponseCodes.SUCCESS);
    }

}
