package bo.com.luminia.sflbilling.msreport.siat.text.invoices;

import bo.com.luminia.sflbilling.msreport.siat.pdf.PdfInvoice;
import bo.com.luminia.sflbilling.msreport.siat.text.TextInvoice;
import bo.com.luminia.sflbilling.msreport.siat.text.TextInvoiceParameters;
import bo.com.luminia.sflbilling.msreport.siat.text.invoices.templates.TextServiciosBasicosTemplate;
import bo.gob.impuestos.sfe.invoice.xml.XmlDateUtil;
import bo.gob.impuestos.sfe.invoice.xml.detail.XmlServiciosBasicosDetail;
import bo.gob.impuestos.sfe.invoice.xml.eltr.XmlServiciosBasicosEltrInvoice;

import java.util.HashMap;

public class TextEltrServiciosBasicosInvoice extends TextInvoice<XmlServiciosBasicosEltrInvoice> {

    public TextEltrServiciosBasicosInvoice(XmlServiciosBasicosEltrInvoice invoice, String paperType) {
        super(invoice, paperType);
        this.productCode = invoice.getDetalle().get(0).getCodigoProducto();
        if (paperType == TextInvoice.PAPER_A4) {
            if (invoice.getCabecera().getDireccion().length() > MAX_DIRECCION_LENGTH) {
                this.header = TextServiciosBasicosTemplate.HEADER_A4_2;
            }else {
                this.header = TextServiciosBasicosTemplate.HEADER_A4;
            }
            this.details = TextServiciosBasicosTemplate.DETAILS_A4;
            this.footer = TextServiciosBasicosTemplate.FOOTER_A4;
        } else {
            this.header = TextServiciosBasicosTemplate.HEADER_POS;
            this.details = TextServiciosBasicosTemplate.DETAILS_POS;
            this.footer = TextServiciosBasicosTemplate.FOOTER_POS;
        }
    }

    @Override
    protected String[] generateHeaderA4() {
        parameters.put(TextInvoiceParameters.HDR_RAZON_SOCIAL_EMISOR, invoice.getCabecera().getRazonSocialEmisor());
        parameters.put(TextInvoiceParameters.HDR_DIRECCION, invoice.getCabecera().getDireccion());
        parameters.put(TextInvoiceParameters.HDR_TELEFONO, invoice.getCabecera().getTelefono());
        parameters.put(TextInvoiceParameters.HDR_MUNICIPIO, invoice.getCabecera().getMunicipio());
        parameters.put(TextInvoiceParameters.HDR_NIT, invoice.getCabecera().getNitEmisor().toString());
        parameters.put(TextInvoiceParameters.HDR_FACTURA, invoice.getCabecera().getNumeroFactura().toString());
        parameters.put(TextInvoiceParameters.HDR_CUF, invoice.getCabecera().getCuf());
        parameters.put(TextInvoiceParameters.HDR_ACTIVIDAD, this.getActividad());
        parameters.put(TextInvoiceParameters.HDR_FECHA, XmlDateUtil.convertDateToYyyymmddhhmmssSSS(invoice.getCabecera().getFechaEmision()));
        parameters.put(TextInvoiceParameters.HDR_NIT_CI_EX, invoice.getCabecera().getNumeroDocumento());
        parameters.put(TextInvoiceParameters.HDR_CMPL, invoice.getCabecera().getComplemento()!= null ? invoice.getCabecera().getComplemento() : "");
        parameters.put(TextInvoiceParameters.HDR_NOMBRE_RAZON_SOCIAL, invoice.getCabecera().getNombreRazonSocial());
        parameters.put(TextInvoiceParameters.HDR_BENEFICIARIO, invoice.getCabecera().getBeneficiarioLey1886().toString());
        parameters.put(TextInvoiceParameters.HDR_NRO_CLIENTE, invoice.getCabecera().getCodigoCliente());
        parameters.put(TextInvoiceParameters.HDR_CLIENTE_DIRECCION, invoice.getCabecera().getDomicilioCliente());
        parameters.put(TextInvoiceParameters.HDR_CONSUMO_PERIODO, invoice.getCabecera().getConsumoPeriodo() != null ?
            invoice.getCabecera().getConsumoPeriodo().toString() : "0.00");
        parameters.put(TextInvoiceParameters.HDR_NRO_MEDIDOR, invoice.getCabecera().getNumeroMedidor());
        parameters.put(TextInvoiceParameters.HDR_PERIODO, invoice.getCabecera().getMes() + "/" + invoice.getCabecera().getGestion().toString());

        checkForOptional(invoice.getCabecera().getOptional());

        String header = generateHeaderA4Default();

        return split(header, "\n", LENGTH_A4, false);
    }

    @Override
    protected String[] generateDetailsA4() {
        for (XmlServiciosBasicosDetail item : invoice.getDetalle()) {
            HashMap<String, String> row = new HashMap<>();
            row.put(TextInvoiceParameters.CODIGO_PRODUCTO, item.getCodigoProducto());
            row.put(TextInvoiceParameters.CANTIDAD, item.getCantidad().toString());
            row.put(TextInvoiceParameters.PRECIO_UNITARIO, item.getPrecioUnitario().toString());
            row.put(TextInvoiceParameters.DESCRIPCION, item.getDescripcion());
            row.put(TextInvoiceParameters.DESCUENTO, item.getMontoDescuento().toString());
            row.put(TextInvoiceParameters.SUB_TOTAL, item.getSubTotal().toString());
            row.put(TextInvoiceParameters.UNIDAD_MEDIDA, this.getUnidadMedidadLiteral(item.getUnidadMedida()));

            data.add(row);
        }

        String details = generateDetailsA4Default();

        return split(details, "\n", LENGTH_A4, false);
    }

    @Override
    protected String[] generateFooterA4() {
        parameters.put(TextInvoiceParameters.FTR_MONTO_TOTAL, invoice.getCabecera().getMontoTotal().toString());
        parameters.put(TextInvoiceParameters.FTR_MONTO_IVA, invoice.getCabecera().getMontoTotalSujetoIva().toString());
        parameters.put(TextInvoiceParameters.FTR_TOTAL_LITERAL, integerToLiteral(invoice.getCabecera().getMontoTotal().intValue()));
        parameters.put(TextInvoiceParameters.FTR_TOTAL_DECIMAL, obtainDecimalPart(invoice.getCabecera().getMontoTotal()));
        parameters.put(TextInvoiceParameters.FTR_MONTO_SUBTOTAL, (PdfInvoice.detailsToSubtotal( invoice.getDetalle() )).toString());
        parameters.put(TextInvoiceParameters.FTR_MONEDA_LITERAL, TextInvoice.BOLIVIANOS);
        parameters.put(TextInvoiceParameters.HDR_CODIGO_SECTOR, invoice.getCabecera().getCodigoDocumentoSector().toString());
        parameters.put(TextInvoiceParameters.FTR_LEYENDA, invoice.getCabecera().getLeyenda());
        parameters.put(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA, this.modalidadLeyenda);
        parameters.put(TextInvoiceParameters.FTR_MONTO_DESCUENTO, "0.00");
        parameters.put(TextInvoiceParameters.FTR_MONTO_GIFT, "0.00");
        parameters.put(TextInvoiceParameters.NRO_SERIE, invoice.getCabecera().getNumeroMedidor());
        parameters.put(TextInvoiceParameters.AJUSTE_NO_IVA, invoice.getCabecera().getAjusteNoSujetoIva() != null ? invoice.getCabecera().getAjusteNoSujetoIva().toString() : "0.00");
        parameters.put(TextInvoiceParameters.DESCUENTO_LEY_1886, invoice.getCabecera().getMontoDescuentoLey1886() != null ? invoice.getCabecera().getMontoDescuentoLey1886().toString() : "0.00");
        parameters.put(TextInvoiceParameters.DESCUENTO_TARIFA_DIGNIDAD, invoice.getCabecera().getMontoDescuentoTarifaDignidad() != null ? invoice.getCabecera().getMontoDescuentoTarifaDignidad().toString() : "0.00");
        parameters.put(TextInvoiceParameters.TASA_ALUMBRADO_PUBLICO, invoice.getCabecera().getTasaAlumbrado() != null ? invoice.getCabecera().getTasaAlumbrado().toString() : "0.00");
        parameters.put(TextInvoiceParameters.TASA_ASEO_URBANO, invoice.getCabecera().getTasaAseo() != null ? invoice.getCabecera().getTasaAseo().toString() : "0.00");
        parameters.put(TextInvoiceParameters.OTRAS_TASAS, invoice.getCabecera().getOtrasTasas() != null ? invoice.getCabecera().getOtrasTasas().toString() : "0.00");
        parameters.put(TextInvoiceParameters.OTROS_PAGOS, invoice.getCabecera().getOtrosPagosNoSujetoIva() != null ? invoice.getCabecera().getOtrosPagosNoSujetoIva().toString() : "0.00");

        String footer = generateFooterA4Default();
        return split(footer, "\n", LENGTH_A4, false);
    }

    @Override
    protected String[] generateHeaderPOS() {
        parameters.put(TextInvoiceParameters.HDR_RAZON_SOCIAL_EMISOR, invoice.getCabecera().getRazonSocialEmisor());
        parameters.put(TextInvoiceParameters.HDR_DIRECCION, invoice.getCabecera().getDireccion());
        parameters.put(TextInvoiceParameters.HDR_TELEFONO, invoice.getCabecera().getTelefono());
        parameters.put(TextInvoiceParameters.HDR_MUNICIPIO, invoice.getCabecera().getMunicipio());
        parameters.put(TextInvoiceParameters.HDR_NIT, invoice.getCabecera().getNitEmisor().toString());
        parameters.put(TextInvoiceParameters.HDR_FACTURA, invoice.getCabecera().getNumeroFactura().toString());
        parameters.put(TextInvoiceParameters.HDR_CUF, invoice.getCabecera().getCuf());
        parameters.put(TextInvoiceParameters.HDR_ACTIVIDAD, this.getActividad());
        parameters.put(TextInvoiceParameters.HDR_FECHA, XmlDateUtil.convertDateToYyyymmddhhmmssSSS(invoice.getCabecera().getFechaEmision()));
        parameters.put(TextInvoiceParameters.HDR_NIT_CI_EX, invoice.getCabecera().getNumeroDocumento());
        parameters.put(TextInvoiceParameters.HDR_CMPL, invoice.getCabecera().getComplemento()!= null ? invoice.getCabecera().getComplemento() : "");
        parameters.put(TextInvoiceParameters.HDR_NOMBRE_RAZON_SOCIAL, invoice.getCabecera().getNombreRazonSocial());
        parameters.put(TextInvoiceParameters.HDR_PUNTO_VENTA, this.getPointOfSale());
        parameters.put(TextInvoiceParameters.HDR_COD_CLIENTE, invoice.getCabecera().getCodigoCliente());
        parameters.put(TextInvoiceParameters.HDR_BENEFICIARIO, invoice.getCabecera().getBeneficiarioLey1886().toString());
        parameters.put(TextInvoiceParameters.HDR_NRO_CLIENTE, invoice.getCabecera().getCodigoCliente());
        parameters.put(TextInvoiceParameters.HDR_CLIENTE_DIRECCION, invoice.getCabecera().getDomicilioCliente());
        parameters.put(TextInvoiceParameters.HDR_CONSUMO_PERIODO, invoice.getCabecera().getConsumoPeriodo() != null ?
            invoice.getCabecera().getConsumoPeriodo().toString() : "0.00");
        parameters.put(TextInvoiceParameters.HDR_NRO_MEDIDOR, invoice.getCabecera().getNumeroMedidor());
        parameters.put(TextInvoiceParameters.HDR_PERIODO, invoice.getCabecera().getMes() + "/" + invoice.getCabecera().getGestion().toString());

        checkForOptional(invoice.getCabecera().getOptional());

        String response = generateHeaderPOSDefault();
        return split(response, ";", LENGTH_POS, true);
    }

    @Override
    protected String[] generateDetailsPOS() {
        String details = "";
        for (XmlServiciosBasicosDetail item : invoice.getDetalle()) {
            HashMap<String, String> row = new HashMap<>();
            row.put(TextInvoiceParameters.CODIGO_PRODUCTO, item.getCodigoProducto());
            row.put(TextInvoiceParameters.CANTI, item.getCantidad().toString());
            row.put(TextInvoiceParameters.PREC_UNITA, item.getPrecioUnitario().toString());
            row.put(TextInvoiceParameters.DESCRIPCION, item.getDescripcion());
            row.put(TextInvoiceParameters.DESCUENTO, item.getMontoDescuento().toString());
            row.put(TextInvoiceParameters.SUB_TOTAL, item.getSubTotal().toString());
            row.put(TextInvoiceParameters.UNIDAD_MEDIDA, this.getUnidadMedidadLiteral(item.getUnidadMedida()));

            data.add(row);
        }

        details = generateDetailsPOSDefault();

        return split(details, ";", LENGTH_POS, false);
    }

    @Override
    protected String[] generateFooterPOS() {
        parameters.put(TextInvoiceParameters.FTR_MONTO_TOTAL, invoice.getCabecera().getMontoTotal().toString());
        parameters.put(TextInvoiceParameters.FTR_MONTO_IVA, invoice.getCabecera().getMontoTotalSujetoIva().toString());
        parameters.put(TextInvoiceParameters.FTR_TOTAL_LITERAL, integerToLiteral(invoice.getCabecera().getMontoTotal().intValue()));
        parameters.put(TextInvoiceParameters.FTR_TOTAL_DECIMAL, obtainDecimalPart(invoice.getCabecera().getMontoTotal()));
        parameters.put(TextInvoiceParameters.FTR_MNT_SUBTOTAL, (PdfInvoice.detailsToSubtotal( invoice.getDetalle() )).toString());
        parameters.put(TextInvoiceParameters.FTR_MONEDA_LITERAL, TextInvoice.BOLIVIANOS);
        parameters.put(TextInvoiceParameters.HDR_CODIGO_SECTOR, invoice.getCabecera().getCodigoDocumentoSector().toString());
        parameters.put(TextInvoiceParameters.NRO_SERIE, invoice.getCabecera().getNumeroMedidor());
        parameters.put(TextInvoiceParameters.AJUSTE_NO_IVA, invoice.getCabecera().getAjusteNoSujetoIva() != null ? invoice.getCabecera().getAjusteNoSujetoIva().toString() : "0.00");
        parameters.put(TextInvoiceParameters.DESCUENTO_LEY_1886, invoice.getCabecera().getMontoDescuentoLey1886() != null ? invoice.getCabecera().getMontoDescuentoLey1886().toString() : "0.00");
        parameters.put(TextInvoiceParameters.DESCUENTO_TARIFA_DIGNIDAD, invoice.getCabecera().getMontoDescuentoTarifaDignidad() != null ? invoice.getCabecera().getMontoDescuentoTarifaDignidad().toString() : "0.00");
        parameters.put(TextInvoiceParameters.TASA_ALUMBRADO_PUBLICO, invoice.getCabecera().getTasaAlumbrado() != null ? invoice.getCabecera().getTasaAlumbrado().toString() : "0.00");
        parameters.put(TextInvoiceParameters.TASA_ASEO_URBANO, invoice.getCabecera().getTasaAseo() != null ? invoice.getCabecera().getTasaAseo().toString() : "0.00");
        parameters.put(TextInvoiceParameters.OTRAS_TASAS, invoice.getCabecera().getOtrasTasas() != null ? invoice.getCabecera().getOtrasTasas().toString() : "0.00");
        parameters.put(TextInvoiceParameters.OTROS_PAGOS, invoice.getCabecera().getOtrosPagosNoSujetoIva() != null ? invoice.getCabecera().getOtrosPagosNoSujetoIva().toString() : "0.00");
        parameters.put(TextInvoiceParameters.FTR_LEYENDA, invoice.getCabecera().getLeyenda());
        parameters.put(TextInvoiceParameters.FTR_MODALIDAD_LEYENDA, this.modalidadLeyenda);
        parameters.put(TextInvoiceParameters.FTR_MONTO_DESCUENTO, "0.00");
        parameters.put(TextInvoiceParameters.FTR_MONTO_GIFT, "0.00");

        String footer = generateFooterPOSDefault();

        return split(footer, ";", LENGTH_POS, false);
    }
}
