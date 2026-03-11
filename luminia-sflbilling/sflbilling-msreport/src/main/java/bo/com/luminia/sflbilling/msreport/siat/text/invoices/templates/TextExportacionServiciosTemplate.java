package bo.com.luminia.sflbilling.msreport.siat.text.invoices.templates;

public class TextExportacionServiciosTemplate {
    /**
     * Exportacion Servicios A4
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
            "\n" +
            "                                          FACTURA COMERCIAL DE EXPORTACIÓN DE SERVICIOS\n" +
            "                                              (COMMERCIAL SERVICES EXPORT INVOICE)\n" +
            "                                                 (Sin Derecho a Credito Fiscal)\n" +
            "\n" +
            "             Fecha  : <HDR_FECHA>                                       ;      NIT/CI/CEX :<HDR_NIT_CI_EX>; COMPLEMENTO:<HDR_CMPL>;\n" +
            "Nombre/Razón Social : <HDR_NOMBRE_RAZON_SOCIAL>                        ;   Tipo de Cambio :;<HDR_TIPO_CAMBIO>\n" +
            "(Buyer's Name)                                                             (Exchange Rate)\n" +
            "Dirección Comprador : <HDR_DIRECCION_COMPRADOR>                       ;Moneda Transacción : <HDR_MONEDA_TRANSACCION>\n" +
            "(Buyer's Address)                                                      (Commercial Transaction currency) \n" +
            "      Lugar Destino : <HDR_LUGAR_DESTINO>\n" +
            "(Destination Place)" +

            "\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  \n" +
            "COD.SERVICIO  | CANTIDAD | UNIDAD MEDIDA   | DESCRIPCIÓN                                     | PRECIO UNIT | DESCUEN |   SUBTOTAL |\n" +
            "(Serv. Code)  |(Quantity)|                 | (Description)                                   | (Unit Value)|(Discoun)|            |\n" +
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
            "\n" +
            "                                          FACTURA COMERCIAL DE EXPORTACIÓN DE SERVICIOS\n" +
            "                                              (COMMERCIAL SERVICES EXPORT INVOICE)\n" +
            "                                                 (Sin Derecho a Credito Fiscal)\n" +
            "\n" +
            "             Fecha  : <HDR_FECHA>                                       ;      NIT/CI/CEX :<HDR_NIT_CI_EX>; COMPLEMENTO:<HDR_CMPL>;\n" +
            "Nombre/Razón Social : <HDR_NOMBRE_RAZON_SOCIAL>                        ;   Tipo de Cambio :;<HDR_TIPO_CAMBIO>\n" +
            "(Buyer's Name)                                                             (Exchange Rate)\n" +
            "Dirección Comprador : <HDR_DIRECCION_COMPRADOR>                       ;Moneda Transacción : <HDR_MONEDA_TRANSACCION>\n" +
            "(Buyer's Address)                                                      (Commercial Transaction currency) \n" +
            "      Lugar Destino : <HDR_LUGAR_DESTINO>\n" +
            "(Destination Place)" +

            "\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  \n" +
            "COD.SERVICIO  | CANTIDAD | UNIDAD MEDIDA   | DESCRIPCIÓN                                     | PRECIO UNIT | DESCUEN |   SUBTOTAL |\n" +
            "(Serv. Code)  |(Quantity)|                 | (Description)                                   | (Unit Value)|(Discoun)|            |\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  ";

    public static final String DETAILS_A4 =
            "<codProducto> ;;<cantid>  <uniMedida>       ;<descripcion>                                     ;;<precUnit>  ;<descu>; <subTotal> |" ;

    public static final String FOOTER_A4 =
        "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - \n" +
            "                                                                                            DESCUENTO Bs.  ;<FTR_MONTO_DESCUENTO>\n" +
            //"                                                                           TOTAL GENERAL <FTR_MON_CAMBIO>;     ;<FTR_TOTA_CAMBIO>\n" +
            //"SON: <FTR_CAMBIO_LITERAL> <FTR_CAMBIO_DECIMAL>/100 <FTR_MON_CAMBIO_LITERAL>\n" +
            "                                                                                TOTAL GENERAL <FTR_MONE_TOTAL>;;<FTR_MONTO_TOTAL>\n" +
            "SON: <FTR_TOTAL_LITERAL> <FTR_TOTAL_DECIMAL>/100 <FTR_MONEDA_LITERAL>\n" +
            "\n" +
            "Información Adicional\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - \n" +
            "<FTR_INFORMACION_ADICIONAL>\n" +
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
        "FACTURA COMERCIAL DE EXPORTACIÓN;" +
            "DE SERVICIOS;" +
            "(COMMERCIAL SERVICES EXPORT ;" +
            "INVOICE);" +
            ";" +
            "SIN DERECHO A CREDITO FISCAL;" +
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
            "(Name Buyer);" +
            "   NIT/CI/EX:<HDR_NIT_CI_EX>;" +
            "COMPLEMENTO:<HDR_CMPL>;" +
            "      FECHA :<HDR_FECHA>;" +
            " (Date);" +
            "DIR.COMPRADOR:<HDR_DIRECCION_COMPRADOR>;" +
            "(Buyer's Address);" +
            "LUGAR DESTINO:<HDR_LUGAR_DESTINO>;" +
            "(Destination Place);" +
            "MNDA.TRANSACC:<HDR_MONEDA_TRANSACCION>;" +
            "(Currency);" +
            "TIPO.CAMBIO  :<HDR_TIPO_CAMBIO>;" +
            "(Exchange Rate);" +
            "- - - - - - - - - - - - - - - -;" +
            "DETALLE;";

    public static final String DETAILS_POS =
        "<codProducto>-<descripcion>;" +
            "<cant>;x<precUnit>;; <subTotal>;";

    public static final String FOOTER_POS =
        "- - - - - - - - - - - - - - - -;" +
            "DESC Bs   ;<FTR_MONTO_DESCUENTO>" +
            "TOTAL GENERAL <FTR_MON_CAMBIO>; " +
            "             ;<FTR_MONTO_TOTAL>;" +
            "SON: <FTR_CAMBIO_LITERAL> <FTR_CAMBIO_DECIMAL>/100 <FTR_MON_CAMBIO_LITERAL>;" +
            ";" +
            "SON: <FTR_TOTAL_LITERAL> <FTR_TOTAL_DECIMAL>/100 <FTR_MONEDA_LITERAL>;" +
            ";" +
            "- - - - - - - - - - - - - - - -;" +
            "INFORMACION ADICIONAL:;" +
            "<FTR_INFORMACION_ADICIONAL>;" +
            "- - - - - - - - - - - - - - - -;" +
            ";" +
            "<FTR_MENSAJE>;" +
            ";" +
            "<FTR_LEYENDA>;" +
            ";" +
            "<FTR_MODALIDAD_LEYENDA>;" +
            ";" +
            "<FTR_QR>;";

}
