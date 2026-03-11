package bo.com.luminia.sflbilling.msreport.web.rest.response;

import lombok.Data;

@Data
public class BatchReceptionRes {
    private String receptionCode;
    private String date;
    private String status;
}
