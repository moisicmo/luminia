package bo.gob.impuestos.sfe.invoice.xml.cmp;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlTasaCeroDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlTasaCeroHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "facturaComputarizadaTasaCero")
public class XmlTasaCeroCmpInvoice extends XmlBaseInvoice<XmlTasaCeroHeader, XmlTasaCeroDetail> {

    public XmlTasaCeroCmpInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_CMP_TASA_CERO);
    }

    @XmlElement(name = "detalle")
    public List<XmlTasaCeroDetail> getDetalle() {
        return this.detalle;
    }

    @XmlElement(name = "cabecera")
    public XmlTasaCeroHeader getCabecera() {
        return this.cabecera;
    }

    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlTasaCeroHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlTasaCeroDetail detail = new XmlTasaCeroDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
