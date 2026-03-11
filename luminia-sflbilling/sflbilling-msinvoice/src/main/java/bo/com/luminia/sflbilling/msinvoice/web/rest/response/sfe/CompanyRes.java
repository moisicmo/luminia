package bo.com.luminia.sflbilling.msinvoice.web.rest.response.sfe;

import bo.com.luminia.sflbilling.domain.Company;
import lombok.Data;

@Data
public class CompanyRes {

    private Long id;
    private Long nit;
    private String name;
    private String businessName;
    private String city;
    private String phone;
    private String address;
    private String systemCode;
    private String environmentSiat;
    private String modalitySiat;
    private Boolean packageSend;
    private Boolean eventSend;
    private String userSiat;
    private String passwordSiat;

    public CompanyRes(Company entity) {
        this.id = entity.getId();
        this.nit = entity.getNit();
        this.name = entity.getName();
        this.businessName = entity.getBusinessName();
        this.city = entity.getCity();
        this.phone = entity.getPhone();
        this.address = entity.getAddress();
        this.systemCode = entity.getSystemCode();
        this.environmentSiat = entity.getEnvironmentSiat().toString();
        this.modalitySiat = entity.getModalitySiat().toString();
        this.packageSend = entity.getPackageSend();
        this.eventSend = entity.getEventSend();
        //this.userSiat = entity.getUserSiat();
        //this.passwordSiat = entity.getPasswordSiat() ;
    }

}
