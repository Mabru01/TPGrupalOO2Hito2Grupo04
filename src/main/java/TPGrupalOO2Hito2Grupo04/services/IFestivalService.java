package TPGrupalOO2Hito2Grupo04.services;

import java.util.List;
import java.util.Optional;

import TPGrupalOO2Hito2Grupo04.entities.Festival;

public interface IFestivalService {

    List<Festival> getAll();

    Optional<Festival> findById(long id);

    Festival findByNombre(String nombre);

    Festival findByIdWithUnidades(long id);

    Festival insertOrUpdate(Festival festival);

    boolean remove(long id);
}
