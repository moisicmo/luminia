package bo.com.luminia.sflbilling.msaccount.service.dto;

import bo.com.luminia.sflbilling.domain.Authority;
import bo.com.luminia.sflbilling.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCompanyDTO {

    private Long id;

    private String login;

    private String firstName;

    private String lastName;

    private String email;

    private Boolean activated;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Long companyId;

    private String companyBusinessCode;

    private Set<String> authorities;

    public UserCompanyDTO(User entity) {
        this.id = entity.getId();
        this.login = entity.getLogin();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
        this.email = entity.getEmail();
        this.activated = entity.isActivated();
        this.createdBy = entity.getCreatedBy();
        this.createdDate = entity.getCreatedDate();
        this.lastModifiedBy = entity.getLastModifiedBy();
        this.lastModifiedDate = entity.getLastModifiedDate();
        if (entity.getCompany() != null) {
            this.companyId = entity.getCompany().getId();
            this.companyBusinessCode = entity.getCompany().getBusinessCode();
        }
        if (entity.getAuthorities() != null && !entity.getAuthorities().isEmpty())
            this.authorities = entity.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
    }
}
