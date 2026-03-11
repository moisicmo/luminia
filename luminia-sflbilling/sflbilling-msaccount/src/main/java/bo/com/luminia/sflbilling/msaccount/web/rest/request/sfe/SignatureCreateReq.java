package bo.com.luminia.sflbilling.msaccount.web.rest.request.sfe;

import lombok.Data;

@Data
public class SignatureCreateReq {
    private Long id;
    private String certificate;
    private String privateKey;
    private String startDate;
    private String endDate;
    private Long companyId;
}
