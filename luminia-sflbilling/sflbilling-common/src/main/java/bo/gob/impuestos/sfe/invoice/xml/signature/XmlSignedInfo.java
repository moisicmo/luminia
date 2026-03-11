package bo.gob.impuestos.sfe.invoice.xml.signature;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;

@Data
public class XmlSignedInfo {
    private XmlSignatureMethod canonicalizationMethod ;
    private XmlSignatureMethod signatureMethod;
    private XmlSignatureReference reference ;

    @XmlElement( name = "CanonicalizationMethod")
    public XmlSignatureMethod getCanonicalizationMethod() {
        return canonicalizationMethod;
    }

    @XmlElement(name = "SignatureMethod")
    public XmlSignatureMethod getSignatureMethod() {
        return signatureMethod;
    }

    @XmlElement(name = "Reference")
    public XmlSignatureReference getReference() {
        return reference;
    }

    public void setCanonicalizationMethod(XmlSignatureMethod canonicalizationMethod) {
        this.canonicalizationMethod = canonicalizationMethod;
    }

    public void setSignatureMethod(XmlSignatureMethod signatureMethod) {
        this.signatureMethod = signatureMethod;
    }

    public void setReference(XmlSignatureReference reference) {
        this.reference = reference;
    }
}
