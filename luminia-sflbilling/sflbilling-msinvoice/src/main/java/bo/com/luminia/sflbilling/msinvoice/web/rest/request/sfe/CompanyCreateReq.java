package bo.com.luminia.sflbilling.msinvoice.web.rest.request.sfe;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CompanyCreateReq {

    @NotBlank
    private Long nit;

    @NotBlank
    private Integer connectionTypeId;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String businessName;

    @NotBlank
    @Size(max = 50)
    private String city;

    @NotBlank
    @Size(max = 50)
    private String phone;

    @NotBlank
    @Size(max = 255)
    private String address;

    @NotBlank
    @Size(max = 50)
    private String systemCode;

    @NotBlank
    private Integer environmentSiat;

    @NotBlank
    private Integer modalitySiat;

    private Boolean packageSend;

    private Boolean eventSend;

    @NotBlank
    @Size(max = 50)
    private String userSiat;

    @NotBlank
    @Size(max = 50)
    private String passwordSiat;

}
