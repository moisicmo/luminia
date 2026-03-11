package bo.gob.impuestos.sfe.invoice.xml.signature;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

public class XmlSignatureReference extends XmlSignatureMethod {

    private List<XmlSignatureMethod> transforms ;
    private XmlSignatureMethod digestMethod ;
    private XmlSignatureMethod digestValue ;

    @XmlElementWrapper(name = "Transforms")
    @XmlElement(name = "Transform")
    public List<XmlSignatureMethod> getTransforms() {
        return transforms;
    }

    @XmlElement(name = "DigestMethod")
    public XmlSignatureMethod getDigestMethod() {
        return digestMethod;
    }

    @XmlElement(name = "DigestValue")
    public XmlSignatureMethod getDigestValue() {
        return digestValue;
    }

    public void setDigestValue(XmlSignatureMethod digestValue) {
        this.digestValue = digestValue;
    }

    public void setDigestMethod(XmlSignatureMethod digestMethod) {
        this.digestMethod = digestMethod;
    }

    public void setTransforms(List<XmlSignatureMethod> transforms) {
        this.transforms = transforms;
    }
}
