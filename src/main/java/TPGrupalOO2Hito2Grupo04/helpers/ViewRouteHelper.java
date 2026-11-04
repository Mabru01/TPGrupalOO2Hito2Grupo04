package TPGrupalOO2Hito2Grupo04.helpers;

public class ViewRouteHelper {
    public final static String INDEX = "home/index";

    // Redirects
    public final static String ROUTE_INDEX = "/index";
  
    //Unidad de venta
    public final static String UnidadVenta_INDEX = "unidadVenta/index";
    public final static String UnidadVenta_NEW_FOOD_TRUCK = "unidadVenta/new-food-truck";
    public final static String UnidadVenta_NEW_PUESTO = "unidadVenta/new-puesto-desarmable";
    public final static String UnidadVenta_SET_PUESTO = "unidadventa/modificar-puesto-desarmable";
    public final static String UnidadVenta_SET_FOOD_TRUCK = "unidadventa/modificar-food-truck";
    public final static String UnidadVenta_MINE = "/unidadVenta/mi-unidad";
    public static final String UnidadVenta_RANKING_RECAUDACION = "unidadVenta/ranking-recaudacion"; 


    // Reportes
    public final static String REPORTE_RECAUDACION = "reporte/recaudacion";
    public final static String reporte_MAYOR_CANON = "reporte/mayorCanon";
    public final static String reporte_PDF_MAYOR_CANON = "reporte-mayores-canon-";

    // Liquidacion
    public final static String LIQUIDACION_INDEX = "liquidacion/index";

    // Cierre de Caja
    public final static String CIERRE_CAJA_INDEX = "cierreCaja/index";
    public final static String CIERRE_CAJA_NUEVO = "cierreCaja/nuevo";
    public final static String CIERRE_CAJA_RESULTADO = "cierreCaja/resultado";

    //STAFF
    public final static String STAFF_INDEX = "staff/index";
    public final static String STAFF_NEW_COCINERO = "staff/new-cocinero";
    public final static String STAFF_NEW_CAJERO = "staff/new-cajero";
    public final static String STAFF_UPDATE = "staff/update-staff";
    public final static String STAFF_DROP= "staff/drop-staff";
    public final static String STAFF_REACTIVATE = "staff/reactivate-staff";
    public final static String STAFF_PARTIAL_VIEW = "staff/partialPersonView";
    public final static String STAFF_CAMBIAR_CONTRASENA = "staff/cambiar-contrasenia";
    public final static String STAFF_PERFIL = "staff/perfil";
    public final static String STAFF_CALENDARIO = "staff/calendario";

    //USER
    public final static String USER_LOGIN = "user/login";
    public final static String USER_LOGOUT = "user/logout";

    //USUARIO
    public final static String USUARIO_NEW = "usuario/new-usuario";
  /**** Redirects ****/
    public final static String UnidadVenta_ROOT = "/unidadVenta";
    public final static String STAFF_ROOT = "/staff";

}
