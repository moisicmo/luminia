package bo.com.luminia.sflbilling.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


@Data
@Entity
@Table(name = "valid_identity_document_type")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ValidIdentityDocumentType extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "validIdentityDocumentTypeSeq")
    @SequenceGenerator(name = "validIdentityDocumentTypeSeq")
    private Long id;

    @NotNull
    @Column(name = "siat_code", nullable = false)
    private Byte siatCode;

    @NotNull
    @Size(max = 20)
    @Column(name = "document", length = 20, nullable = false)
    private String document;

    @Size(max = 5)
    @Column(name = "complement", length = 5, nullable = true)
    private String complement;

    @NotNull
    @Size(max = 100)
    @Column(name = "social_reason", length = 100, nullable = false)
    private String socialReason;

}
