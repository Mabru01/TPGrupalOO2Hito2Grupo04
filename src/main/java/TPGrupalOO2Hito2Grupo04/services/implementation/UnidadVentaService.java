package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.util.List;
import java.util.Optional;

import TPGrupalOO2Hito2Grupo04.repositories.IFestivalRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IUsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import TPGrupalOO2Hito2Grupo04.entities.*;
import TPGrupalOO2Hito2Grupo04.dtos.*;
import TPGrupalOO2Hito2Grupo04.repositories.*;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;

@Service("unidadVentaService")
public class UnidadVentaService implements IUnidadVentaService {
    private static final Logger logger = LoggerFactory.getLogger(UnidadVentaService.class);

    private final IUnidadVentaRepository unidadVentaRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IFestivalRepository festivalRepository;
    private final IRolRepository rolRepository;
    private final ModelMapper modelMapper;

    public UnidadVentaService(IUnidadVentaRepository unidadVentaRepository, IUsuarioRepository usuarioRepository, IFestivalRepository festivalRepository, IRolRepository rolRepository, ModelMapper modelMapper) {
        this.unidadVentaRepository = unidadVentaRepository;
        this.usuarioRepository = usuarioRepository;
        this.festivalRepository = festivalRepository;
        this.rolRepository = rolRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<UnidadVenta> getAll() {
        return unidadVentaRepository.findAll();
    }

    @Override
    public Optional<UnidadVenta> findById(long id) {
        return unidadVentaRepository.findById(id);
    }

    @Override
    public UnidadVenta findByCodigoUnico(String codigoUnico) {

        return unidadVentaRepository.findByCodigoUnico(codigoUnico).orElseThrow(
                () -> new IllegalArgumentException("ERROR: Unidad de venta not found with cod: " + codigoUnico)
        );
    }

    @Override
    public UnidadVenta insertOrUpdate(UnidadVenta unidadVenta) {
        return unidadVentaRepository.save(unidadVenta);
    }

    @Override
    public boolean remove(long id) {
        try {
            unidadVentaRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException | DataIntegrityViolationException e) {
            logger.warn("Could not remove UnidadVenta with id {}", id, e);
            return false;
        } catch (DataAccessException e) {
            logger.error("Database error while removing UnidadVenta with id {}", id, e);
            return false;
        }
    }
    @Override
    public UnidadVenta registrarUnidadVentaConResponsable(UnidadVentaDTO dto){

        // 1. validacion de email
        if (usuarioRepository.findByMail(dto.getEmailResponsable()).isPresent()) {
            throw new IllegalArgumentException("El email del responsable ya se encuentra registrado.");
        }

        // 2. Buscar festival
        Festival festival = festivalRepository.findById(dto.getIdFestival())
                .orElseThrow(() -> new IllegalArgumentException("Festival no encontrado con ID: " + dto.getIdFestival()));

        // 3. Crear y guardar el nuevo Usuario Responsable
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setMail(dto.getEmailResponsable());
        nuevoUsuario.setPassword(dto.getPasswordResponsable());
        nuevoUsuario.setActivo(true);

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        Rol nuevoRol = new Rol();
        nuevoRol.setRol("RESPONSABLE");
        nuevoRol.setUsuario(usuarioGuardado);

        rolRepository.save(nuevoRol);
        // 4. Instanciar y mapear la entidad hija (FoodTruck o PuestoDesarmable)
        UnidadVenta unidadVenta;

        if (dto instanceof FoodTruckDTO) {
            FoodTruckDTO ftDto = (FoodTruckDTO) dto;
            FoodTruck ft = modelMapper.map(ftDto, FoodTruck.class);
            unidadVenta = ft;
        } else if (dto instanceof PuestoDesarmableDTO) {
            PuestoDesarmableDTO pdDto = (PuestoDesarmableDTO) dto;
            PuestoDesarmable pd = modelMapper.map(pdDto, PuestoDesarmable.class);
            unidadVenta = pd;
        } else {
            throw new IllegalArgumentException("Tipo de unidad de venta desconocido.");
        }

        unidadVenta.setResponsable(usuarioGuardado);
        unidadVenta.setFestival(festival);

        return unidadVentaRepository.save(unidadVenta);

    };


}