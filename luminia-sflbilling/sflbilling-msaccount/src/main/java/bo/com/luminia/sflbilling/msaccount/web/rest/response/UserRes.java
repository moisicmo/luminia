package bo.com.luminia.sflbilling.msaccount.web.rest.response;

import bo.com.luminia.sflbilling.domain.Authority;
import bo.com.luminia.sflbilling.domain.User;
import lombok.Data;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserRes {

    private Long id;

    private String login;

    private String firstName;

    private String lastName;

    private String email;

    private Boolean activated;

    private Long companyId;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Set<String> authorities;

    public UserRes(User entity) {
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
        this.companyId = entity.getCompany() != null ? entity.getCompany().getId() : null;
        this.authorities = entity.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
    }

    public UserRes() {
    }
}
