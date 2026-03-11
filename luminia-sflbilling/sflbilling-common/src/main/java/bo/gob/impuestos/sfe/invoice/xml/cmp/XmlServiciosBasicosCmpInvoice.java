package bo.gob.impuestos.sfe.invoice.xml.cmp;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlServiciosBasicosDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlServiciosBasicosHeader;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@XmlRootElement(name = "facturaComputarizadaServicioBasico")
public class XmlServiciosBasicosCmpInvoice
    extends XmlBaseInvoice<XmlServiciosBasicosHeader, XmlServiciosBasicosDetail> {

    public XmlServiciosBasicosCmpInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_CMP_SERVICIO_BASICO);
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlServiciosBasicosHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    @XmlElement(name = "detalle")
    public List<XmlServiciosBasicosDetail> getDetalle() {
        return this.detalle;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlServiciosBasicosHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlServiciosBasicosDetail detail = new XmlServiciosBasicosDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
