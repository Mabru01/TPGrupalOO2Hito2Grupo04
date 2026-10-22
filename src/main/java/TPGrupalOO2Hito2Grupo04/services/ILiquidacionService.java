package TPGrupalOO2Hito2Grupo04.services;

import java.util.List;

import TPGrupalOO2Hito2Grupo04.dtos.LiquidacionDTO;
import TPGrupalOO2Hito2Grupo04.entities.Staff;

public interface ILiquidacionService {

    List<Staff> getEmpleados();

    LiquidacionDTO liquidar(int idStaff, long idFestival);
}
