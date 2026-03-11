package bo.com.luminia.sflbilling.msreport.web.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.JsonAdapter;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceResponse {
    private Long id;
    private Integer invoiceNumber;
    private String cuf;
    private String broadcastDate;
    private String invoiceXml;
    private String invoiceHash;
    private String receptionCode;
    private String status;
    private String modalitySiat;
    private String cufd;
    private Integer sectorDocumentTypeId;
    private String sectorDocumentTypeDescription;
    private Integer broadcastType;
    private String broadcastTypeDescription;
    private Integer invoiceType;
    private String invoiceTypeDescription;
    private Map<String, Object> invoice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Integer invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCuf() {
        return cuf;
    }

    public void setCuf(String cuf) {
        this.cuf = cuf;
    }

    public String getBroadcastDate() {
        return broadcastDate;
    }

    public void setBroadcastDate(String broadcastDate) {
        this.broadcastDate = broadcastDate;
    }

    public String getInvoiceXml() {
        return invoiceXml;
    }

    public void setInvoiceXml(String invoiceXml) {
        this.invoiceXml = invoiceXml;
    }

    public String getInvoiceHash() {
        return invoiceHash;
    }

    public void setInvoiceHash(String invoiceHash) {
        this.invoiceHash = invoiceHash;
    }

    public String getReceptionCode() {
        return receptionCode;
    }

    public void setReceptionCode(String receptionCode) {
        this.receptionCode = receptionCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getModalitySiat() {
        return modalitySiat;
    }

    public void setModalitySiat(String modalitySiat) {
        this.modalitySiat = modalitySiat;
    }

    public String getCufd() {
        return cufd;
    }

    public void setCufd(String cufd) {
        this.cufd = cufd;
    }

    public Integer getSectorDocumentTypeId() {
        return sectorDocumentTypeId;
    }

    public void setSectorDocumentTypeId(Integer sectorDocumentTypeId) {
        this.sectorDocumentTypeId = sectorDocumentTypeId;
    }

    public String getSectorDocumentTypeDescription() {
        return sectorDocumentTypeDescription;
    }

    public void setSectorDocumentTypeDescription(String sectorDocumentTypeDescription) {
        this.sectorDocumentTypeDescription = sectorDocumentTypeDescription;
    }

    public Integer getBroadcastType() {
        return broadcastType;
    }

    public void setBroadcastType(Integer broadcastType) {
        this.broadcastType = broadcastType;
    }

    public String getBroadcastTypeDescription() {
        return broadcastTypeDescription;
    }

    public void setBroadcastTypeDescription(String broadcastTypeDescription) {
        this.broadcastTypeDescription = broadcastTypeDescription;
    }

    public Integer getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(Integer invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceTypeDescription() {
        return invoiceTypeDescription;
    }

    public void setInvoiceTypeDescription(String invoiceTypeDescription) {
        this.invoiceTypeDescription = invoiceTypeDescription;
    }

    public Map<String, Object> getInvoice() {
        return invoice;
    }

    public void setInvoice(Map<String, Object> invoice) {
        this.invoice = invoice;
    }
}
