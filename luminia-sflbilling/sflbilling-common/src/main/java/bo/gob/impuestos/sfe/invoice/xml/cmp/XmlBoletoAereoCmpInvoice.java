package bo.gob.impuestos.sfe.invoice.xml.cmp;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlBoletoAereoHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@XmlType
@XmlRootElement(name = "facturaComputarizadaBoletoAereo")
public class XmlBoletoAereoCmpInvoice extends XmlBaseInvoice<XmlBoletoAereoHeader, Object> {

    public XmlBoletoAereoCmpInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_CMP_BOLETO_AEREO);
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlBoletoAereoHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    public List<Object> getDetalle() {
        return null;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlBoletoAereoHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = null;
    }
}
