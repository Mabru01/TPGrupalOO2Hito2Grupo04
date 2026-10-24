package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import TPGrupalOO2Hito2Grupo04.entities.ItemPedido;
import TPGrupalOO2Hito2Grupo04.repositories.IItemPedidoRepository;
import TPGrupalOO2Hito2Grupo04.services.IItemPedidoService;

@Service("itemPedidoService")
public class ItemPedidoService implements IItemPedidoService {

    private static final Logger logger =
            LoggerFactory.getLogger(ItemPedidoService.class);

    private final IItemPedidoRepository itemPedidoRepository;

    public ItemPedidoService(
            IItemPedidoRepository itemPedidoRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @Override
    public List<ItemPedido> getAll() {
        return itemPedidoRepository.findAll();
    }

    @Override
    public Optional<ItemPedido> findById(long id) {
        return itemPedidoRepository.findById(id);
    }

    @Override
    public ItemPedido insertOrUpdate(ItemPedido itemPedido) {
        return itemPedidoRepository.save(itemPedido);
    }

    @Override
    public boolean remove(long id) {
        try {
            itemPedidoRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException |
                 DataIntegrityViolationException e) {
            logger.warn(
                    "No se pudo eliminar el Itempedido con id {}", id, e);
            return false;
        } catch (DataAccessException e) {
            logger.error(
                    "Error de base de datos al eliminar el Itempedido con id {}",
                    id, e);
            return false;
        }
    }
}