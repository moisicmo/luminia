package bo.com.luminia.sflbilling.msaccount.web.rest.request.admin;

import bo.com.luminia.sflbilling.domain.enumeration.EnvironmentSiatEnum;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class CompanyReq {

    private Long id;

    @NotNull
    private Long nit;

    @NotNull
    @Size(max = 100)
    private String name;

    private String businessCode;

    @NotNull
    @Size(max = 100)
    private String businessName;

    @NotNull
    @Size(max = 50)
    private String city;

    @NotNull
    @Size(max = 50)
    private String phone;

    @NotNull
    @Size(max = 255)
    private String address;

    @NotNull
    @Size(max = 50)
    private String systemCode;

    @NotNull
    private EnvironmentSiatEnum environmentSiat;

    @NotNull
    private ModalitySiatEnum modalitySiat;

    private Boolean packageSend;

    private Boolean eventSend;

    private String emailNotification;

    private Boolean active;

    @NotNull
    @Size(max = 500)
    private String token;

    private byte[] logo;
}
