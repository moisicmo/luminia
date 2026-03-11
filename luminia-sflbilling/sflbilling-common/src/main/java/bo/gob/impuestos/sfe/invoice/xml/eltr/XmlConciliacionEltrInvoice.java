package bo.gob.impuestos.sfe.invoice.xml.eltr;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlConciliacionDetail;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlConciliacionOriginalDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlConciliacionHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlType
@XmlRootElement(name = "notaElectronicaConciliacion")
public class XmlConciliacionEltrInvoice extends XmlBaseInvoice<XmlConciliacionHeader, XmlConciliacionOriginalDetail> {

    private List<XmlConciliacionDetail> detalleConciliacion;

    public XmlConciliacionEltrInvoice() throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_ELE_NOTA_CONCILIACION);
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlConciliacionHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    @XmlElement(name = "detalleOriginal")
    public List<XmlConciliacionOriginalDetail> getDetalle() {
        return this.detalle;
    }

    @XmlElement(name = "detalleConciliacion")
    public List<XmlConciliacionDetail> getDetalleConciliacion() {
        return this.detalleConciliacion;
    }

    public void setDetalleConciliacion(List<XmlConciliacionDetail> detalleConciliacion) {
        this.detalleConciliacion = detalleConciliacion;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlConciliacionHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlConciliacionOriginalDetail detail = new XmlConciliacionOriginalDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }

    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetailOriginal, List<Map<String, String>> fromDetailConciliacion) {
        this.convert(fromHeader, fromDetailOriginal);

        this.detalleConciliacion = new ArrayList<>();
        fromDetailConciliacion.forEach(item -> {
            XmlConciliacionDetail detail = new XmlConciliacionDetail();
            detail.convert(item);
            this.detalleConciliacion.add(detail);
        });
    }
}
