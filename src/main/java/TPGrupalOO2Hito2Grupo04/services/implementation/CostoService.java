package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import TPGrupalOO2Hito2Grupo04.entities.Costo;
import TPGrupalOO2Hito2Grupo04.repositories.ICostoRepository;
import TPGrupalOO2Hito2Grupo04.services.ICostoService;

@Service("costoService")
public class CostoService implements ICostoService {
    private static final Logger logger = LoggerFactory.getLogger(CostoService.class);

    private final ICostoRepository costoRepository;

    public CostoService(ICostoRepository costoRepository) {
        this.costoRepository = costoRepository;
    }

    @Override
    public List<Costo> getAll() {
        return costoRepository.findAll();
    }

    @Override
    public Optional<Costo> findById(long id) {
        return costoRepository.findById(id);
    }

    @Override
    public Costo findByFestival(long festivalId) {
        return costoRepository.findByFestivalId(festivalId).orElseThrow(
                () -> new IllegalArgumentException("ERROR: Costo not found for festival id: " + festivalId)
        );
    }

    @Override
    public Costo insertOrUpdate(Costo costo) {
        return costoRepository.save(costo);
    }

    @Override
    public boolean remove(long id) {
        try {
            costoRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException | DataIntegrityViolationException e) {
            logger.warn("Could not remove Costo with id {}", id, e);
            return false;
        } catch (DataAccessException e) {
            logger.error("Database error while removing Costo with id {}", id, e);
            return false;
        }
    }
}