package bo.gob.impuestos.sfe.invoice.xml.eltr;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlTelecomunicacionesDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlTelecomunicacionesHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "facturaElectronicaTelecomunicacion")
@XmlType
public class XmlTelecomunicacionesEltrInvoice
    extends XmlBaseInvoice<XmlTelecomunicacionesHeader, XmlTelecomunicacionesDetail>
{
    public XmlTelecomunicacionesEltrInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_ELE_TELECOMUNICACIONES);
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlTelecomunicacionesHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    @XmlElement(name = "detalle")
    public List<XmlTelecomunicacionesDetail> getDetalle() {
        return this.detalle;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlTelecomunicacionesHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlTelecomunicacionesDetail detail = new XmlTelecomunicacionesDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
