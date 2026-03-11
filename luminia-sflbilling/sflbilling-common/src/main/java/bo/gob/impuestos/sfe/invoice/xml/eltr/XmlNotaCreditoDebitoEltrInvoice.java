package bo.gob.impuestos.sfe.invoice.xml.eltr;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlNotaCreditoDebitoDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlNotaCreditoDebitoHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlType
@XmlRootElement(name = "notaFiscalElectronicaCreditoDebito")
public class XmlNotaCreditoDebitoEltrInvoice
    extends XmlBaseInvoice<XmlNotaCreditoDebitoHeader, XmlNotaCreditoDebitoDetail> {

    public XmlNotaCreditoDebitoEltrInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_ELE_NOTA_CREDITO_DEBITO);
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlNotaCreditoDebitoHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    @XmlElement(name = "detalle")
    public List<XmlNotaCreditoDebitoDetail> getDetalle() {
        return this.detalle;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlNotaCreditoDebitoHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlNotaCreditoDebitoDetail detail = new XmlNotaCreditoDebitoDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
