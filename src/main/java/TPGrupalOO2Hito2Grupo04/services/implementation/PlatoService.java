package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import TPGrupalOO2Hito2Grupo04.entities.Plato;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.dtos.PlatoDTO;
import TPGrupalOO2Hito2Grupo04.repositories.IPlatoRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IUnidadVentaRepository;
import TPGrupalOO2Hito2Grupo04.services.IPlatoService;

@Service("platoService")
public class PlatoService implements IPlatoService {

    private static final Logger logger =
            LoggerFactory.getLogger(PlatoService.class);

    private final IPlatoRepository platoRepository;
    private final IUnidadVentaRepository unidadVentaRepository;
    private final ModelMapper modelMapper;

    public PlatoService(IPlatoRepository platoRepository, IUnidadVentaRepository unidadVentaRepository, ModelMapper modelMapper) {
        this.platoRepository = platoRepository;
        this.unidadVentaRepository = unidadVentaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Plato> traerTodos() {
        List<Plato> platos = platoRepository.findAll();
        return platoRepository.findAllByOrderByUnidadVentaNombreComercialAsc();
    }

    @Override
    public Optional<Plato> traerPorId(Long id) {
        return platoRepository.findById(id);
    }

    @Override
    public Plato insertarOActualizar(Plato plato) {
        return platoRepository.save(plato);
    }

    @Override
    public boolean eliminar(Long id) {
        try {
            platoRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException |
                 DataIntegrityViolationException e) {
            logger.warn("No se pudo eliminar el Plato con id {}", id, e);
            return false;
        } catch (DataAccessException e) {
            logger.error(
                    "Error de base de datos al eliminar el Plato con id {}", id, e);
            return false;
        }
    }

    @Override
    public Plato registrarPlato(PlatoDTO dto) {
        // El nombre no puede repetirse con OTRO plato (al modificar, el plato se encuentra a sí mismo)
        Optional<Plato> mismoNombre = platoRepository.findByNombre(dto.getNombre());
        if (mismoNombre.isPresent() && !mismoNombre.get().getIdPlato().equals(dto.getIdPlato())) {
            throw new IllegalArgumentException("Ya existe un plato registrado con ese nombre.");
        }

        UnidadVenta unidadVenta = unidadVentaRepository.findById(dto.getIdUnidadVenta())
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Unidad de venta no encontrada"));

        Plato plato = new Plato();

        // Si el DTO trae un ID (en caso de modificar), se lo asignamos para que Spring actualice
        if (dto.getIdPlato() != null) {
            plato.setIdPlato(dto.getIdPlato());
        }

        plato.setNombre(dto.getNombre());
        plato.setPrecio(dto.getPrecio());
        plato.setCosto(dto.getCosto());
        plato.setUnidadVenta(unidadVenta);

        return platoRepository.save(plato);
    }
}
