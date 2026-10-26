package TPGrupalOO2Hito2Grupo04.repositories;

import TPGrupalOO2Hito2Grupo04.entities.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository("staffRepository")
public interface IStaffRepository extends JpaRepository<Staff, Serializable> {

    // Metodo para buscar por dni
    public abstract Optional<Staff> findByDni(long dni);

    // Empleado asociado al usuario que inició sesión
    public abstract Optional<Staff> findByUsuarioMail(String mail);

    // Staff que trabaja en una unidad de venta
    public abstract List<Staff> findByUnidadVentaIdUnidadVenta(long idUnidadVenta);
}
