package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.repositories.IFestivalRepository;
import TPGrupalOO2Hito2Grupo04.services.IFestivalService;

@Service("festivalService")
public class FestivalService implements IFestivalService {
    private static final Logger logger = LoggerFactory.getLogger(FestivalService.class);

    private final IFestivalRepository festivalRepository;

    public FestivalService(IFestivalRepository festivalRepository) {
        this.festivalRepository = festivalRepository;
    }

    @Override
    public List<Festival> getAll() {
        return festivalRepository.findAll();
    }

    @Override
    public Optional<Festival> findById(long id) {
        return festivalRepository.findById(id);
    }

    @Override
    public Festival findByNombre(String nombre) {
        return festivalRepository.findByNombre(nombre).orElseThrow(
                () -> new IllegalArgumentException("ERROR: Festival not found with nombre: " + nombre)
        );
    }

    @Override
    public Festival findByIdWithUnidades(long id) {
        return festivalRepository.findByIdAndFetchUnidadesEagerly(id).orElseThrow(
                () -> new IllegalArgumentException("ERROR: Festival not found with id: " + id)
        );
    }

    @Override
    public Festival insertOrUpdate(Festival festival) {
        return festivalRepository.save(festival);
    }

    @Override
    public boolean remove(long id) {
        try {
            festivalRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException | DataIntegrityViolationException e) {
            logger.warn("Could not remove Festival with id {}", id, e);
            return false;
        } catch (DataAccessException e) {
            logger.error("Database error while removing Festival with id {}", id, e);
            return false;
        }
    }
}