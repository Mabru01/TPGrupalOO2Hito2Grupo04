package TPGrupalOO2Hito2Grupo04.repositories;

import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("unidadVentaRepository")
public interface IUnidadVentaRepository extends JpaRepository<UnidadVenta, Long> {

    // Método para buscar por código único
    public abstract Optional<UnidadVenta> findByCodigoUnico(String codigoUnico);
    public abstract boolean existsByCodigoUnico(String codigo);

    // Unidad de ventas a cargo del responsable que inició sesión
    List<UnidadVenta> findByActivaTrue();
    Optional<UnidadVenta> findByCodigoUnicoAndActivaTrue(String codigoUnico);
    Optional<UnidadVenta> findByResponsable_Mail(String mail);
    List<UnidadVenta> findByFestivalIdFestivalAndActivaTrue(long idFestival);

    @Query("SELECT DISTINCT u FROM UnidadVenta u LEFT JOIN FETCH u.pedidos p LEFT JOIN FETCH p.items i LEFT JOIN FETCH i.plato WHERE u.festival.id = :idFestival and u.activa = true")
    List<UnidadVenta> findByFestivalWithPedidosAndItems(@Param("idFestival") Long idFestival);
}