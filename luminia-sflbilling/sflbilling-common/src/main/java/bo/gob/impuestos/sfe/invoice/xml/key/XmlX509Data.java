package bo.gob.impuestos.sfe.invoice.xml.key;

import javax.xml.bind.annotation.XmlElement;

public class XmlX509Data {

    private String x509Certificate ;
    private String x509SubjectName ;
    private XmlX509IssuerSerial xmlX509IssuerSerial ;

    @XmlElement(name = "X509Certificate")
    public String getX509Certificate() {
        return x509Certificate;
    }

    @XmlElement(name = "X509SubjectName")
    public String getX509SubjectName() {
        return x509SubjectName;
    }

    @XmlElement(name = "X509IssuerSerial")
    public XmlX509IssuerSerial getXmlX509IssuerSerial() {
        return xmlX509IssuerSerial;
    }
}
