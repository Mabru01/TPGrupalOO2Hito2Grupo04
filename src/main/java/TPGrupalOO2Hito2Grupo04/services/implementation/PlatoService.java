package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import TPGrupalOO2Hito2Grupo04.entities.Plato;
import TPGrupalOO2Hito2Grupo04.entities.Staff;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.dtos.PlatoDTO;
import TPGrupalOO2Hito2Grupo04.repositories.IPlatoRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IStaffRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IUnidadVentaRepository;
import TPGrupalOO2Hito2Grupo04.services.IPlatoService;

@Service("platoService")
public class PlatoService implements IPlatoService {

    private static final Logger logger =
            LoggerFactory.getLogger(PlatoService.class);

    private final IPlatoRepository platoRepository;
    private final IUnidadVentaRepository unidadVentaRepository;
    private final IStaffRepository staffRepository;
    private final ModelMapper modelMapper;

    public PlatoService(IPlatoRepository platoRepository,
                        IUnidadVentaRepository unidadVentaRepository,
                        IStaffRepository staffRepository,
                        ModelMapper modelMapper) {
        this.platoRepository = platoRepository;
        this.unidadVentaRepository = unidadVentaRepository;
        this.staffRepository = staffRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Plato> traerTodos() {
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
            Plato plato = platoRepository.findById(id).orElse(null);

            if (plato != null) {
                plato.setActivo(false);
                platoRepository.save(plato);

                logger.info("Plato con id {} dado de baja con éxito.", id);
                return true;
            } else {
                logger.warn("No se encontró el Plato con id {}.", id);
                return false;
            }
        } catch (DataAccessException e) {
            logger.error("Error en la base de datos al dar de baja el Plato con id {}", id, e);
            return false;
        }
    }

    @Override
    public Plato registrarPlato(PlatoDTO dto) {
        UnidadVenta unidadVenta;

        // Si el DTO trae un idUnidadVenta (lo usa el Administrador desde el selector)
        if (dto.getIdUnidadVenta() != null) {
            unidadVenta = unidadVentaRepository.findById(dto.getIdUnidadVenta())
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: Unidad de venta no encontrada"));
        } else {
            String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();

            Staff staff = staffRepository.findByUsuarioMail(emailUsuario)
                    .orElseThrow(() -> new RuntimeException("ERROR: El usuario logueado no tiene un staff/unidad asociada"));

            unidadVenta = staff.getUnidadVenta();
            if (unidadVenta == null) {
                throw new IllegalArgumentException("ERROR: El responsable logueado no tiene una unidad de venta asignada.");
            }
        }

        Optional<Plato> mismoNombre = platoRepository.findByNombre(dto.getNombre());
        Plato plato;

        if (mismoNombre.isPresent()) {
            Plato platoExistente = mismoNombre.get();

            // Si el nombre pertenece a OTRO registro distinto al que estamos editando
            if (dto.getIdPlato() == null || !platoExistente.getIdPlato().equals(dto.getIdPlato())) {

                // Si el plato existente está inactivo, lo reactivamos en lugar de tirar error
                if (Boolean.FALSE.equals(platoExistente.getActivo())) {
                    plato = platoExistente;
                    plato.setActivo(true); // ¡Lo reactivamos!
                } else {
                    // Si ya existe y está activo, prohibido duplicar
                    throw new IllegalArgumentException("Ya existe un plato activo registrado con ese nombre.");
                }
            } else {
                // Es el mismo plato que estamos actualizando
                plato = platoExistente;
            }
        } else {
            // Si no existe ninguno con ese nombre, creamos uno nuevo o usamos el ID si viene en el DTO
            if (dto.getIdPlato() != null) {
                plato = platoRepository.findById(dto.getIdPlato()).orElse(new Plato());
            } else {
                plato = new Plato();
                plato.setActivo(true); // Aseguramos que nace activo
            }
        }

        // Actualizamos los campos
        plato.setNombre(dto.getNombre());
        plato.setPrecio(dto.getPrecio());
        plato.setCosto(dto.getCosto());
        plato.setUnidadVenta(unidadVenta);

        return platoRepository.save(plato);
    }
}