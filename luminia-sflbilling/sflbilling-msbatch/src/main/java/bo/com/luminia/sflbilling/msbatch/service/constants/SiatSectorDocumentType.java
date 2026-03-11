package bo.com.luminia.sflbilling.msbatch.service.constants;

import bo.gob.impuestos.sfe.invoice.xml.XmlSectorType;

import java.util.ArrayList;
import java.util.List;

public class SiatSectorDocumentType {

    public static final List<Integer> map;

    static {
        map = new ArrayList<>();
        map.add(XmlSectorType.INVOICE_COMPRA_VENTA);
        map.add(XmlSectorType.INVOICE_ALQUILER_BIENES_INMUEBLES);
        map.add(XmlSectorType.INVOICE_COMERCIAL_EXPORTACION);
        map.add(XmlSectorType.INVOICE_TURISTICO_HOSPEDAJE);
        map.add(XmlSectorType.INVOICE_ALIMENTOS_SEGURIDAD);
        map.add(XmlSectorType.INVOICE_SECTOR_EDUCATIVO);
        map.add(XmlSectorType.INVOICE_COMERCIALIZACION_HIDROCARBUROS);
        map.add(XmlSectorType.INVOICE_FACTURAS_HOTELES);
        map.add(XmlSectorType.INVOICE_HOSPITAL_CLINICAS);
        map.add(XmlSectorType.INVOICE_EXPORTACION_SERVICIOS);
        map.add(XmlSectorType.INVOICE_SERVICIOS_BASICOS);
        map.add(XmlSectorType.INVOICE_ENTIDADES_FINANCIERAS);
        map.add(XmlSectorType.INVOICE_TELECOMUNICACIONES);
        map.add(XmlSectorType.INVOICE_PREVALORADA);
    }
}
