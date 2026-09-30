package TPGrupalOO2Hito2Grupo04.repositories;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TPGrupalOO2Hito2Grupo04.entities.Festival;

@Repository("festivalRepository")
public interface IFestivalRepository extends JpaRepository<Festival, Serializable> {

    public abstract Optional<Festival> findById(long id);

    public abstract Optional<Festival> findByNombre(String nombre);

    // Festival con sus unidades de venta cargadas
    @Query("SELECT f FROM Festival f LEFT JOIN FETCH f.unidadesVenta WHERE f.id = (:id)")
    public abstract Optional<Festival> findByIdAndFetchUnidadesEagerly(@Param("id") long id);
}