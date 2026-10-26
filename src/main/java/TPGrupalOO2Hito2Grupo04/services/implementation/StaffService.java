package TPGrupalOO2Hito2Grupo04.services.implementation;

import TPGrupalOO2Hito2Grupo04.dtos.*;
import TPGrupalOO2Hito2Grupo04.entities.*;
import TPGrupalOO2Hito2Grupo04.repositories.IRolRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IStaffRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IUnidadVentaRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IUsuarioRepository;
import TPGrupalOO2Hito2Grupo04.services.IStaffService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service("staffService")
public class StaffService implements IStaffService {
    private static final Logger logger = LoggerFactory.getLogger(StaffService.class);

    private final IStaffRepository staffRepository;
    private final IUnidadVentaRepository unidadVentaRepository;
    private final IRolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final IUsuarioRepository usuarioRepository;

    public StaffService(IStaffRepository staffRepository, IUnidadVentaRepository unidadVentaRepository,
                        ModelMapper modelMapper, IRolRepository rolRepository,
                        PasswordEncoder passwordEncoder, IUsuarioRepository usuarioRepository) {
        this.staffRepository = staffRepository;
        this.unidadVentaRepository = unidadVentaRepository;
        this.modelMapper = modelMapper;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Staff> getAll() {
        return staffRepository.findAll();
    }

    @Override
    public Optional<Staff> findById(long id) {
        return staffRepository.findById(id);
    }

    @Override
    public Staff findByDni(long dni) {

        return staffRepository.findByDni(dni).orElseThrow(
                () -> new IllegalArgumentException("ERROR: Staff not found with cod: " + dni)
        );
    }

    @Override
    public Staff insertOrUpdate(Staff staff) {
        return staffRepository.save(staff);
    }

    @Override
    public boolean remove(long id) {
        try {
            staffRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException | DataIntegrityViolationException e) {
            logger.warn("Could not remove Staff with id {}", id, e);
            return false;
        } catch (DataAccessException e) {
            logger.error("Database error while removing Staff with id {}", id, e);
            return false;
        }
    }

    public Staff registrarStaff(StaffDTO staffDTO) {
        // Verifico si el staff no existe antes
        if (staffRepository.findByDni(staffDTO.getDni()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un empleado registrado con ese dni.");
        }

        // 3. Obtener (o crear) el rol EMPLEADO
        Rol rolEmpleado= rolRepository.findByRol("EMPLEADO")
                .orElseGet(() -> {
                    Rol r = new Rol();
                    r.setRol("EMPLEADO");
                    return rolRepository.save(r);
                });

        // 4. Crear el usuario con ese rol y la contraseña hasheada
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setMail(staffDTO.getEmailStaff());
        nuevoUsuario.setPassword(passwordEncoder.encode(staffDTO.getPasswordStaff()));
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setRol(rolEmpleado);

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);


        // Se busca la Unidad de Venta en la base de datos
        UnidadVenta unidadVenta = unidadVentaRepository.findById(staffDTO.getIdUnidadVenta())
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Unidad de venta no encontrada"));


        // Creo el staff y lo registro
        Staff staff;

        if (staffDTO instanceof CajeroDTO caDTO) {
            System.out.println("Mapeando a Cajero");
            staff = modelMapper.map(caDTO, Cajero.class);
        } else if (staffDTO instanceof CocineroDTO coDTO) {
            System.out.println("Mapeando a Cocinero");
            staff = modelMapper.map(coDTO, Cocinero.class);
        } else {
            throw new IllegalArgumentException("Tipo de empleado desconocido.");
        }

        staff.setUnidadVenta(unidadVenta);

        staff.setUsuario(usuarioGuardado);

        return staffRepository.save(staff);
    }

    public String obtenerTipoEmpleado(Staff staff) {
        if (staff instanceof Cocinero) {
            return "Cocinero";
        }
        if (staff instanceof Cajero) {
            return "Cajero";
        }
        return "Staff";
    }

    public Staff modificarStaff(StaffDTO staffDTO) {
        // 1. Cargo el empleado existente
        Staff staff = staffRepository.findById(staffDTO.getIdStaff())
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Empleado no encontrado"));

        // 2. Si cambió el DNI, verifico que no lo tenga otro empleado
        staffRepository.findByDni(staffDTO.getDni()).ifPresent(otro -> {
            if (otro.getIdStaff() != staff.getIdStaff()) {
                throw new IllegalArgumentException("Ya existe un empleado registrado con ese dni.");
            }
        });

        // 3. Actualizo los campos comunes
        staff.setDni(staffDTO.getDni());
        staff.setNombre(staffDTO.getNombre());
        staff.setApellido(staffDTO.getApellido());
        staff.setFechaNacimiento(staffDTO.getFechaNacimiento());
        staff.setSueldoBase(staffDTO.getSueldoBase());

        // 4. Actualizo la unidad de venta si cambió
        if (staffDTO.getIdUnidadVenta() != null) {
            UnidadVenta unidadVenta = unidadVentaRepository.findById(staffDTO.getIdUnidadVenta())
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: Unidad de venta no encontrada"));
            staff.setUnidadVenta(unidadVenta);
        }

        // 5. Actualizo los campos propios de cada tipo
        if (staff instanceof Cajero cajero && staffDTO instanceof CajeroDTO caDTO) {
            cajero.setTurno(caDTO.getTurno());
        } else if (staff instanceof Cocinero cocinero && staffDTO instanceof CocineroDTO coDTO) {
            cocinero.setCategoria(coDTO.getCategoria());
            cocinero.setEspecialidad(coDTO.getEspecialidad());
        }

        return staffRepository.save(staff);
    }

    public void darBajaEmpleado(StaffDTO staffDTO) {
        Staff staff = staffRepository.findById(staffDTO.getIdStaff())
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Empleado no encontrado"));

        staff.setFechaEgreso(LocalDate.now());
        staffRepository.save(staff);

        Usuario usuario = staff.getUsuario();
        if (usuario != null) {
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
        }
    }

    public StaffDTO obtenerDTOPorId(int idStaff) {
        Staff staff = staffRepository.findById(idStaff)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Empleado no encontrado"));

        if (staff instanceof Cajero cajero) {
            CajeroDTO cajeroDTO = new CajeroDTO();
            cajeroDTO.setIdStaff(cajero.getIdStaff());
            cajeroDTO.setDni(cajero.getDni());
            cajeroDTO.setNombre(cajero.getNombre());
            cajeroDTO.setApellido(cajero.getApellido());
            cajeroDTO.setFechaNacimiento(cajero.getFechaNacimiento());
            cajeroDTO.setFechaIngreso(cajero.getFechaIngreso());
            cajeroDTO.setSueldoBase(cajero.getSueldoBase());
            cajeroDTO.setFechaEgreso(cajero.getFechaEgreso());
            cajeroDTO.setIdUnidadVenta(cajero.getUnidadVenta() != null ? cajero.getUnidadVenta().getIdUnidadVenta() : null);
            cajeroDTO.setTurno(cajero.getTurno());
            return cajeroDTO;
        } else if (staff instanceof Cocinero cocinero) {
            CocineroDTO cocineroDTO = new CocineroDTO();
            cocineroDTO.setIdStaff(cocinero.getIdStaff());
            cocineroDTO.setDni(cocinero.getDni());
            cocineroDTO.setNombre(cocinero.getNombre());
            cocineroDTO.setApellido(cocinero.getApellido());
            cocineroDTO.setFechaNacimiento(cocinero.getFechaNacimiento());
            cocineroDTO.setFechaIngreso(cocinero.getFechaIngreso());
            cocineroDTO.setSueldoBase(cocinero.getSueldoBase());
            cocineroDTO.setFechaEgreso(cocinero.getFechaEgreso());
            cocineroDTO.setIdUnidadVenta(cocinero.getUnidadVenta() != null ? cocinero.getUnidadVenta().getIdUnidadVenta() : null);
            cocineroDTO.setCategoria(cocinero.getCategoria());
            cocineroDTO.setEspecialidad(cocinero.getEspecialidad());
            return cocineroDTO;
        } else {
            throw new IllegalArgumentException("Tipo de empleado desconocido");
        }
    }
}
