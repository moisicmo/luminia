package bo.gob.impuestos.sfe.invoice.xml.key;

import javax.xml.bind.annotation.XmlElement;

public class XmlKeyInfo {

    private XmlX509Data x509Data ;

    @XmlElement(name = "X509Data")
    public XmlX509Data getX509Data() {
        return x509Data;
    }

    public void setX509Data(XmlX509Data x509Data) {
        this.x509Data = x509Data;
    }
}
