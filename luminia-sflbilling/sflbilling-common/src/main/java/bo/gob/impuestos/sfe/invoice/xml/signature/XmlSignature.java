package bo.gob.impuestos.sfe.invoice.xml.signature;

import bo.gob.impuestos.sfe.invoice.xml.key.XmlKeyInfo;

import javax.xml.bind.annotation.XmlElement;

public class XmlSignature {

    private XmlSignedInfo signatureInfo;
    private String signatureValue ;
    private XmlKeyInfo keyInfo ;

    @XmlElement (name = "SignatureInfo")
    public XmlSignedInfo getSignatureInfo() {
        return signatureInfo;
    }

    @XmlElement(name = "SignatureValue")
    public String getSignatureValue() {
        return signatureValue;
    }

    @XmlElement(name="KeyInfo")
    public XmlKeyInfo getKeyInfo() {
        return keyInfo;
    }
}
