package TPGrupalOO2Hito2Grupo04.repositories;

import TPGrupalOO2Hito2Grupo04.entities.PuestoDesarmable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("puestoDesarmableRepository")
public interface IPuestoDesarmableRepository extends JpaRepository<PuestoDesarmable, Long> {
    // Si necesitás algún método específico para PuestoDesarmable se agrega acá


}