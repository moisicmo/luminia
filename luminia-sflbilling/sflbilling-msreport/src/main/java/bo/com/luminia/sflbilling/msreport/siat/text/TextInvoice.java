package bo.com.luminia.sflbilling.msreport.siat.text;


import bo.com.luminia.sflbilling.domain.Activity;
import bo.com.luminia.sflbilling.domain.ApprovedProduct;
import bo.com.luminia.sflbilling.domain.MeasurementUnit;
import bo.com.luminia.sflbilling.msreport.repository.ActivityRepository;
import bo.com.luminia.sflbilling.msreport.repository.ApprovedProductRepository;
import bo.com.luminia.sflbilling.msreport.repository.CurrencyTypeRepository;
import bo.com.luminia.sflbilling.msreport.repository.MeasurementUnitRepository;
import bo.com.luminia.sflbilling.msreport.siat.pdf.literal.Spanish;
import lombok.Setter;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

public abstract class TextInvoice<I> {
    protected static final String BOLIVIANOS = "Bolivianos";
    public static final Integer LENGTH_POS = 32;
    public static final Integer LENGTH_A4 = 135;
    public static final String PAPER_A4 = "a4";
    public static final String PAPER_ROLL = "roll";
    protected I invoice;
    protected String paperType;
    protected ApprovedProductRepository approvedProductRepository;
    protected ActivityRepository activityRepository;
    protected CurrencyTypeRepository currencyTypeRepository;
    protected Long companyId;
    protected String productCode;
    protected String header, details, footer;
    protected String pointOfSale;
    protected HashMap<String, String> parameters;
    protected List<HashMap<String, String>> data;
    protected String siatUrl;
    @Setter
    protected String modalidadLeyenda;
    @Setter
    protected MeasurementUnitRepository measurementUnitRepository;
    protected final static int MAX_DIRECCION_LENGTH = 58 ;

    protected static final String MENSAJE = "Esta factura contribuye al desarrollo del país, el uso ilícito será sancionado penalmente de acuerdo a Ley" ;


    public TextInvoice(I invoice, String paperType) {
        this.invoice = invoice;
        this.paperType = paperType;
        parameters = new HashMap<>();
        data = new ArrayList<>();
    }

    protected void checkForOptional(List<HashMap<String, String>> optional) {
        if (optional != null && !optional.isEmpty()) {
            int i = 1;
            for (Object item : optional) {
                HashMap<String, String> itemFromList = (HashMap<String, String>) item;
                parameters.put("<HDR_KEY_" + i + ">", itemFromList.get("key"));
                parameters.put("<HDR_VALUE_" + i + ">", itemFromList.get("value"));
                i++;
            }
        }
    }

    public String[] generate() {
        if (paperType == TextInvoice.PAPER_A4) {
            return concatWithStream(concatWithStream(generateHeaderA4(), generateDetailsA4()), generateFooterA4());
        } else {
            return concatWithStream(concatWithStream(generateHeaderPOS(), generateDetailsPOS()), generateFooterPOS());
        }
    }

    protected abstract String[] generateHeaderA4();

    protected String generateHeaderA4Default() {
        StringBuilder header = new StringBuilder(this.header);

        header = replace(TextInvoiceParameters.HDR_RAZON_SOCIAL_EMISOR, parameters.get(TextInvoiceParameters.HDR_RAZON_SOCIAL_EMISOR), header);
        header = replace(TextInvoiceParameters.HDR_DIRECCION, parameters.get(TextInvoiceParameters.HDR_DIRECCION), header);
        header = replace(TextInvoiceParameters.HDR_TELEFONO, parameters.get(TextInvoiceParameters.HDR_TELEFONO), header);
        header = replace(TextInvoiceParameters.HDR_MUNICIPIO, parameters.get(TextInvoiceParameters.HDR_MUNICIPIO), header);

        header = replace(TextInvoiceParameters.HDR_NIT, parameters.get(TextInvoiceParameters.HDR_NIT), header);
        header = replace(TextInvoiceParameters.HDR_FACTURA, parameters.get(TextInvoiceParameters.HDR_FACTURA), header);
        header = replace(TextInvoiceParameters.HDR_CUF, parameters.get(TextInvoiceParameters.HDR_CUF), header);

        if (parameters.get(TextInvoiceParameters.HDR_COD_CONTROL) !=null) {
            header = replace(TextInvoiceParameters.HDR_COD_CONTROL, parameters.get(TextInvoiceParameters.HDR_COD_CONTROL), header);
        }

        String actividad = getActividad();
        if (actividad.equals("-")) {
            header = replace(TextInvoiceParameters.HDR_ACTIVIDAD, "-", header);
            header = replace(TextInvoiceParameters.HDR_ACTIVIDAD, "-", header);
        } else {
            header = replace(TextInvoiceParameters.HDR_ACTIVIDAD, parameters.get(TextInvoiceParameters.HDR_ACTIVIDAD), header);
        }

        header = replace(TextInvoiceParameters.HDR_FECHA, parameters.get(TextInvoiceParameters.HDR_FECHA), header);
        header = replace(TextInvoiceParameters.HDR_NIT_CI_EX, parameters.get(TextInvoiceParameters.HDR_NIT_CI_EX), header);

        if (parameters.get(TextInvoiceParameters.HDR_CMPL) != null && parameters.get(TextInvoiceParameters.HDR_CMPL).trim().length()==0) {
            header = new StringBuilder(header.toString().replace("COMPLEMENTO:", ""));
        }
        header = replace(TextInvoiceParameters.HDR_CMPL, parameters.get(TextInvoiceParameters.HDR_CMPL), header);

        header = replace(TextInvoiceParameters.HDR_NOMBRE_RAZON_SOCIAL, parameters.get(TextInvoiceParameters.HDR_NOMBRE_RAZON_SOCIAL), header);

        if (parameters.get(TextInvoiceParameters.HDR_COD_CLIENTE)!= null){
                header = replace(TextInvoiceParameters.HDR_COD_CLIENTE, parameters.get(TextInvoiceParameters.HDR_COD_CLIENTE), header);
        }

        if (parameters.get(TextInvoiceParameters.HDR_INCOTERM) != null) {
            header = new StringBuilder(header.toString().replace(TextInvoiceParameters.HDR_INCOTERM, parameters.get(TextInvoiceParameters.HDR_INCOTERM)));
        }

        if (parameters.get(TextInvoiceParameters.HDR_NOMBRE_ALUMNO) != null) {
            header = new StringBuilder(header.toString().replace(TextInvoiceParameters.HDR_NOMBRE_ALUMNO, parameters.get(TextInvoiceParameters.HDR_NOMBRE_ALUMNO)));
        }

        if (parameters.get(TextInvoiceParameters.HDR_MONEDA_TRANSACCION) != null) {
            header = new StringBuilder(header.toString().replace(TextInvoiceParameters.HDR_MONEDA_TRANSACCION, parameters.get(TextInvoiceParameters.HDR_MONEDA_TRANSACCION)));
        }

        if (parameters.get(TextInvoiceParameters.HDR_DIRECCION_COMPRADOR) != null) {
            header = replace(TextInvoiceParameters.HDR_DIRECCION_COMPRADOR, parameters.get(TextInvoiceParameters.HDR_DIRECCION_COMPRADOR), header);
        }

        if (parameters.get(TextInvoiceParameters.HDR_LUGAR_DESTINO) != null) {
            header = new StringBuilder(header.toString().replace(TextInvoiceParameters.HDR_LUGAR_DESTINO, parameters.get(TextInvoiceParameters.HDR_LUGAR_DESTINO)));
        }

        if (parameters.get(TextInvoiceParameters.HDR_TIPO_CAMBIO) != null) {
            header = new StringBuilder(header.toString().replace(TextInvoiceParameters.HDR_TIPO_CAMBIO, parameters.get(TextInvoiceParameters.HDR_TIPO_CAMBIO)));
        }

        if (parameters.get(TextInvoiceParameters.HDR_MONEDA_TRANSACCION) != null) {
            header = new StringBuilder(header.toString().replace(TextInvoiceParameters.HDR_MONEDA_TRANSACCION, parameters.get(TextInvoiceParameters.HDR_MONEDA_TRANSACCION)));
        }

        if (parameters.get(TextInvoiceParameters.HDR_PLACA_B_SISA_VIN) != null) {
            header = new StringBuilder(header.toString().replace(TextInvoiceParameters.HDR_PLACA_B_SISA_VIN, parameters.get(TextInvoiceParameters.HDR_PLACA_B_SISA_VIN)));
        }

        if (parameters.get(TextInvoiceParameters.HDR_TIPO_ENVASE) != null) {
            header = new StringBuilder(header.toString().replace(TextInvoiceParameters.HDR_TIPO_ENVASE, parameters.get(TextInvoiceParameters.HDR_TIPO_ENVASE)));
        }

        if (parameters.get(TextInvoiceParameters.HDR_NRO_CLIENTE) != null) {
            header = replace(TextInvoiceParameters.HDR_NRO_CLIENTE, parameters.get(TextInvoiceParameters.HDR_NRO_CLIENTE), header);
        }

        if (parameters.get(TextInvoiceParameters.HDR_CLIENTE_DIRECCION) != null) {
            header = replace(TextInvoiceParameters.HDR_CLIENTE_DIRECCION, parameters.get(TextInvoiceParameters.HDR_CLIENTE_DIRECCION), header);
        }

        if (parameters.get(TextInvoiceParameters.HDR_CONSUMO_PERIODO) != null) {
            header = replace(TextInvoiceParameters.HDR_CONSUMO_PERIODO, parameters.get(TextInvoiceParameters.HDR_CONSUMO_PERIODO), header);
        }

        if (parameters.get(TextInvoiceParameters.HDR_BENEFICIARIO) != null) {
            header = replace(TextInvoiceParameters.HDR_BENEFICIARIO, parameters.get(TextInvoiceParameters.HDR_BENEFICIARIO), header);
        }

        if (parameters.get(TextInvoiceParameters.HDR_PERIODO) != null) {
            header = replace(TextInvoiceParameters.HDR_PERIODO, parameters.get(TextInvoiceParameters.HDR_PERIODO), header);
        }

        if (parameters.get(TextInvoiceParameters.HDR_NRO_MEDIDOR) != null) {
            header = replace(TextInvoiceParameters.HDR_NRO_MEDIDOR, parameters.get(TextInvoiceParameters.HDR_NRO_MEDIDOR), header);
        }

        if (parameters.get(TextInvoiceParameters.HDR_NRO_FACTURA) != null) {
            header = replace(TextInvoiceParameters.HDR_NRO_FACTURA, parameters.get(TextInvoiceParameters.HDR_NRO_FACTURA), header);
        }

        if (parameters.get(TextInvoiceParameters.HDR_FECHA_FACTURA) != null) {
            header = replace(TextInvoiceParameters.HDR_FECHA_FACTURA, parameters.get(TextInvoiceParameters.HDR_FECHA_FACTURA), header);
        }

        if (parameters.get(TextInvoiceParameters.HDR_CUF_FACTURA) != null) {
            header = replace(TextInvoiceParameters.HDR_CUF_FACTURA, parameters.get(TextInvoiceParameters.HDR_CUF_FACTURA), header);
        }

        if (parameters.get(TextInvoiceParameters.HDR_KEY_1) != null) {
            header = replaceNumberValues(TextInvoiceParameters.HDR_KEY_1, parameters.get(TextInvoiceParameters.HDR_KEY_1), header, parameters.get(TextInvoiceParameters.HDR_KEY_1).length());
        }

        if (parameters.get(TextInvoiceParameters.HDR_KEY_2) != null) {
            header = replaceNumberValues(TextInvoiceParameters.HDR_KEY_2, parameters.get(TextInvoiceParameters.HDR_KEY_2), header, parameters.get(TextInvoiceParameters.HDR_KEY_2).length());
        }

        if (parameters.get(TextInvoiceParameters.HDR_KEY_3) != null) {
            header = replaceNumberValues(TextInvoiceParameters.HDR_KEY_3, parameters.get(TextInvoiceParameters.HDR_KEY_3), header, parameters.get(TextInvoiceParameters.HDR_KEY_3).length());
        }

        if (parameters.get(TextInvoiceParameters.HDR_KEY_4) != null) {
            header = replaceNumberValues(TextInvoiceParameters.HDR_KEY_4, parameters.get(TextInvoiceParameters.HDR_KEY_4), header, parameters.get(TextInvoiceParameters.HDR_KEY_4).length());
        }

        if (parameters.get(TextInvoiceParameters.HDR_VALUE_1) != null) {
            header = replace(TextInvoiceParameters.HDR_VALUE_1, parameters.get(TextInvoiceParameters.HDR_VALUE_1), header);
        }
        if (parameters.get(TextInvoiceParameters.HDR_VALUE_2) != null) {
            header = replace(TextInvoiceParameters.HDR_VALUE_2, parameters.get(TextInvoiceParameters.HDR_VALUE_2), header);
        }
        if (parameters.get(TextInvoiceParameters.HDR_VALUE_3) != null) {
            header = replace(TextInvoiceParameters.HDR_VALUE_3, parameters.get(TextInvoiceParameters.HDR_VALUE_3), header);
        }
        if (parameters.get(TextInvoiceParameters.HDR_VALUE_4) != null) {
            header = replace(TextInvoiceParameters.HDR_VALUE_4, parameters.get(TextInvoiceParameters.HDR_VALUE_4), header);
        }
        if (parameters.get(TextInvoiceParameters.HDR_NOMBRE_RAZON_SOCIAL) != null) {
            header = replace(TextInvoiceParameters.HDR_NOMBRE_RAZON_SOCIAL,"", header);
        }

        return header.toString();
    }

    protected abstract String[] generateDetailsA4();

    protected String generateDetailsA4Default() {
        String response = "";
        for (HashMap<String, String> item : data) {
            StringBuilder row = new StringBuilder(this.details);

            row = replace(TextInvoiceParameters.CODIGO_PRODUCTO, item.get(TextInvoiceParameters.CODIGO_PRODUCTO), row);
            row = replace(TextInvoiceParameters.UNIDAD_MEDIDA, item.get(TextInvoiceParameters.UNIDAD_MEDIDA), row);
            row = replaceNumberValues(TextInvoiceParameters.CANTIDAD, item.get(TextInvoiceParameters.CANTIDAD), row, item.get(TextInvoiceParameters.CANTIDAD).length());
            row = replace(TextInvoiceParameters.DESCRIPCION, item.get(TextInvoiceParameters.DESCRIPCION), row);
            row = replace(TextInvoiceParameters.UNIDAD_MEDIDA, item.get(TextInvoiceParameters.UNIDAD_MEDIDA), row);
            row = replaceNumberValues(TextInvoiceParameters.PRECIO_UNITARIO, item.get(TextInvoiceParameters.PRECIO_UNITARIO), row, item.get(TextInvoiceParameters.PRECIO_UNITARIO).length());
            row = replaceNumberValues(TextInvoiceParameters.DESCUENTO, item.get(TextInvoiceParameters.DESCUENTO), row, item.get(TextInvoiceParameters.DESCUENTO).length());
            row = replaceNumberValues(TextInvoiceParameters.SUB_TOTAL, item.get(TextInvoiceParameters.SUB_TOTAL), row, item.get(TextInvoiceParameters.SUB_TOTAL).length());

            if (item.get(TextInvoiceParameters.ESPECIALIDAD) != null) {
                StringBuilder hospitales = new StringBuilder(row);
                hospitales = replace(TextInvoiceParameters.ESPECIALIDAD, item.get(TextInvoiceParameters.ESPECIALIDAD), hospitales);

                row = hospitales;
            }

            if (item.get(TextInvoiceParameters.QUIROFANO) != null) {
                StringBuilder hospitales = new StringBuilder(row);
                hospitales = replace(TextInvoiceParameters.QUIROFANO, item.get(TextInvoiceParameters.QUIROFANO), hospitales);

                row = hospitales;
            }

            if (item.get(TextInvoiceParameters.MEDICO) != null) {
                StringBuilder hospitales = new StringBuilder(row);
                hospitales = replace(TextInvoiceParameters.MEDICO, item.get(TextInvoiceParameters.MEDICO), hospitales);

                row = hospitales;
            }

            if (item.get(TextInvoiceParameters.ESPECIALIDAD_MED) != null) {
                StringBuilder hospitales = new StringBuilder(row);
                hospitales = replace(TextInvoiceParameters.ESPECIALIDAD_MED, item.get(TextInvoiceParameters.ESPECIALIDAD_MED), hospitales);

                row = hospitales;
            }

            if (item.get(TextInvoiceParameters.NIT) != null) {
                StringBuilder hospitales = new StringBuilder(row);
                hospitales = replace(TextInvoiceParameters.NIT, item.get(TextInvoiceParameters.NIT), hospitales);

                row = hospitales;
            }

            if (item.get(TextInvoiceParameters.NRO_FACTURA) != null) {
                StringBuilder hospitales = new StringBuilder(row);
                hospitales = replace(TextInvoiceParameters.NRO_FACTURA, item.get(TextInvoiceParameters.NRO_FACTURA), hospitales);

                row = hospitales;
            }

            response += row.toString() + "\n";
        }

        return response;
    }

    protected abstract String[] generateFooterA4();

    protected String generateFooterA4Default() {
        StringBuilder footer = new StringBuilder(this.footer);

        footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_TOTAL, parameters.get(TextInvoiceParameters.FTR_MONTO_TOTAL), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_TOTAL).length());
        footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_SUBTOTAL, parameters.get(TextInvoiceParameters.FTR_MONTO_SUBTOTAL), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_SUBTOTAL).length());
        footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_DESCUENTO, parameters.get(TextInvoiceParameters.FTR_MONTO_DESCUENTO), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_DESCUENTO).length());
        footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_GIFT, parameters.get(TextInvoiceParameters.FTR_MONTO_GIFT), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_GIFT).length());

        if (parameters.get(TextInvoiceParameters.FTR_MONTO_IVA) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_IVA, parameters.get(TextInvoiceParameters.FTR_MONTO_IVA), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_IVA).length());
        }

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_TOTAL_LITERAL, parameters.get(TextInvoiceParameters.FTR_TOTAL_LITERAL)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_TOTAL_DECIMAL, parameters.get(TextInvoiceParameters.FTR_TOTAL_DECIMAL)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MONEDA_LITERAL, parameters.get(TextInvoiceParameters.FTR_MONEDA_LITERAL)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_LEYENDA, parameters.get(TextInvoiceParameters.FTR_LEYENDA)));

        String qr = String.format(siatUrl,
            parameters.get(TextInvoiceParameters.HDR_NIT), parameters.get(TextInvoiceParameters.HDR_CUF), parameters.get(TextInvoiceParameters.HDR_FACTURA),
            parameters.get(TextInvoiceParameters.HDR_CODIGO_SECTOR));

        if (parameters.get(TextInvoiceParameters.FTR_MONTO_ARREND) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_ARREND, parameters.get(TextInvoiceParameters.FTR_MONTO_ARREND), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_ARREND).length());
        }

        if (parameters.get(TextInvoiceParameters.FTR_TOTA_CAMBIO) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_TOTA_CAMBIO, parameters.get(TextInvoiceParameters.FTR_TOTA_CAMBIO), footer, parameters.get(TextInvoiceParameters.FTR_TOTA_CAMBIO).length());
        }

        if (parameters.get(TextInvoiceParameters.FTR_MON_CAMBIO) != null) {
            footer = replace(TextInvoiceParameters.FTR_MON_CAMBIO, parameters.get(TextInvoiceParameters.FTR_MON_CAMBIO), footer, 0);
        }

        if (parameters.get(TextInvoiceParameters.FTR_MONE_TOTAL) != null) {
            footer = replace(TextInvoiceParameters.FTR_MONE_TOTAL, parameters.get(TextInvoiceParameters.FTR_MONE_TOTAL), footer, 0);
        }

        if (parameters.get(TextInvoiceParameters.FTR_CAMBIO_LITERAL) != null) {
            footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_CAMBIO_LITERAL, parameters.get(TextInvoiceParameters.FTR_CAMBIO_LITERAL)));
        }

        if (parameters.get(TextInvoiceParameters.FTR_CAMBIO_DECIMAL) != null) {
            footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_CAMBIO_DECIMAL, parameters.get(TextInvoiceParameters.FTR_CAMBIO_DECIMAL)));
        }

        if (parameters.get(TextInvoiceParameters.FTR_MON_CAMBIO_LITERAL) != null) {
            footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MON_CAMBIO_LITERAL, parameters.get(TextInvoiceParameters.FTR_MON_CAMBIO_LITERAL)));
        }

        if (parameters.get(TextInvoiceParameters.FTR_INFORMACION_ADICIONAL) != null) {
            footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_INFORMACION_ADICIONAL, parameters.get(TextInvoiceParameters.FTR_INFORMACION_ADICIONAL)));
        }

        if (parameters.get(TextInvoiceParameters.FTR_MONTO_LEY) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_LEY, parameters.get(TextInvoiceParameters.FTR_MONTO_LEY), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_LEY).length());
        }

        if (parameters.get(TextInvoiceParameters.AJUSTE_NO_IVA) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.AJUSTE_NO_IVA, parameters.get(TextInvoiceParameters.AJUSTE_NO_IVA), footer, parameters.get(TextInvoiceParameters.AJUSTE_NO_IVA).length());
        }

        if (parameters.get(TextInvoiceParameters.DESCUENTO_LEY_1886) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.DESCUENTO_LEY_1886, parameters.get(TextInvoiceParameters.DESCUENTO_LEY_1886), footer, parameters.get(TextInvoiceParameters.DESCUENTO_LEY_1886).length());
        }

        if (parameters.get(TextInvoiceParameters.DESCUENTO_TARIFA_DIGNIDAD) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.DESCUENTO_TARIFA_DIGNIDAD, parameters.get(TextInvoiceParameters.DESCUENTO_TARIFA_DIGNIDAD), footer, parameters.get(TextInvoiceParameters.DESCUENTO_TARIFA_DIGNIDAD).length());
        }

        if (parameters.get(TextInvoiceParameters.TASA_ALUMBRADO_PUBLICO) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.TASA_ALUMBRADO_PUBLICO, parameters.get(TextInvoiceParameters.TASA_ALUMBRADO_PUBLICO), footer, parameters.get(TextInvoiceParameters.TASA_ALUMBRADO_PUBLICO).length());
        }

        if (parameters.get(TextInvoiceParameters.TASA_ASEO_URBANO) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.TASA_ASEO_URBANO, parameters.get(TextInvoiceParameters.TASA_ASEO_URBANO), footer, parameters.get(TextInvoiceParameters.TASA_ASEO_URBANO).length());
        }

        if (parameters.get(TextInvoiceParameters.OTROS_PAGOS) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.OTROS_PAGOS, parameters.get(TextInvoiceParameters.OTROS_PAGOS), footer, parameters.get(TextInvoiceParameters.OTROS_PAGOS).length());
        }

        if (parameters.get(TextInvoiceParameters.OTRAS_TASAS) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.OTRAS_TASAS, parameters.get(TextInvoiceParameters.OTRAS_TASAS), footer, parameters.get(TextInvoiceParameters.OTRAS_TASAS).length());
        }

        if (parameters.get(TextInvoiceParameters.NRO_SERIE) != null) {
            footer = replace(TextInvoiceParameters.NRO_SERIE, parameters.get(TextInvoiceParameters.NRO_SERIE), footer, 0);
        }

        if (parameters.get(TextInvoiceParameters.FTR_MONTO_GIFT) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_GIFT, parameters.get(TextInvoiceParameters.FTR_MONTO_GIFT), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_GIFT).length());
        }

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA, parameters.get(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA)));

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_QR, qr));

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MENSAJE, MENSAJE));

        return footer.toString();

    }

    protected abstract String[] generateHeaderPOS();

    protected String generateHeaderPOSDefault() {
        String response = new String(this.header);

        response = response.replace(TextInvoiceParameters.HDR_RAZON_SOCIAL_EMISOR, parameters.get(TextInvoiceParameters.HDR_RAZON_SOCIAL_EMISOR));
        response = response.replace(TextInvoiceParameters.HDR_DIRECCION, parameters.get(TextInvoiceParameters.HDR_DIRECCION));
        response = response.replace(TextInvoiceParameters.HDR_TELEFONO, parameters.get(TextInvoiceParameters.HDR_TELEFONO));
        response = response.replace(TextInvoiceParameters.HDR_MUNICIPIO, parameters.get(TextInvoiceParameters.HDR_MUNICIPIO));

        response = response.replace(TextInvoiceParameters.HDR_NIT, parameters.get(TextInvoiceParameters.HDR_NIT));
        response = response.replace(TextInvoiceParameters.HDR_FACTURA, parameters.get(TextInvoiceParameters.HDR_FACTURA));
        response = response.replace(TextInvoiceParameters.HDR_CUF, parameters.get(TextInvoiceParameters.HDR_CUF));

        if (parameters.get(TextInvoiceParameters.HDR_COD_CONTROL) !=null) {
            response = response.replace(TextInvoiceParameters.HDR_COD_CONTROL, parameters.get(TextInvoiceParameters.HDR_COD_CONTROL));
        }

        if (parameters.get(TextInvoiceParameters.HDR_NOMBRE_ALUMNO) != null) {
            response = response.replace(TextInvoiceParameters.HDR_NOMBRE_ALUMNO, parameters.get(TextInvoiceParameters.HDR_NOMBRE_ALUMNO));
        }

        if (parameters.get(TextInvoiceParameters.HDR_INCOTERM)!= null){
            response = response.replace(TextInvoiceParameters.HDR_INCOTERM, parameters.get(TextInvoiceParameters.HDR_INCOTERM));
        }

        String actividad = getActividad();
        if (actividad.equals("-")) {
            response = response.replace(TextInvoiceParameters.HDR_ACTIVIDAD, "-");
            response = response.replace(TextInvoiceParameters.HDR_ACTIVIDAD, "-");
        } else {
            response = response.replace(TextInvoiceParameters.HDR_ACTIVIDAD, parameters.get(TextInvoiceParameters.HDR_ACTIVIDAD));
        }

        if (parameters.get(TextInvoiceParameters.HDR_KEY_1) != null) {
            response = response.replace(TextInvoiceParameters.HDR_KEY_1, parameters.get(TextInvoiceParameters.HDR_KEY_1));
        }

        if (parameters.get(TextInvoiceParameters.HDR_KEY_2) != null) {
            response = response.replace(TextInvoiceParameters.HDR_KEY_2, parameters.get(TextInvoiceParameters.HDR_KEY_2));
        }

        if (parameters.get(TextInvoiceParameters.HDR_KEY_3) != null) {
            response = response.replace(TextInvoiceParameters.HDR_KEY_3, parameters.get(TextInvoiceParameters.HDR_KEY_3));
        }

        if (parameters.get(TextInvoiceParameters.HDR_KEY_4) != null) {
            response = response.replace(TextInvoiceParameters.HDR_KEY_4, parameters.get(TextInvoiceParameters.HDR_KEY_4));
        }

        if (parameters.get(TextInvoiceParameters.HDR_VALUE_1) != null) {
            response = response.replace(TextInvoiceParameters.HDR_VALUE_1, parameters.get(TextInvoiceParameters.HDR_VALUE_1));
        }
        if (parameters.get(TextInvoiceParameters.HDR_VALUE_2) != null) {
            response = response.replace(TextInvoiceParameters.HDR_VALUE_2, parameters.get(TextInvoiceParameters.HDR_VALUE_2));
        }
        if (parameters.get(TextInvoiceParameters.HDR_VALUE_3) != null) {
            response = response.replace(TextInvoiceParameters.HDR_VALUE_3, parameters.get(TextInvoiceParameters.HDR_VALUE_3));
        }
        if (parameters.get(TextInvoiceParameters.HDR_VALUE_4) != null) {
            response = response.replace(TextInvoiceParameters.HDR_VALUE_4, parameters.get(TextInvoiceParameters.HDR_VALUE_4));
        }

        response = response.replace(TextInvoiceParameters.HDR_FECHA, (parameters.get(TextInvoiceParameters.HDR_FECHA)));
        response = response.replace(TextInvoiceParameters.HDR_NIT_CI_EX, parameters.get(TextInvoiceParameters.HDR_NIT_CI_EX));

        if (parameters.get(TextInvoiceParameters.HDR_CMPL)!= null && parameters.get(TextInvoiceParameters.HDR_CMPL).trim().length() == 0){
            response = response.replace("COMPLEMENTO:", "");
        }
        response = response.replace(TextInvoiceParameters.HDR_CMPL, parameters.get(TextInvoiceParameters.HDR_CMPL));

        response = response.replace(TextInvoiceParameters.HDR_NOMBRE_RAZON_SOCIAL, parameters.get(TextInvoiceParameters.HDR_NOMBRE_RAZON_SOCIAL));
        response = response.replace(TextInvoiceParameters.HDR_PUNTO_VENTA, parameters.get(TextInvoiceParameters.HDR_PUNTO_VENTA));
        response = response.replace(TextInvoiceParameters.HDR_COD_CLIENTE, parameters.get(TextInvoiceParameters.HDR_COD_CLIENTE));

        if (parameters.get(TextInvoiceParameters.HDR_MONEDA_TRANSACCION) != null) {
            response = response.replace(TextInvoiceParameters.HDR_MONEDA_TRANSACCION, parameters.get(TextInvoiceParameters.HDR_MONEDA_TRANSACCION));
        }

        if (parameters.get(TextInvoiceParameters.HDR_DIRECCION_COMPRADOR) != null) {
            response = response.replace(TextInvoiceParameters.HDR_DIRECCION_COMPRADOR, parameters.get(TextInvoiceParameters.HDR_DIRECCION_COMPRADOR));
        }

        if (parameters.get(TextInvoiceParameters.HDR_LUGAR_DESTINO) != null) {
            response = response.replace(TextInvoiceParameters.HDR_LUGAR_DESTINO, parameters.get(TextInvoiceParameters.HDR_LUGAR_DESTINO));
        }

        if (parameters.get(TextInvoiceParameters.HDR_TIPO_CAMBIO) != null) {
            response = response.replace(TextInvoiceParameters.HDR_TIPO_CAMBIO, parameters.get(TextInvoiceParameters.HDR_TIPO_CAMBIO));
        }

        if (parameters.get(TextInvoiceParameters.HDR_MONEDA_TRANSACCION) != null) {
            response = response.replace(TextInvoiceParameters.HDR_MONEDA_TRANSACCION, parameters.get(TextInvoiceParameters.HDR_MONEDA_TRANSACCION));
        }

        if (parameters.get(TextInvoiceParameters.HDR_NRO_CLIENTE) != null) {
            response = response.replace(TextInvoiceParameters.HDR_NRO_CLIENTE, parameters.get(TextInvoiceParameters.HDR_NRO_CLIENTE));
        }

        if (parameters.get(TextInvoiceParameters.HDR_CLIENTE_DIRECCION) != null) {
            response = response.replace(TextInvoiceParameters.HDR_CLIENTE_DIRECCION, parameters.get(TextInvoiceParameters.HDR_CLIENTE_DIRECCION));
        }

        if (parameters.get(TextInvoiceParameters.HDR_CONSUMO_PERIODO) != null) {
            response = response.replace(TextInvoiceParameters.HDR_CONSUMO_PERIODO, parameters.get(TextInvoiceParameters.HDR_CONSUMO_PERIODO));
        }

        if (parameters.get(TextInvoiceParameters.HDR_BENEFICIARIO) != null) {
            response = response.replace(TextInvoiceParameters.HDR_BENEFICIARIO, parameters.get(TextInvoiceParameters.HDR_BENEFICIARIO));
        }

        if (parameters.get(TextInvoiceParameters.HDR_PERIODO) != null) {
            response = response.replace(TextInvoiceParameters.HDR_PERIODO, parameters.get(TextInvoiceParameters.HDR_PERIODO));
        }

        if (parameters.get(TextInvoiceParameters.HDR_NRO_MEDIDOR) != null) {
            response = response.replace(TextInvoiceParameters.HDR_NRO_MEDIDOR, parameters.get(TextInvoiceParameters.HDR_NRO_MEDIDOR));
        }

        if (parameters.get(TextInvoiceParameters.HDR_FECHA_FACTURA) != null) {
            response = response.replace(TextInvoiceParameters.HDR_FECHA_FACTURA, parameters.get(TextInvoiceParameters.HDR_FECHA_FACTURA));
        }

        if (parameters.get(TextInvoiceParameters.HDR_NRO_FACTURA) != null) {
            response = response.replace(TextInvoiceParameters.HDR_NRO_FACTURA, parameters.get(TextInvoiceParameters.HDR_NRO_FACTURA));
        }

        if (parameters.get(TextInvoiceParameters.HDR_CUF_FACTURA) != null) {
            response = response.replace(TextInvoiceParameters.HDR_CUF_FACTURA, parameters.get(TextInvoiceParameters.HDR_CUF_FACTURA));
        }

        return response;
    }

    protected abstract String[] generateDetailsPOS();

    protected String generateDetailsPOSDefault() {
        String details = "";

        for (HashMap<String, String> item : data) {
            String row = new String(this.details);
            row = row.replace(TextInvoiceParameters.CODIGO_PRODUCTO, item.get(TextInvoiceParameters.CODIGO_PRODUCTO));

            StringBuilder cantidad = new StringBuilder(row);
            cantidad = replace(TextInvoiceParameters.CANTI, item.get(TextInvoiceParameters.CANTI), cantidad);

            cantidad = replace(TextInvoiceParameters.PREC_UNITA, item.get(TextInvoiceParameters.PREC_UNITA), cantidad);
            cantidad = replace(TextInvoiceParameters.UNIDAD_MEDIDA, item.get(TextInvoiceParameters.UNIDAD_MEDIDA), cantidad);

            row = cantidad.toString();

            row = row.replace(TextInvoiceParameters.DESCRIPCION, item.get(TextInvoiceParameters.DESCRIPCION));


            row = row.replace(TextInvoiceParameters.PREC_UNITA, item.get(TextInvoiceParameters.PREC_UNITA));
            row = row.replace(TextInvoiceParameters.DESCUENTO, item.get(TextInvoiceParameters.DESCUENTO));

            StringBuilder subTotal = new StringBuilder(row);
            subTotal = replaceNumberValues(TextInvoiceParameters.SUB_TOTAL, item.get(TextInvoiceParameters.SUB_TOTAL),
                subTotal, item.get(TextInvoiceParameters.SUB_TOTAL).length());
            row = subTotal.toString();

            if (item.get(TextInvoiceParameters.ESPECIALIDAD) != null) {
                row = row.replace(TextInvoiceParameters.ESPECIALIDAD, item.get(TextInvoiceParameters.ESPECIALIDAD));
            }

            if (item.get(TextInvoiceParameters.QUIROFANO) != null) {
                StringBuilder hospitales = new StringBuilder(row);
                hospitales = replace(TextInvoiceParameters.QUIROFANO, item.get(TextInvoiceParameters.QUIROFANO), hospitales);

                row = hospitales.toString();
            }

            if (item.get(TextInvoiceParameters.MEDICO) != null) {
                StringBuilder hospitales = new StringBuilder(row);
                hospitales = replace(TextInvoiceParameters.MEDICO, item.get(TextInvoiceParameters.MEDICO), hospitales);

                row = hospitales.toString();
            }

            if (item.get(TextInvoiceParameters.ESPECIALIDAD_MED) != null) {
                StringBuilder hospitales = new StringBuilder(row);
                hospitales = replace(TextInvoiceParameters.ESPECIALIDAD_MED, item.get(TextInvoiceParameters.ESPECIALIDAD_MED), hospitales);

                row = hospitales.toString();
            }

            if (item.get(TextInvoiceParameters.NIT) != null) {
                StringBuilder hospitales = new StringBuilder(row);
                hospitales = replace(TextInvoiceParameters.NIT, item.get(TextInvoiceParameters.NIT), hospitales);

                row = hospitales.toString();
            }

            if (item.get(TextInvoiceParameters.NRO_FACTURA) != null) {
                StringBuilder hospitales = new StringBuilder(row);
                hospitales = replace(TextInvoiceParameters.NRO_FACTURA, item.get(TextInvoiceParameters.NRO_FACTURA), hospitales);

                row = hospitales.toString();
            }

            details += row;

        }

        return details;
    }

    protected abstract String[] generateFooterPOS();

    protected String generateFooterPOSDefault() {
        StringBuilder footer = new StringBuilder(this.footer);

        footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_TOTAL, parameters.get(TextInvoiceParameters.FTR_MONTO_TOTAL), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_TOTAL).length());
        footer = replaceNumberValues(TextInvoiceParameters.FTR_MNT_SUBTOTAL, parameters.get(TextInvoiceParameters.FTR_MNT_SUBTOTAL), footer, parameters.get(TextInvoiceParameters.FTR_MNT_SUBTOTAL).length());

        if (parameters.get(TextInvoiceParameters.FTR_MONTO_IVA) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_IVA, parameters.get(TextInvoiceParameters.FTR_MONTO_IVA), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_IVA).length());
        }
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_TOTAL_LITERAL, parameters.get(TextInvoiceParameters.FTR_TOTAL_LITERAL)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_TOTAL_DECIMAL, parameters.get(TextInvoiceParameters.FTR_TOTAL_DECIMAL)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MONEDA_LITERAL, parameters.get(TextInvoiceParameters.FTR_MONEDA_LITERAL)));
        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA, parameters.get(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA)));

        String qr = String.format(siatUrl,
            parameters.get(TextInvoiceParameters.HDR_NIT), parameters.get(TextInvoiceParameters.HDR_CUF), parameters.get(TextInvoiceParameters.HDR_FACTURA),
            parameters.get(TextInvoiceParameters.HDR_CODIGO_SECTOR));

        if (parameters.get(TextInvoiceParameters.FTR_ARRENDAMIEN) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_ARRENDAMIEN, parameters.get(TextInvoiceParameters.FTR_ARRENDAMIEN), footer, parameters.get(TextInvoiceParameters.FTR_ARRENDAMIEN).length());
        }

        if (parameters.get(TextInvoiceParameters.FTR_MONTO_ARREND) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_ARREND, parameters.get(TextInvoiceParameters.FTR_MONTO_ARREND), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_ARREND).length());
        }

        if (parameters.get(TextInvoiceParameters.FTR_TOTA_CAMBIO) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_TOTA_CAMBIO, parameters.get(TextInvoiceParameters.FTR_TOTA_CAMBIO), footer, parameters.get(TextInvoiceParameters.FTR_TOTA_CAMBIO).length());
        }

        if (parameters.get(TextInvoiceParameters.FTR_MON_CAMBIO) != null) {
            footer = replace(TextInvoiceParameters.FTR_MON_CAMBIO, parameters.get(TextInvoiceParameters.FTR_MON_CAMBIO), footer, 0);
        }

        if (parameters.get(TextInvoiceParameters.FTR_MONE_TOTAL) != null) {
            footer = replace(TextInvoiceParameters.FTR_MONE_TOTAL, parameters.get(TextInvoiceParameters.FTR_MONE_TOTAL), footer, 0);
        }

        if (parameters.get(TextInvoiceParameters.FTR_CAMBIO_LITERAL) != null) {
            footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_CAMBIO_LITERAL, parameters.get(TextInvoiceParameters.FTR_CAMBIO_LITERAL)));
        }

        if (parameters.get(TextInvoiceParameters.FTR_CAMBIO_DECIMAL) != null) {
            footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_CAMBIO_DECIMAL, parameters.get(TextInvoiceParameters.FTR_CAMBIO_DECIMAL)));
        }

        if (parameters.get(TextInvoiceParameters.FTR_MON_CAMBIO_LITERAL) != null) {
            footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MON_CAMBIO_LITERAL, parameters.get(TextInvoiceParameters.FTR_MON_CAMBIO_LITERAL)));
        }

        if (parameters.get(TextInvoiceParameters.FTR_INFORMACION_ADICIONAL) != null) {
            footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_INFORMACION_ADICIONAL, parameters.get(TextInvoiceParameters.FTR_INFORMACION_ADICIONAL)));
        }

        if (parameters.get(TextInvoiceParameters.FTR_MONTO_LEY) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_LEY, parameters.get(TextInvoiceParameters.FTR_MONTO_LEY), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_LEY).length());
        }

        if (parameters.get(TextInvoiceParameters.AJUSTE_NO_IVA) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.AJUSTE_NO_IVA, parameters.get(TextInvoiceParameters.AJUSTE_NO_IVA), footer, parameters.get(TextInvoiceParameters.AJUSTE_NO_IVA).length());
        }

        if (parameters.get(TextInvoiceParameters.DESCUENTO_LEY_1886) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.DESCUENTO_LEY_1886, parameters.get(TextInvoiceParameters.DESCUENTO_LEY_1886), footer, parameters.get(TextInvoiceParameters.DESCUENTO_LEY_1886).length());
        }

        if (parameters.get(TextInvoiceParameters.DESCUENTO_TARIFA_DIGNIDAD) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.DESCUENTO_TARIFA_DIGNIDAD, parameters.get(TextInvoiceParameters.DESCUENTO_TARIFA_DIGNIDAD), footer, parameters.get(TextInvoiceParameters.DESCUENTO_TARIFA_DIGNIDAD).length());
        }

        if (parameters.get(TextInvoiceParameters.TASA_ALUMBRADO_PUBLICO) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.TASA_ALUMBRADO_PUBLICO, parameters.get(TextInvoiceParameters.TASA_ALUMBRADO_PUBLICO), footer, parameters.get(TextInvoiceParameters.TASA_ALUMBRADO_PUBLICO).length());
        }

        if (parameters.get(TextInvoiceParameters.TASA_ASEO_URBANO) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.TASA_ASEO_URBANO, parameters.get(TextInvoiceParameters.TASA_ASEO_URBANO), footer, parameters.get(TextInvoiceParameters.TASA_ASEO_URBANO).length());
        }

        if (parameters.get(TextInvoiceParameters.OTROS_PAGOS) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.OTROS_PAGOS, parameters.get(TextInvoiceParameters.OTROS_PAGOS), footer, parameters.get(TextInvoiceParameters.OTROS_PAGOS).length());
        }

        if (parameters.get(TextInvoiceParameters.OTRAS_TASAS) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.OTRAS_TASAS, parameters.get(TextInvoiceParameters.OTRAS_TASAS), footer, parameters.get(TextInvoiceParameters.OTRAS_TASAS).length());
        }

        if (parameters.get(TextInvoiceParameters.NRO_SERIE) != null) {
            footer = replace(TextInvoiceParameters.NRO_SERIE, parameters.get(TextInvoiceParameters.NRO_SERIE), footer, 0);
        }

        if (parameters.get(TextInvoiceParameters.FTR_MONTO_GIFT) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_GIFT, parameters.get(TextInvoiceParameters.FTR_MONTO_GIFT), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_GIFT).length());
        }

        if (parameters.get(TextInvoiceParameters.FTR_MONTO_DESCUENTO) != null) {
            footer = replaceNumberValues(TextInvoiceParameters.FTR_MONTO_DESCUENTO, parameters.get(TextInvoiceParameters.FTR_MONTO_DESCUENTO), footer, parameters.get(TextInvoiceParameters.FTR_MONTO_DESCUENTO).length());
        }

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA, parameters.get(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA)));

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_QR, qr));

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_MENSAJE, MENSAJE));

        footer = new StringBuilder(footer.toString().replace(TextInvoiceParameters.FTR_LEYENDA, parameters.get(TextInvoiceParameters.FTR_LEYENDA)));

        return footer.toString();
    }

    public static StringBuilder replace(String tag, String value, StringBuilder line) {
        return replace(tag, value, line, 0);
    }

    public static StringBuilder replace(String tag, String value, StringBuilder line, int index) {

        int indexOf = line.indexOf(tag);
        int endIndexOf = line.indexOf(";", indexOf);

        if (indexOf == -1 || endIndexOf == -1)
            return line;

        for (int i = indexOf; i <= endIndexOf; i++) {
            if (line.charAt(i) != ';') {
                if (index < value.length()) {
                    line.setCharAt(i, value.charAt(index));
                    index++;
                } else {
                    line.setCharAt(i, ' ');
                }
            } else {
                line.setCharAt(i, ' ');
                if (index < value.length()) {
                    return replace(tag, value, line, index);
                }
            }
        }
        return line;
    }

    public static StringBuilder replaceNumberValues(String tag, String value, StringBuilder line, int index) {

        int indexOf = line.indexOf(tag);
        int endIndexOf = line.indexOf(">", indexOf);

        int indexOfComma = line.lastIndexOf(";", indexOf);

        if (value == null){
            return line ;
        }

        if (indexOf == -1 || endIndexOf == -1)
            return line;

        for (int i = endIndexOf; i >= indexOfComma; i--) {
            if (line.charAt(i) != ';') {
                if (index > 0) {
                    line.setCharAt(i, value.charAt(index - 1));
                    index--;
                } else {
                    line.setCharAt(i, ' ');
                }
            } else {
                line.setCharAt(i, ' ');
            }
        }
        return line;
    }

    public static String[] getStringArray(ArrayList<String> arr) {
        // declaration and initialise String Array
        String str[] = new String[arr.size()];
        // ArrayList to Array Conversion
        for (int j = 0; j < arr.size(); j++) {
            // Assign each value to String array
            str[j] = arr.get(j);
        }
        return str;
    }

    public static String center(String s, int size) {
        return center(s, size, ' ');
    }

    public static String center(String s, int size, char pad) {
        if (s == null || size <= s.length()) {
            return s;
        }

        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < (size - s.length()) / 2; i++) {
            sb.append(pad);
        }
        sb.append(s);
        while (sb.length() < size) {
            sb.append(pad);
        }
        return sb.toString();
    }

    protected static <T> T[] concatWithStream(T[] array1, T[] array2) {
        return Stream.concat(Arrays.stream(array1), Arrays.stream(array2))
            .toArray(size -> (T[]) Array.newInstance(array1.getClass().getComponentType(), size));
    }

    public String[] split(String data, String regex, Integer length, boolean center) {
        String[] responseArray = data.split(regex);
        ArrayList<String> list = new ArrayList<>();
        for (String item : responseArray) {
            if (item.contains("<QR>")){
                list.add(item);
            }else
            if (!item.contains("HDR_KEY") && !item.contains("HDR_VALUE")) {
                if (item.length() > length) {
                    String[] splitted = item.split("(?<=\\G.{" + length + "})");
                    for (String i : splitted) {
                        list.add(i);
                    }
                } else {
                    if (center) {
                        item = center(item, 32);
                    }
                    list.add(item);
                }
            }
        }

        return getStringArray(list);
    }

    protected String getActividad() {
        Optional<ApprovedProduct> fromDb = approvedProductRepository.findApprovedProductByCompanyIdAndProductCode(companyId, productCode);
        if (fromDb.isPresent()) {
            ApprovedProduct approvedProduct = fromDb.get();
            Optional<Activity> actFromDb = activityRepository.findDistinctFirstBySiatId(approvedProduct.getProductService().getActivityCode());
            if (actFromDb.isPresent()) {
                Activity activity = actFromDb.get();
                return activity.getDescription();
            } else {
                return "-";
            }
        } else {
            return "-";
        }
    }

    protected String integerToLiteral(Integer value) {
        Spanish literal = new Spanish();
        String result = literal.convert(value);
        result = result.substring(0,1).toUpperCase() + result.substring(1);
        return result;
    }

    protected String obtainDecimalPart(BigDecimal value) {
        BigDecimal fractional = value.remainder(BigDecimal.ONE);
        String result = fractional.setScale(2, BigDecimal.ROUND_CEILING).toString();
        return result.substring(result.indexOf('.') + 1);
    }

    public void setApprovedProductRepository(ApprovedProductRepository approvedProductRepository) {
        this.approvedProductRepository = approvedProductRepository;
    }

    public void setActivityRepository(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getPointOfSale() {
        return pointOfSale;
    }

    public void setPointOfSale(String pointOfSale) {
        this.pointOfSale = pointOfSale;
    }

    public void setCurrencyTypeRepository(CurrencyTypeRepository currencyTypeRepository) {
        this.currencyTypeRepository = currencyTypeRepository;
    }

    public void setSiatUrl(String siatUrl) {
        this.siatUrl = "<QR>"+siatUrl;
    }

    protected String getUnidadMedidadLiteral(Integer unidadMedida){
            Optional<MeasurementUnit> unit =  this.measurementUnitRepository.findAllByCompanyIdAndSiatIdAndActiveIsTrue(companyId.intValue(), unidadMedida);
            if(unit.isPresent()){
                return unit.get().getDescription();
            }else {
                return "" ;
            }

    }
}
