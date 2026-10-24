package TPGrupalOO2Hito2Grupo04.services.implementation;

import TPGrupalOO2Hito2Grupo04.dtos.*;
import TPGrupalOO2Hito2Grupo04.entities.*;
import TPGrupalOO2Hito2Grupo04.repositories.IStaffRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IUnidadVentaRepository;
import TPGrupalOO2Hito2Grupo04.services.IStaffService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service("staffService")
public class StaffService implements IStaffService {
    private static final Logger logger = LoggerFactory.getLogger(StaffService.class);

    private final IStaffRepository staffRepository;
    private final IUnidadVentaRepository unidadVentaRepository;
    private final ModelMapper modelMapper;

    public StaffService(IStaffRepository staffRepository, IUnidadVentaRepository unidadVentaRepository, ModelMapper modelMapper) {
        this.staffRepository = staffRepository;
        this.unidadVentaRepository = unidadVentaRepository;
        this.modelMapper = modelMapper;
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

        // Se busca la Unidad de Venta en la base de datos
        UnidadVenta unidadVenta = unidadVentaRepository.findById(staffDTO.getIdUnidadVenta())
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Unidad de venta no encontrada"));

        // Creo el staff y lo registro
        Staff staff;

        System.out.println("DTO recibido: " + staffDTO.getClass().getName());
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
}
