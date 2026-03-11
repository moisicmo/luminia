package bo.gob.impuestos.sfe.invoice.xml.eltr;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlZonaFrancaDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlZonaFrancaHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "facturaElectronicaZonaFranca")
public class XmlZonaFrancaEltrInvoice extends XmlBaseInvoice<XmlZonaFrancaHeader, XmlZonaFrancaDetail> {

    public XmlZonaFrancaEltrInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_ELE_ZONA_FRANCA);
    }

    @XmlElement(name = "detalle")
    public List<XmlZonaFrancaDetail> getDetalle() {
        return this.detalle;
    }

    @XmlElement(name = "cabecera")
    public XmlZonaFrancaHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlZonaFrancaHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlZonaFrancaDetail detail = new XmlZonaFrancaDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
