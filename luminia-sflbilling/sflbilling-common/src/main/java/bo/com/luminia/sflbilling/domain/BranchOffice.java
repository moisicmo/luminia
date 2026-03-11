package bo.com.luminia.sflbilling.domain;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@ToString(exclude = {"company"})
@Entity
@Table(name = "branch_office")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BranchOffice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "branchOfficeSeq")
    @SequenceGenerator(name = "branchOfficeSeq")
    private Long id;

    @NotNull
    @Column(name = "branch_office_siat_id", nullable = false)
    private Integer branchOfficeSiatId;

    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Size(max = 255)
    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(optional = false)
    @NotNull
    private Company company;

    @Size(max = 255)
    @Column(name = "city", length = 255, nullable = true)
    private String city;

    @Size(max = 15)
    @Column(name = "phone", length = 15, nullable = true)
    private String phone;

}
