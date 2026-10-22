package TPGrupalOO2Hito2Grupo04.repositories;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TPGrupalOO2Hito2Grupo04.entities.Costo;

@Repository("costoRepository")
public interface ICostoRepository extends JpaRepository<Costo, Serializable> {

    public abstract Optional<Costo> findByIdCosto(long idCosto);

    public abstract Optional<Costo> findByFestivalIdFestival(long idFestival);
}