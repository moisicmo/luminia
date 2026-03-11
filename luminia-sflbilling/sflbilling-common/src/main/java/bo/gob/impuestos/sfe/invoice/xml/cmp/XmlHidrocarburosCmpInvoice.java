package bo.gob.impuestos.sfe.invoice.xml.cmp;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlHidrocarburosDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlHidrocarburosHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlType
@XmlRootElement(name = "facturaComputarizadaComercializacionHidrocarburo")
public class XmlHidrocarburosCmpInvoice
    extends XmlBaseInvoice<XmlHidrocarburosHeader, XmlHidrocarburosDetail> {

    public XmlHidrocarburosCmpInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_CMP_HIDROCARBUROS);
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlHidrocarburosHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    @XmlElement(name = "detalle")
    public List<XmlHidrocarburosDetail> getDetalle() {
        return this.detalle;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlHidrocarburosHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlHidrocarburosDetail detail = new XmlHidrocarburosDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
