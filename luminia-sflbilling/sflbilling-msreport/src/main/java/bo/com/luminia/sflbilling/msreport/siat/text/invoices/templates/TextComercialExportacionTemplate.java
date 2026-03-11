package bo.com.luminia.sflbilling.msreport.siat.text.invoices.templates;

public class TextComercialExportacionTemplate {
    /**
     * Comercial Exportacion
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
            "                                                 FACTURA DE COMERCIAL DE EXPORTACIÓN                                               \n" +
            "                                                 (COMMERCIAL SERVICE EXPORT INVOICE)                                               \n" +
            "                                                    (Sin derecho a crédito fiscal)                                                 \n" +
            "\n" +
            "\n" +
            "Fecha               : <HDR_FECHA>                                       ;      NIT/CI/CEX :<HDR_NIT_CI_EX>;COMPLEMENTO:<HDR_CMPL>;\n" +
            "Nombre/Razon Social : <HDR_NOMBRE_RAZON_SOCIAL>                        ;   Tipo de Cambio :;<HDR_TIPO_CAMBIO>\n" +
            "(Buyer's Name)                                                             (Exchange Rate)\n" +
            "Direccion Comprador : <HDR_DIRECCION_COMPRADOR>                       ;Moneda Transaccion : <HDR_MONEDA_TRANSACCION>\n" +
            "(Buyer's Address)                                                      (Commercial Transaction currency) \n" +
            "Lugar Destino       : <HDR_LUGAR_DESTINO>\n" +
            "(Destination Place)" +
            "INCOTERM            : <HDR_INCOTERM>\n" +
            "\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  \n" +
            "COD.SERVICIO  | CANTIDAD | UNIDAD MEDIDA   | DESCRIPCIÓN                                     | PRECIO UNIT | DESCUENTO|    SUBTOTAL |\n" +
            "(Serv. Code)  |(Quantity)| (Measurement)   | (Description)                                   | (Unit Value)|(Discount)|             |\n" +
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
            "                                                 FACTURA DE COMERCIAL DE EXPORTACIÓN                                               \n" +
            "                                                 (COMMERCIAL SERVICE EXPORT INVOICE)                                               \n" +
            "                                                    (Sin derecho a crédito fiscal)                                                 \n" +
            "\n" +
            "\n" +
            "Fecha               : <HDR_FECHA>                                       ;      NIT/CI/CEX :<HDR_NIT_CI_EX>;COMPLEMENTO:<HDR_CMPL>;\n" +
            "Nombre/Razón Social : <HDR_NOMBRE_RAZON_SOCIAL>                        ;   Tipo de Cambio :;<HDR_TIPO_CAMBIO>\n" +
            "(Buyer's Name)        <HDR_NOMBRE_RAZON_SOCIAL>                        ;   (Exchange Rate)\n" +
            "Dirección Comprador : <HDR_DIRECCION_COMPRADOR>                       ;Moneda Transacción : <HDR_MONEDA_TRANSACCION>\n" +
            "(Buyer's Address)                                                      (Commercial Transaction currency) \n" +
            "Lugar Destino       : <HDR_LUGAR_DESTINO>\n" +
            "(Destination Place)" +
            "INCOTERM            : <HDR_INCOTERM>\n" +
            "\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  \n" +
            "COD.SERVICIO  | CANTIDAD | UNIDAD MEDIDA   | DESCRIPCIÓN                                      | PRECIO UNIT | DESCUEN  |   SUBTOTAL |\n" +
            "(Serv. Code)  |(Quantity)| (Measurement)   | (Description)                                    | (Unit Value)|(Discount)|             |\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  ";

    public static final String DETAILS_A4 =
            "<codProducto> ;;<cantid>  <uniMedida>       ;<descripcion>                                    ;;<precUnit>   ;<descu>;   <subTotal> |" ;

    public static final String FOOTER_A4_1 =
        "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - \n" +
            "                                                           TOTAL DETALLE (Total Detail) (<FTR_MONEDA_LITERAL>) : ;  <FTR_TOTAL_DETAIL>\n" +
            "                                                                                    INCOTERM Y ALCANCE TOTAL :  <FTR_INCOTERM>     ;\n" +
            "\n" +
            "DESGLOSE DE COSTOS Y GASTOS NACIONALES                                                                                              \n" +
            "(National Costs and Expenses Details)                                                                                               \n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  \n" ;
    public static final String DETAIL_FOOTER_A4 =
            "<jsonDetail>                                                                                ;      |     ;          <jsonMonto>    |\n";

    public static final String FOOTER_A4_2 =
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  \n" +
            "                                                                                    SUBTOTAL FOB   | ;       <FTR_SUBTOTAL_FOB>    |\n" +
            "\n" +
            "DESGLOSE DE COSTOS Y GASTOS INTERNACIONALES                                                                                         \n" +
            "(International Costs and Expenses Details)                                                                                          \n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  \n" ;

    public static final String FOOTER_A4_3 =
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - \n" +
            "                                                                                    SUBTOTAL CIF   |  ;      <FTR_SUBTOTAL_CIF>    |\n" +
            "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - \n" +
            "                                                                                   SUBTOTAL Bs. : ;              <FTR_SUBTOTAL>    \n" +
            "                                                                                  DESCUENTO Bs. : ;             <FTR_DESCUENTO>    \n" +
            "                                                                              TOTAL GENERAL Bs. : ;                 <FTR_TOTAL>    \n" +
            "\n"+
                "SON: <FTR_TOTAL_LITERAL> <FTR_TOTAL_DECIMAL>/100 <FTR_MONEDA_LITERAL>\n" +
            "\n" +
            "NÚMERO Y DESCRIPCIÓN DE PAQUETES Y BULTOS\n"+
            "(Number and description of boxes)\n"+
            "<FTR_BOXES_DESCRIPTION>                                                                                                           ;\n"+
                "<FTR_LEYENDA>\n" +
                "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - \n" +
                "\n"+
                "INFORMACIÓN ADICIONAL \n"+
                "(Additional Information)\n"+
                "<FTR_ADITIONAL_INFORMATION>                                                                                                       ;\n"+
                "-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  - \n" +
                "\n"+
                "<FTR_MENSAJE>\n" +
                "\n" +
                "<FTR_LEYENDA>\n" +
                "\n" +
                "<FTR_MODALIDAD_LEYENDA>\n"+
                "\n" +
                "<FTR_QR>\n" ;

    /**
     * Compra Venta POS
     */
    public static final String HEADER_POS =
            "FACTURA DE COMERCIAL;" +
            "DE EXPORTACIÓN;" +
            "(COMMERCIAL SERVICE;"+
            "EXPORT INVOICE);"+
            ";" +
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
            "NOMBRE/RAZOÓN:<HDR_NOMBRE_RAZON_SOCIAL>;" +
            "   NIT/CI/EX:<HDR_NIT_CI_EX>;" +
                "COMPLEMENTO:<HDR_CMPL>;"+
            "TIPO CAMBIO :<HDR_TIPO_CAMBIO>;" +
            "DIR. COMPRADOR:<HDR_DIRECCION_COMPRADOR>;" +
            "MON. TRANSACC :<HDR_MONEDA_TRANSACCION>;" +
            "LUGAR DESTINO : TRANSACC :<HDR_LUGAR_DESTINO>;" +
            "INCOTERM :<HDR_INCOTERM>;" +
            "- - - - - - - - - - - - - - - -;" ;
    public static final String DETAILS_POS =
        "<codProducto>-<descripcion>;" +
            "; <cantid>x<uniMedida>;" +
            "x<precUnit>;;"+
            ";                    <subTotal>;";

    public static final String FOOTER_POS_1 =
            "- - - - - - - - - - - - - - - -;" +
            "TOTAL DETALLE Bs. ;" +
            ";           <FTR_TOTAL_DETAIL>;"+
            "INCOTERM Y ALCANCE TOTAL;" +
            ";               <FTR_INCOTERM>;" +
            "DATOS DE LA DEVOLUCIÓN O RESCISIÓN;" +
            "- - - - - - - - - - - - - - - -;"+
            ";"+
            "DESGLOSE DE COSTOS Y GASTOS NACIONALES;"+
            "- - - - - - - - - - - - - - - -";

    public static final String DETAIL_FOOTER_POS =
        "<jsonDetail>; x ; <jsonMonto> ;";

    public static final String FOOTER_POS_2 =
        "SUBTOTAL FOB;<FTR_SUBTOTAL_FOB>;"+
         ";"+
            "- - - - - - - - - - - - - - - -;"+
         "DESGLOSE DE COSTOS Y GASTOS INTERNACIONALES;";


    public static final String FOOTER_POS_3 =
        "- - - - - - - - - - - - - - - -;" +
            "SUBTOTAL CIF;<FTR_SUBTOTAL_CIF>;"+
            ";"+
            "SUBTOTAL Bs. ;" +
            "               ;<FTR_SUBTOTAL>;"+
            "DESCUENTO Bs.;"+
            ";              <FTR_DESCUENTO>;"+
            "TOTAL GENERAL Bs.;"+
            ";                  <FTR_TOTAL>;"+

            ";" +
            "Son:;" +
            "<FTR_TOTAL_LITERAL> <FTR_TOTAL_DECIMAL>/100 <FTR_MONEDA_LITERAL>;" +
            ";" +
            ";" +
            "NÚMERO Y DESCRIPCIÓN DE PAQUETES Y BULTOS;"+
            "<FTR_BOXES_DESCRIPTION>;"+
            "- - - - - - - - - - - - - - - -;" +
            "INFORMACIÓN ADICIONAL;"+
            "<FTR_ADITIONAL_INFORMATION>;"+
            "- - - - - - - - - - - - - - - -;" +
            ";" +
            "<FTR_MENSAJE>;"+
            ";" +
            "<FTR_LEYENDA>;"+
            ";" +
            "<FTR_MODALIDAD_LEYENDA>;"+
            ";"+
            "<FTR_QR>;" ;

}
