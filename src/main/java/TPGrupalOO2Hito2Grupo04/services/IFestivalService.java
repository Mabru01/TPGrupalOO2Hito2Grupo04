package TPGrupalOO2Hito2Grupo04.services;

import java.util.List;
import java.util.Optional;

import TPGrupalOO2Hito2Grupo04.dtos.FestivalDTO;
import TPGrupalOO2Hito2Grupo04.entities.Festival;

public interface IFestivalService {

    List<Festival> getAll();

    List<Festival> getAllWithUnidades();

    Optional<Festival> findById(long id);

    Festival findByNombre(String nombre);

    Festival findByIdWithUnidades(long id);

    Festival insertOrUpdate(Festival festival);

    // Formulario de alta con los costos del último festival como sugerencia
    FestivalDTO nuevoConCostosSugeridos();

    // Alta de un festival con sus costos
    Festival crearFestivalConCostos(FestivalDTO festivalDTO);

    // Formulario de modificación con los datos y costos actuales del festival
    FestivalDTO datosParaModificar(long idFestival);

    // Modificación de un festival que todavía no terminó
    Festival modificarFestival(long idFestival, FestivalDTO festivalDTO);

    boolean remove(long id);
}
