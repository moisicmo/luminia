package bo.com.luminia.sflbilling.domain;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@ToString(exclude = {"cuis"})
@Entity
@Table(name = "cufd")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Cufd implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cufdSeq")
    @SequenceGenerator(name = "cufdSeq")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "cufd", length = 100, nullable = false)
    private String cufd;

    @NotNull
    @Size(max = 20)
    @Column(name = "control_code", length = 20, nullable = false)
    private String controlCode;

    @NotNull
    @Size(max = 255)
    @Column(name = "address", length = 255, nullable = false)
    private String address;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private ZonedDateTime startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private ZonedDateTime endDate;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(optional = false)
    @NotNull
    private Cuis cuis;
}
