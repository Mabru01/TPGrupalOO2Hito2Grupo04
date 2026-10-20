package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.repositories.IUnidadVentaRepository;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;

@Service("unidadVentaService")
public class UnidadVentaService implements IUnidadVentaService {
    private static final Logger logger = LoggerFactory.getLogger(UnidadVentaService.class);

    private final IUnidadVentaRepository unidadVentaRepository;
    private final ModelMapper modelMapper;

    public UnidadVentaService(IUnidadVentaRepository unidadVentaRepository, ModelMapper modelMapper) {
        this.unidadVentaRepository = unidadVentaRepository;
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


}