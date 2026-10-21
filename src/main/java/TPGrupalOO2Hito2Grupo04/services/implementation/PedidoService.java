package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import TPGrupalOO2Hito2Grupo04.entities.Pedido;
import TPGrupalOO2Hito2Grupo04.repositories.IPedidoRepository;
import TPGrupalOO2Hito2Grupo04.services.IPedidoService;

@Service("pedidoService")
public class PedidoService implements IPedidoService {

    private static final Logger logger =
            LoggerFactory.getLogger(PedidoService.class);

    private final IPedidoRepository pedidoRepository;

    public PedidoService(IPedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public List<Pedido> getAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public Optional<Pedido> findById(long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public Pedido insertOrUpdate(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    public boolean remove(long id) {
        try {
            pedidoRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException |
                 DataIntegrityViolationException e) {
            logger.warn("No se pudo eliminar el Pedido con id {}", id, e);
            return false;
        } catch (DataAccessException e) {
            logger.error(
                    "Error de base de datos al eliminar el Pedido con id {}", id, e);
            return false;
        }
    }
}