package TPGrupalOO2Hito2Grupo04.repositories;

import TPGrupalOO2Hito2Grupo04.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository("userRepository")
public interface IUsuarioRepository extends JpaRepository<Usuario, Serializable> {

    @Query("SELECT u FROM User u JOIN FETCH u.userRoles WHERE u.username = (:username)")
    public abstract User findByUsernameAndFetchUserRolesEagerly(@Param("username") String username);
}
