package TPGrupalOO2Hito2Grupo04.repositories;

import TPGrupalOO2Hito2Grupo04.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.io.Serializable;

@Repository("userRepository")
public interface IUsuarioRepository extends JpaRepository<Usuario, Serializable> {

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.mail = (:mail)")
    public abstract Usuario findByMailAndFetchRolEagerly(@Param("mail") String mail);
    Optional<Usuario> findByMail(String mail);

}
