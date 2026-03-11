package bo.gob.impuestos.sfe.invoice.xml.eltr;

import bo.gob.impuestos.sfe.invoice.xml.XmlBaseInvoice;
import bo.gob.impuestos.sfe.invoice.xml.XmlNoNamespace;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlHospitalClinicaDetail;
import bo.gob.impuestos.sfe.invoice.xml.header.XmlHospitalClinicaHeader;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlType
@XmlRootElement(name = "facturaElectronicaHospitalClinica")
public class XmlHospitalClinicaEltrInvoice
    extends XmlBaseInvoice<XmlHospitalClinicaHeader, XmlHospitalClinicaDetail> {

    public XmlHospitalClinicaEltrInvoice()throws FileNotFoundException, URISyntaxException {
        super(XmlNoNamespace.SCHEMA_LOCATION_ELE_HOSPITAL_CLINICA);
    }

    @Override
    @XmlElement(name = "cabecera")
    public XmlHospitalClinicaHeader getCabecera() {
        return this.cabecera;
    }

    @Override
    @XmlElement(name = "detalle")
    public List<XmlHospitalClinicaDetail> getDetalle() {
        return this.detalle;
    }

    @Override
    public void convert(Map<String, Object> fromHeader, List<Map<String, String>> fromDetail) {
        this.cabecera = new XmlHospitalClinicaHeader();
        this.cabecera.convert(fromHeader);

        this.detalle = new ArrayList<>();
        fromDetail.forEach(item -> {
            XmlHospitalClinicaDetail detail = new XmlHospitalClinicaDetail();
            detail.convert(item);
            this.detalle.add(detail);
        });
    }
}
