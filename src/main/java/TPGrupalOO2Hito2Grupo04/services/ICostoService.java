package TPGrupalOO2Hito2Grupo04.services;

import java.util.List;
import java.util.Optional;

import TPGrupalOO2Hito2Grupo04.entities.Costo;

public interface ICostoService {

    List<Costo> getAll();

    Optional<Costo> findById(long id);

    Costo findByFestival(long festivalId);

    Costo insertOrUpdate(Costo costo);

    boolean remove(long id);
}