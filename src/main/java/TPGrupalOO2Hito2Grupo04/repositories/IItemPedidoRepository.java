package TPGrupalOO2Hito2Grupo04.repositories;

import TPGrupalOO2Hito2Grupo04.entities.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    // Metodos especificos.

    List<ItemPedido> findByPlatoIdPlato(Long idPlato);
}
