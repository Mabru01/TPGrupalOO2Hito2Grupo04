package TPGrupalOO2Hito2Grupo04.repositories;

import TPGrupalOO2Hito2Grupo04.entities.Cocinero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICocineroRepository extends JpaRepository<Cocinero, Integer> {

    @Query("SELECT c FROM Cocinero c JOIN FETCH c.usuario u JOIN FETCH c.unidadVenta WHERE u.mail = :mail")
    Optional<Cocinero> findByUsuarioMailWithUnidadVenta(@Param("mail") String mail);
}