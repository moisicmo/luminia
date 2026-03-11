package bo.gob.impuestos.sfe.invoice.xml.cmp;

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

@XmlRootElement(name = "facturaComputarizadaZonaFranca")
public class XmlZonaFrancaCmpInvoice extends XmlBaseInvoice<XmlZonaFrancaHeader, XmlZonaFrancaDetail> {

    public XmlZonaFrancaCmpInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_CMP_ZONO_FRANCA);
    }

    @XmlElement(name = "detalle")
    public List<XmlZonaFrancaDetail> getDetalle() {
        return this.detalle;
    }

    @XmlElement(name = "cabecera")
    public XmlZonaFrancaHeader getCabecera() {
        return this.cabecera;
    }

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
