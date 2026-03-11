package bo.com.luminia.sflbilling.msaccount.web.rest.response.sfe;

import lombok.Data;

@Data
public class SignatureRes {
    private Long id;
    private String certificate;
    private String privateKey;
    private String startDate;
    private String endDate;
    private Boolean active;
    private Long companyId;
}
