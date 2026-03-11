package bo.com.luminia.sflbilling.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sync_setting")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SyncSetting extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "syncSettingSeq")
    @SequenceGenerator(name = "syncSettingSeq")
    private Long id;

    @NotNull
    @Column(name = "sync_type", nullable = false)
    private Integer syncType;

    @NotNull
    @Size(max = 255)
    @Column(name = "cron_date", length = 255, nullable = false)
    private String cronDate;

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
}
