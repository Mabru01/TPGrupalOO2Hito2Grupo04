package TPGrupalOO2Hito2Grupo04.repositories;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TPGrupalOO2Hito2Grupo04.entities.Costo;

@Repository("costoRepository")
public interface ICostoRepository extends JpaRepository<Costo, Serializable> {

    public abstract Optional<Costo> findById(long id);

    public abstract Optional<Costo> findByFestivalId(long festivalId);
}