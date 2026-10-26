package TPGrupalOO2Hito2Grupo04.services;

import java.util.List;
import java.util.Optional;

import TPGrupalOO2Hito2Grupo04.dtos.LiquidacionDTO;
import TPGrupalOO2Hito2Grupo04.entities.Staff;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;

public interface ILiquidacionService {

    List<Staff> getEmpleados();

    // Empleado asociado al usuario (mail) que inició sesión
    Optional<Staff> getEmpleadoPorMail(String mail);

    // Unidad de venta a cargo del responsable (mail) que inició sesión
    Optional<UnidadVenta> getUnidadDeResponsable(String mail);

    // Staff que trabaja en la unidad
    List<Staff> getEmpleadosDeUnidad(long idUnidadVenta);

    LiquidacionDTO liquidar(int idStaff, long idFestival);

    // Liquida al empleado en el festival de la unidad donde trabaja
    LiquidacionDTO liquidarEnSuFestival(int idStaff);

    byte[] generarReciboPdf(LiquidacionDTO liquidacion);
}
