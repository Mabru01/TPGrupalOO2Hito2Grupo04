package TPGrupalOO2Hito2Grupo04.repositories;

import TPGrupalOO2Hito2Grupo04.entities.Pedido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT p, SUM(d.cantidad) FROM Pedido pe " +
            "JOIN pe.items d " +
            "JOIN d.plato p " +
            "WHERE pe.unidadVenta.idUnidadVenta = :idUnidadVenta " +
            "AND pe.unidadVenta.festival.idFestival = :idFestival " +
            "GROUP BY p " +
            "ORDER BY SUM(d.cantidad) DESC")
    List<Object[]> encontrarPlatoEstrella(
            @Param("idUnidadVenta") Long idUnidadVenta,
            @Param("idFestival") Long idFestival,
            Pageable pageable
    );
}
