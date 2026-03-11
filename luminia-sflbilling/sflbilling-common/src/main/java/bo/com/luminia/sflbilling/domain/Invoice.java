package bo.com.luminia.sflbilling.domain;

import bo.com.luminia.sflbilling.domain.enumeration.InvoiceStatusEnum;
import bo.com.luminia.sflbilling.domain.enumeration.ModalitySiatEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "invoice")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Invoice extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoiceSeq")
    @SequenceGenerator(name = "invoiceSeq")
    private Long id;

    @NotNull
    @Column(name = "invoice_number", nullable = false)
    private Integer invoiceNumber;

    @NotNull
    @Size(max = 70)
    @Column(name = "cuf", length = 70, nullable = false)
    private String cuf;

    @NotNull
    @Column(name = "broadcast_date", nullable = false)
    private ZonedDateTime broadcastDate;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "invoice_xml", nullable = false)
    private String invoiceXml;

    @NotNull
    @Size(max = 70)
    @Column(name = "invoice_hash", length = 70, nullable = false)
    private String invoiceHash;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "invoice_json", nullable = false)
    private String invoiceJson;

    @NotNull
    @Size(max = 20)
    @Column(name = "nit", length = 20, nullable = false)
    private String nit;

    @NotNull
    @Size(max = 500)
    @Column(name = "business_name", length = 500, nullable = false)
    private String businessName;

    @Size(max = 50)
    @Column(name = "reception_code", length = 50)
    private String receptionCode;

    @Size(max = 255)
    @Column(name = "cafc", length = 255)
    private String cafc;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatusEnum status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "modality_siat", nullable = false)
    private ModalitySiatEnum modalitySiat;

    @ManyToOne(optional = false)
    @NotNull
    private Cufd cufd;

    @ManyToOne(optional = false)
    @NotNull
    private SectorDocumentType sectorDocumentType;

    @ManyToOne(optional = false)
    @NotNull
    private BroadcastType broadcastType;

    @ManyToOne(optional = false)
    @NotNull
    private InvoiceType invoiceType;
}
