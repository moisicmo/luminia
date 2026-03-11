package bo.com.luminia.sflbilling.msaccount.utils;

public class ResponseMessages {

    public static final String WARNING_LISTA_VACIA = "La lista se encuentra vacia.";
    public static final String WARNING_OPERACION_NO_EJECUTADA = "La operacion no ha sido ejecutada. Contacte al administrador.";
    public static final String SUCCESS_OPERACION_EXITOSA = "La operacion se realizo con exito.";
    public static final String SUCCESS_ELIMINACION_EXITOSA = "Registro eliminado con exito.";
    public static final String SUCCESS_ACTUALIZACION_EXITOSA = "Registro actualizado con exito.";
    public static final String ERROR_REGISTRO_NO_ENCONTRADO = "El registro no fue encontrado.";
    public static final String ERROR_REGISTRO_COMPANY_NO_ENCONTRADO = "El registro %d de company no fue encontrado.";
    public static final String ERROR_DEBE_ESPECIFICAR_ID = "Debe especificar un ID.";
    public static final String ERROR_EXISTE_BRANCH_ID = "Ya existe un branch <%d> en la empresa.";
    public static final String ERROR_EXISTE_SIGNATURE = "Ya existe un signature en la empresa.";
    public static final String ERROR_NO_EXISTE_PRODUCT_SERVICE = "No existe el product_service <%d> en el sistema.";
    public static final String ERROR_EXIST_APPROVED_PRODUCT_INVOICE = "El producto homologado <%s>, se usó para la emisión de al menos un documento fiscal.";
    public static final String ERROR_NO_EXISTE_MEASUREMENT_UNIT = "No existe el measurement <%d> en el sistema.";
    public static final String ERROR_REGISTRO_NO_GUARDADO = "No se pudo almacenar el registro.";
    public static final String ERROR_EMPRESA_NO_ENCONTRADA = "No se pudo encontrar la empresa <%d> o se encuentra inactiva.";
    public static final String ERROR_CAMPO_REQUERIDO = "El campo <%s> es un valor requerido.";
    public static final String ERROR_SUCURSAL_NO_ENCONTRADA = "No se pudo encontrar la sucursal <%d>.";
    public static final String ERROR_NO_EXISTE_POINT_SALE_TYPE = "No existe el point_sale_type <%d> en el sistema.";
    public static final String ERROR_CREDENCIALES_INVALIDOS = "Credenciales Inválidos.";
    public static final String ERROR_SOLO_PERMITIDO_DOS_COMPANY = "Solo esta permitido 2 empresas con el mismo nit.";
    public static final String ERROR_COMPANY_REGISTRADO_MODALIDAD = "Ya se encuentra registrado una empresa con la modalidad enviada.";
    public static final String ERROR_COMPANY_NOT_FOUND = "No se pudo encontrar la empresa <%s> o se encuentra inactiva.";
}
