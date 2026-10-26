package TPGrupalOO2Hito2Grupo04.services;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.entities.Usuario;
import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.dtos.UnidadVentaDTO;
import TPGrupalOO2Hito2Grupo04.repositories.IUnidadVentaRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IFoodTruckRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IPuestoDesarmableRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface IUnidadVentaService {

    List<UnidadVenta> getAll();

    Optional<UnidadVenta> findById(long id);

    UnidadVenta findByCodigoUnico(String codigoUnico);

    UnidadVenta insertOrUpdate(UnidadVenta unidadVenta);

    boolean remove(long id);

    UnidadVenta registrarUnidadVentaConResponsable(UnidadVentaDTO dto);

    String generarCodigoUnico();

    double calcularCanon(Long idUnidadVenta);
}
