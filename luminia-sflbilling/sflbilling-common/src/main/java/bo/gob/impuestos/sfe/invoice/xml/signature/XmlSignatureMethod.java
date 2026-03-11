package bo.gob.impuestos.sfe.invoice.xml.signature;

import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;

@Data
public class XmlSignatureMethod {

    @XmlAttribute (name = "Algorithm")
    private String algorithm ;

    @XmlAttribute (name = "URI")
    private String uri ;
}
