package bo.gob.impuestos.sfe.invoice.xml.cmp;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlSectorEducativoDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlSectorEducativoHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlRootElement(name ="facturaComputarizadaSectorEducativo" )
public class XmlSectorEducativoCmpInvoice
    extends XmlBaseInvoice<XmlSectorEducativoHeader, XmlSectorEducativoDetail> {

    public XmlSectorEducativoCmpInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_CMP_SECTOR_EDUCATIVO );
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlSectorEducativoHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    @XmlElement(name = "detalle")
    public List<XmlSectorEducativoDetail> getDetalle() {
        return this.detalle;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlSectorEducativoHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlSectorEducativoDetail detail = new XmlSectorEducativoDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
