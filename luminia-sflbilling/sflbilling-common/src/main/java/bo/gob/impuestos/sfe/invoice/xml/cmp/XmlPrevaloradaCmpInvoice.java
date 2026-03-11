package bo.gob.impuestos.sfe.invoice.xml.cmp;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlPrevaloradaDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlPrevaloradaHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "facturaComputarizadaPrevalorada")
public class XmlPrevaloradaCmpInvoice
        extends XmlBaseInvoice<XmlPrevaloradaHeader, XmlPrevaloradaDetail> {

    public XmlPrevaloradaCmpInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_CMP_PREVALORADA);
    }

    @XmlElement(name = "detalle")
    public List<XmlPrevaloradaDetail> getDetalle() {
        return this.detalle;
    }

    @XmlElement(name = "cabecera")
    public XmlPrevaloradaHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlPrevaloradaHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlPrevaloradaDetail detail = new XmlPrevaloradaDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
