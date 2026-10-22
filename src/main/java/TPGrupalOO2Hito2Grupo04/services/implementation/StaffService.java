package TPGrupalOO2Hito2Grupo04.services.implementation;

import TPGrupalOO2Hito2Grupo04.entities.Staff;
import TPGrupalOO2Hito2Grupo04.repositories.IStaffRepository;
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
    private final ModelMapper modelMapper;

    public StaffService(IStaffRepository staffRepository, ModelMapper modelMapper) {
        this.staffRepository = staffRepository;
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
}
