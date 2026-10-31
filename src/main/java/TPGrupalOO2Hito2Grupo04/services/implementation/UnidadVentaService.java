package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.security.crypto.password.PasswordEncoder;

@Service("unidadVentaService")
public class UnidadVentaService implements IUnidadVentaService {
    private static final Logger logger = LoggerFactory.getLogger(UnidadVentaService.class);

    private final IUnidadVentaRepository unidadVentaRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IFestivalRepository festivalRepository;
    private final IRolRepository rolRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UnidadVentaService(IUnidadVentaRepository unidadVentaRepository, IUsuarioRepository usuarioRepository, IFestivalRepository festivalRepository, IRolRepository rolRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.unidadVentaRepository = unidadVentaRepository;
        this.usuarioRepository = usuarioRepository;
        this.festivalRepository = festivalRepository;
        this.rolRepository = rolRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
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
    public UnidadVenta registrarUnidadVentaConResponsable(UnidadVentaDTO dto) {

        // 1. Validar que el email no exista
        if (usuarioRepository.findByMail(dto.getEmailResponsable()).isPresent()) {
            throw new IllegalArgumentException("El email del responsable ya se encuentra registrado.");
        }

        // 2. Buscar el único festival activo
        LocalDate hoy = LocalDate.now();
        Festival festival = festivalRepository.findFestivalActivo(hoy)
                .orElseThrow(() -> new RuntimeException("No hay ningún festival activo."));

        // 3. Obtener (o crear) el rol RESPONSABLE
        Rol rolResponsable = rolRepository.findByRol("RESPONSABLE")
                .orElseGet(() -> {
                    Rol r = new Rol();
                    r.setRol("RESPONSABLE");
                    return rolRepository.save(r);
                });

        // 4. Crear el usuario con ese rol y la contraseña hasheada
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setMail(dto.getEmailResponsable());
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getPasswordResponsable()));
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setRol(rolResponsable);

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // 5. Instanciar y mapear la entidad hija
        UnidadVenta unidadVenta;

        if (dto instanceof FoodTruckDTO ftDto) {
            unidadVenta = modelMapper.map(ftDto, FoodTruck.class);
        } else if (dto instanceof PuestoDesarmableDTO pdDto) {
            unidadVenta = modelMapper.map(pdDto, PuestoDesarmable.class);
        } else {
            throw new IllegalArgumentException("Tipo de unidad de venta desconocido.");
        }

        unidadVenta.setResponsable(usuarioGuardado);
        unidadVenta.setFestival(festival);
        unidadVenta.setActiva(true);
        unidadVenta.setCodigoUnico(generarCodigoUnico());
        return unidadVentaRepository.save(unidadVenta);
    }

    public String generarCodigoUnico() {
        String codigo;
        boolean existe;
        do {
            // Genera un código aleatorio de 8 caracteres (por ejemplo, usando UUID o Random)
            codigo = UUID.randomUUID().toString().substring(0, 5).toUpperCase();

            // Validar si ya existe

            existe = unidadVentaRepository.existsByCodigoUnico(codigo);
        } while (existe); // Si ya existe, repite el ciclo; si es único, sale.

        return codigo;
    }

    @Override
    public double calcularCanon(Long idUnidadVenta) {
        UnidadVenta unidad = unidadVentaRepository.findById(idUnidadVenta)
                .orElseThrow(() -> new IllegalArgumentException("Sin unidad de venta asignada"));

        return unidad.calcularCanon();
    }

    @Override
    public Optional<UnidadVenta> findByResponsable(String mail) {
        return unidadVentaRepository.findByResponsable_Mail(mail);
    }

    @Override
    public void darDeBaja(Long id) {
        UnidadVenta unidad = unidadVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidad de venta no encontrada"));

        unidad.setActiva(false); // Cambiamos el estado
        unidadVentaRepository.save(unidad); // Guardamos la actualización
    }

    @Override
    public void reactivar(Long id) {
        UnidadVenta unidad = unidadVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidad de venta no encontrada"));

        unidad.setActiva(true); // Cambiamos el estado
        unidadVentaRepository.save(unidad); // Guardamos la actualización
    }


    @Override
    public void modificarFoodTruck(Long id, FoodTruckDTO dto) {
        FoodTruck ft = (FoodTruck) unidadVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FoodTruck no encontrado"));

        // Actualizás los campos comunes y los propios de FoodTruck

        ft.setNombreComercial(dto.getNombreComercial());
        ft.setSuperficie(dto.getSuperficie());
        ft.setPatente(dto.getPatente());
        ft.setUsaLuz(dto.isUsaLuz());

        unidadVentaRepository.save(ft);
    }

    @Override
    public void modificarPuestoDesarmable(Long id, PuestoDesarmableDTO dto) {
        PuestoDesarmable pd = (PuestoDesarmable) unidadVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Puesto Desarmable no encontrado"));

        pd.setNombreComercial(dto.getNombreComercial());
        pd.setSuperficie(dto.getSuperficie());
        pd.setCantidad(dto.getCantidad());
        pd.setTiempoMontaje(dto.getTiempoMontaje());

        unidadVentaRepository.save(pd);
    }



}