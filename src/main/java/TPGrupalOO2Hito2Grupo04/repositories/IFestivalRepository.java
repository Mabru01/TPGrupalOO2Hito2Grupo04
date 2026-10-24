package TPGrupalOO2Hito2Grupo04.repositories;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TPGrupalOO2Hito2Grupo04.entities.Festival;

@Repository("festivalRepository")
public interface IFestivalRepository extends JpaRepository<Festival, Serializable> {

    public abstract Optional<Festival> findByIdFestival(long idFestival);

    public abstract Optional<Festival> findByNombre(String nombre);

    // Festival con sus unidades de venta cargadas
    @Query("SELECT f FROM Festival f LEFT JOIN FETCH f.unidadesVenta WHERE f.idFestival = (:id)")
    public abstract Optional<Festival> findByIdAndFetchUnidadesEagerly(@Param("id") long id);

    // Todos los festivales con sus unidades de venta cargadas, ordenados por fecha de inicio
    @Query("SELECT DISTINCT f FROM Festival f LEFT JOIN FETCH f.unidadesVenta ORDER BY f.fechaInicio")
    public abstract List<Festival> findAllAndFetchUnidadesEagerly();
    // Festival activo fecha de hoy entre inicio y fin
    @Query("SELECT f FROM Festival f WHERE :hoy BETWEEN f.fechaInicio AND f.fechaFin")
    Optional<Festival> findFestivalActivo(@Param("hoy") LocalDate hoy);
}