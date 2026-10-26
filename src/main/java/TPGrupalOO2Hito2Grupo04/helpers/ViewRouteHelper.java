package TPGrupalOO2Hito2Grupo04.helpers;

public class ViewRouteHelper {
    public final static String INDEX = "home/index";

    // Redirects
    public final static String ROUTE_INDEX = "/index";
  
    //Unidad de venta
    public final static String UnidadVenta_INDEX = "unidadVenta/index";
    public final static String UnidadVenta_NEW_FOOD_TRUCK = "unidadVenta/new-food-truck";
    public final static String UnidadVenta_NEW_PUESTO = "unidadVenta/new-puesto-desarmable";  
    public final static String UnidadVenta_UPDATE = "unidadVenta/update";
    public final static String UnidadVenta_PARTIAL_VIEW = "unidadVenta/partialPersonView";
    // Liquidacion
    public final static String LIQUIDACION_INDEX = "liquidacion/index";

    //STAFF
    public final static String STAFF_INDEX = "staff/index";
    public final static String STAFF_NEW_COCINERO = "staff/new-cocinero";
    public final static String STAFF_NEW_CAJERO = "staff/new-cajero";
    public final static String STAFF_UPDATE = "staff/update-staff";
    public final static String STAFF_DROP= "staff/drop-staff";
    public final static String STAFF_PARTIAL_VIEW = "staff/partialPersonView";

    //USER
    public final static String USER_LOGIN = "user/login";
    public final static String USER_LOGOUT = "user/logout";

    //USUARIO
    public final static String USUARIO_NEW = "usuario/new-usuario";
  /**** Redirects ****/
    public final static String UnidadVenta_ROOT = "/unidadVenta";
    public final static String STAFF_ROOT = "/staff";

}
