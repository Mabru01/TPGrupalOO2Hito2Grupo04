package TPGrupalOO2Hito2Grupo04.services;
import TPGrupalOO2Hito2Grupo04.dtos.StaffDTO;
import TPGrupalOO2Hito2Grupo04.entities.Staff;

import java.util.List;
import java.util.Optional;


public interface IStaffService {

    List<Staff> getAll();

    Optional<Staff> findById(long id);

    Staff findByDni(long dni);

    Staff insertOrUpdate(Staff staff);

    boolean remove(long id);

    Staff registrarStaff(StaffDTO staffDTO);

}