package bo.com.luminia.sflbilling.msreport.siat.text.invoices.templates;

public class TextServiciosBasicosTemplate {
    /**
     * Servicios Basicos A4
     */
    public static final String HEADER_A4 =
        "\n" +
            "<b>                                                                                    NIT : <HDR_NIT>                            ;\n" +
            "<b><HDR_RAZON_SOCIAL_EMISOR>                                 ;                  FACTURA No.: <HDR_FACTURA>                        ;\n" +
            "<b><HDR_DIRECCION>                                           ;                   CÓDIGO DE : <HDR_CUF>                            ;\n" +
            "<b>Telf. : <HDR_TELEFONO>                                              ;      AUTORIZACIÓN   <HDR_CUF>                            ;\n" +
            "<b><HDR_MUNICIPIO>                                           ;;                <HDR_KEY_1> : <HDR_VALUE_1>                        ;\n" +
            "                                                             ;                 <HDR_KEY_2> : <HDR_VALUE_2>                        ;\n" +
            "                                                             ;                 <HDR_KEY_3> : <HDR_VALUE_3>                        ;\n" +
            "                                                             ;                 <HDR_KEY_4> : <HDR_VALUE_4>                        ;\n" +
            "\n" +
            "                                                            FACTURA\n" +
            "                                                 (Con Derecho a Credito Fiscal)\n" +
            "\n" +
            "              Fecha : <HDR_FECHA>                                       ;      NIT/CI/CEX :<HDR_NIT_CI_EX>; COMPLEMENTO:<HDR_CMPL>;\n" +
            "Nombre/Razón Social : <HDR_NOMBRE_RAZON_SOCIAL>                        ; Benefic. Ley 1886: <HDR_BENEFICIARIO>                    ;\n" +
            "        Nro Cliente : <HDR_NRO_CLIENTE>                                ;          Período : <HDR_PERIODO>                         ;\n" +
            "          Dirección : <HDR_CLIENTE_DIRECCION>                          ;      NRO Medidor : <HDR_NRO_MEDIDOR>                     ;\n" +
            "    Consumo Período : <HDR_CONSUMO_PERIODO>                            ;\n" +
            "\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  \n" +
            "COD.SERVICIO  | CANTIDAD | UNIDAD MEDIDA   | DESCRIPCIÓN                                     | PRECIO UNIT | DESCUEN |   SUBTOTAL |\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  ";

    public static final String HEADER_A4_2 =
        "\n" +
            "<b>                                                                                    NIT : <HDR_NIT>                            ;\n" +
            "<b><HDR_RAZON_SOCIAL_EMISOR>                                 ;                  FACTURA No.: <HDR_FACTURA>                        ;\n" +
            "<b><HDR_DIRECCION>                                           ;                   CÓDIGO DE : <HDR_CUF>                            ;\n" +
            "<b><HDR_DIRECCION>                                           ;                AUTORIZACIÓN   <HDR_CUF>                            ;\n" +
            "<b>Telf. : <HDR_TELEFONO>                                    ;;                <HDR_KEY_1> : <HDR_VALUE_1>                        ;\n" +
            "<b><HDR_MUNICIPIO>                                           ;;                <HDR_KEY_2> : <HDR_VALUE_2>                        ;\n" +
            "                                                             ;                 <HDR_KEY_3> : <HDR_VALUE_3>                        ;\n" +
            "                                                             ;                 <HDR_KEY_4> : <HDR_VALUE_4>                        ;\n" +
            "\n" +
            "                                                            FACTURA\n" +
            "                                                 (Con Derecho a Crédito Fiscal)\n" +
            "\n" +
            "              Fecha : <HDR_FECHA>                                       ;      NIT/CI/CEX :<HDR_NIT_CI_EX>; COMPLEMENTO:<HDR_CMPL>;\n" +
            "Nombre/Razón Social : <HDR_NOMBRE_RAZON_SOCIAL>                        ; Benefic. Ley 1886: <HDR_BENEFICIARIO>                    ;\n" +
            "        Nro Cliente : <HDR_NRO_CLIENTE>                                ;          Período : <HDR_PERIODO>                         ;\n" +
            "          Dirección : <HDR_CLIENTE_DIRECCION>                          ;     Nro. Medidor : <HDR_NRO_MEDIDOR>                     ;\n" +
            "    Consumo Período : <HDR_CONSUMO_PERIODO>                            ;\n" +
            "\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  \n" +
            "COD.SERVICIO  | CANTIDAD | UNIDAD MEDIDA   | DESCRIPCIÓN                                     | PRECIO UNIT | DESCUEN |   SUBTOTAL |\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  ";


    public static final String DETAILS_A4 =
            "<codProducto> ;;<cantid>  <uniMedida>       ;<descripcion>                                    ;;<precUnit>  ;<descu>;  <subTotal> |" ;


    public static final String FOOTER_A4 =
            "                                             Nro. de Serie: <nroSerie>                          ;                                 |\n" +
            "                                             Ajustes no sujetos a IVA                                                ;<ajusNoIva> |\n" +
            "                                             Descuentos Ley 1886                                                     ;<desLe1886> |\n" +
            "                                             Descuentos Tarifa Dignidad                                              ;<desTarDig> |\n" +
            "                                             Tasa Alumbrado Publico                                                  ;<tasAluPub> |\n" +
            "                                             Tasa Aseo Urbano                                                        ;<tasAseUrb> |\n" +
            "                                             Otros Pagos (cuotas, etc)                                               ;<pagoCuota> |\n" +
            "                                             Otras Tasas                                                            ;<otrasTasas> |\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - \n" +
            "                                                                                                TOTAL Bs.      ;<FTR_MONTO_TOTAL>\n" +
            "                                                                           IMPORTE BASE CRÉDITO FISCAL Bs.      ; <FTR_MONTO_IVA>\n" +
            "SON: <FTR_TOTAL_LITERAL> <FTR_TOTAL_DECIMAL>/100 <FTR_MONEDA_LITERAL>\n" +
            "\n" +
            "<FTR_MENSAJE>\n" +
            "\n" +
            "<FTR_LEYENDA>\n" +
            "\n" +
            "<FTR_MODALIDAD_LEYENDA>\n" +
            "\n" +
            "<FTR_QR>\n";

    /**
     * Compra Venta POS
     */
    public static final String HEADER_POS =
        "FACTURA;" +
            "CON DERECHO A CRÉDITO FISCAL;" +
            ";" +
            "<HDR_RAZON_SOCIAL_EMISOR>;" +
            "<HDR_PUNTO_VENTA>;" +
            "<HDR_DIRECCION>;" +
            "TEL. <HDR_TELEFONO>;" +
            "<HDR_MUNICIPIO>;" +
            "- - - - - - - - - - - - - - - -;" +
            "NIT;" +
            "<HDR_NIT>;" +
            "No FACTURA;" +
            "<HDR_FACTURA>;" +
            "CÓDIGO DE AUTORIZACIÓN;" +
            "<HDR_CUF>;" +
            "<HDR_KEY_1>;" +
            "<HDR_VALUE_1>;" +
            "<HDR_KEY_2>;" +
            "<HDR_VALUE_2>;" +
            "<HDR_KEY_3>;" +
            "<HDR_VALUE_3>;" +
            "<HDR_KEY_4>;" +
            "<HDR_VALUE_4>;" +
            "- - - - - - - - - - - - - - - -;" +
            "NOMBRE/RAZÓN:<HDR_NOMBRE_RAZON_SOCIAL>;" +
            "   NIT/CI/EX:<HDR_NIT_CI_EX>;" +
            "COMPLEMENTO:<HDR_CMPL>;" +
            " COD.CLIENTE:<HDR_COD_CLIENTE>;" +
            "       FECHA:<HDR_FECHA>;" +
            " NRO MEDIDOR:<HDR_NRO_MEDIDOR>;" +
            "CONSUMO PERÍODO:<HDR_CONSUMO_PERIODO>;" +
            "BENEF. LEY 1886:<HDR_BENEFICIARIO>;" +
            "DIRECCIÓN:<HDR_CLIENTE_DIRECCION>;" +

            "- - - - - - - - - - - - - - - -;" +
            "DETALLE;";

    public static final String DETAILS_POS =
        "<codProducto>-<descripcion>;" +
            "<cant>;x<precUnit>;;  <subTotal>;";


    public static final String FOOTER_POS =
        "Ajustes no suj. Iva:;<ajusNoIva>;" +
            "Descuento Ley 1886 ;<desLe1886>;" +
            "Desc. Tarf. Dignid ;<desTarDig>;" +
            "Tasa Alumbrado Pub ;<tasAluPub>;" +
            "Tasa Aseo Urbano   ;<tasAseUrb>;" +
            "Otros Pagos        ;<pagoCuota>;" +
            "Otras Tasas       ;<otrasTasas>;" +
            "- - - - - - - - - - - - - - - -;" +
            "TOTAL Bs.    ;<FTR_MONTO_TOTAL>;" +
            "IMP.CRE.FIS Bs.;<FTR_MONTO_IVA>;" +
            "Son:;" +
            "<FTR_TOTAL_LITERAL> <FTR_TOTAL_DECIMAL>/100 <FTR_MONEDA_LITERAL>;" +
            ";" +
            "<FTR_MENSAJE>;" +
            ";" +
            "<FTR_LEYENDA>;" +
            ";" +
            "<FTR_MODALIDAD_LEYENDA>;" +
            ";" +
            "<FTR_QR>;";

}
