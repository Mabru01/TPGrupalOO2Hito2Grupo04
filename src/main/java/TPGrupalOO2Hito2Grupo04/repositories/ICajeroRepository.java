package TPGrupalOO2Hito2Grupo04.repositories;

import TPGrupalOO2Hito2Grupo04.entities.Cajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("cajeroRepository")
public interface ICajeroRepository extends JpaRepository<Cajero, Integer> {

    // Buscar cajero por DNI
    Optional<Cajero> findByDni(long dni);

    // Cajero asociado al usuario que inició sesión
    Optional<Cajero> findByUsuarioMail(String mail);

    // Cajero asociado al usuario con unidadVenta cargada eagerly
    @Query("SELECT c FROM Cajero c JOIN FETCH c.unidadVenta uv WHERE c.usuario.mail = :mail")
    Optional<Cajero> findByUsuarioMailWithUnidadVenta(String mail);
}
