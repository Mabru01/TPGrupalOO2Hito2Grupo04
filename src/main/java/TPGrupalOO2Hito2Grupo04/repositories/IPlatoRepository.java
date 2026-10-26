package TPGrupalOO2Hito2Grupo04.repositories;

import TPGrupalOO2Hito2Grupo04.entities.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface IPlatoRepository extends JpaRepository<Plato, Long> {

    // Metodos especificos
    Optional<Plato> findByNombre(String nombre);

    List<Plato> findAllByOrderByUnidadVentaNombreComercialAsc();

}
