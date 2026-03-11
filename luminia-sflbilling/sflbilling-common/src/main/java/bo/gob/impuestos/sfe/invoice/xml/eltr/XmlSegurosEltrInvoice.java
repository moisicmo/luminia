package bo.gob.impuestos.sfe.invoice.xml.eltr;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlSegurosDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlSegurosHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "facturaElectronicaSeguros")
public class XmlSegurosEltrInvoice extends XmlBaseInvoice<XmlSegurosHeader, XmlSegurosDetail> {

    public XmlSegurosEltrInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_ELE_SEGUROS);
    }

    @XmlElement(name = "detalle")
    public List<XmlSegurosDetail> getDetalle() {
        return this.detalle;
    }

    @XmlElement(name = "cabecera")
    public XmlSegurosHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlSegurosHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlSegurosDetail detail = new XmlSegurosDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
