package TPGrupalOO2Hito2Grupo04.services;

import TPGrupalOO2Hito2Grupo04.entities.Pedido;
import TPGrupalOO2Hito2Grupo04.dtos.PedidoDTO;

import java.util.List;
import java.util.Optional;

public interface IPedidoService {

    List<Pedido> traerTodo();

    Optional<Pedido> traerPorId(Long id);

    Pedido insertarOActualizar(Pedido pedido);

    boolean eliminar(long id);

    Pedido insertarPedido(PedidoDTO pedidoDTO) throws Exception;

    Pedido actualizarPedido(Long id, PedidoDTO pedidoDTO) throws Exception;

}
