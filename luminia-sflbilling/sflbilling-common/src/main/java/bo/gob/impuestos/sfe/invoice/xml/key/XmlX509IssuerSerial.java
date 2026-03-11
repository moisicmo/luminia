package bo.gob.impuestos.sfe.invoice.xml.key;

import javax.xml.bind.annotation.XmlElement;

public class XmlX509IssuerSerial {
    private String x509IssuerName ;
    private String x509SerialNumber ;

    @XmlElement(name = "X509IssuerName")
    public String getX509IssuerName() {
        return x509IssuerName;
    }

    @XmlElement(name = "X509SerialNumber")
    public String getX509SerialNumber() {
        return x509SerialNumber;
    }

    public void setX509IssuerName(String x509IssuerName) {
        this.x509IssuerName = x509IssuerName;
    }

    public void setX509SerialNumber(String x509SerialNumber) {
        this.x509SerialNumber = x509SerialNumber;
    }
}
