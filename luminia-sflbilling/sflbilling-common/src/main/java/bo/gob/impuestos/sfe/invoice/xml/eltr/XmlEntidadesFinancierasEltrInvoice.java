package bo.gob.impuestos.sfe.invoice.xml.eltr;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlEntidadesFinancierasDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlEntidadesFinancierasHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "facturaElectronicaEntidadFinanciera")
@XmlType
public class XmlEntidadesFinancierasEltrInvoice
    extends XmlBaseInvoice<XmlEntidadesFinancierasHeader, XmlEntidadesFinancierasDetail> {

    public XmlEntidadesFinancierasEltrInvoice() throws FileNotFoundException , URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_ELE_ENTIDAD_FINANCIERA);
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlEntidadesFinancierasHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    @XmlElement(name = "detalle")
    public List<XmlEntidadesFinancierasDetail> getDetalle() {
        return this.detalle;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlEntidadesFinancierasHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlEntidadesFinancierasDetail detail = new XmlEntidadesFinancierasDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
