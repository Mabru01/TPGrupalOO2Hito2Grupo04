package TPGrupalOO2Hito2Grupo04.services;

import TPGrupalOO2Hito2Grupo04.entities.ItemPedido;

import java.util.List;
import java.util.Optional;

public interface IItemPedidoService {

    List<ItemPedido> getAll();

    Optional<ItemPedido> findById(long id);

    ItemPedido insertOrUpdate(ItemPedido itemPedido);

    boolean remove(long id);
}