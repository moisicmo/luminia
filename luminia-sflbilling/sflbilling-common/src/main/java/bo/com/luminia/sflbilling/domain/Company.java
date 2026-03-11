package bo.com.luminia.sflbilling.domain;

import bo.com.luminia.sflbilling.domain.enumeration.EnvironmentSiatEnum;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"logo"})
@Entity
@Table(name = "company")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Company extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "companySeq")
    @SequenceGenerator(name = "companySeq")
    private Long id;

    @NotNull
    @Column(name = "business_code", nullable = false, unique = true)
    private String businessCode;

    @NotNull
    @Column(name = "nit", nullable = false)
    private Long nit;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "business_name", length = 255)
    private String businessName;

    @Size(max = 255)
    @Column(name = "city", length = 255)
    private String city;

    @Size(max = 255)
    @Column(name = "phone", length = 255)
    private String phone;

    @Size(max = 255)
    @Column(name = "address", length = 255)
    private String address;

    @Size(max = 50)
    @Column(name = "system_code", length = 50)
    private String systemCode;

    @NotNull
    @Column(name = "package_send", nullable = false)
    private Boolean packageSend;

    @NotNull
    @Column(name = "event_send", nullable = false)
    private Boolean eventSend;

    @Size(max = 255)
    @Column(name = "email_notification", length = 255)
    private String emailNotification;

    @Size(max = 500)
    @Column(name = "token", length = 500)
    private String token;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "environment_siat", nullable = false)
    private EnvironmentSiatEnum environmentSiat;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "modality_siat", nullable = false)
    private ModalitySiatEnum modalitySiat;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "logo", nullable = true)
    private byte[] logo;
}
