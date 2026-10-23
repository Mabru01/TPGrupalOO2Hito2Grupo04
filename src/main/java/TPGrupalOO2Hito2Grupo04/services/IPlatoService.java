package TPGrupalOO2Hito2Grupo04.services;

import TPGrupalOO2Hito2Grupo04.entities.Plato;

import java.util.List;
import java.util.Optional;

public interface IPlatoService {

    List<Plato> getAll();

    Optional<Plato> findById(long id);

    Plato insertOrUpdate(Plato plato);

    boolean remove(long id);
}
