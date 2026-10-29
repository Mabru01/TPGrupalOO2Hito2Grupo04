package TPGrupalOO2Hito2Grupo04.services;

import TPGrupalOO2Hito2Grupo04.dtos.PlatoDTO;
import TPGrupalOO2Hito2Grupo04.entities.Plato;

import java.util.List;
import java.util.Optional;

public interface IPlatoService {

    List<Plato> traerTodos();

    Optional<Plato> traerPorId(Long id);

    Plato insertarOActualizar(Plato plato);

    boolean eliminar(Long id);

    Plato registrarPlato(PlatoDTO dto);
}
