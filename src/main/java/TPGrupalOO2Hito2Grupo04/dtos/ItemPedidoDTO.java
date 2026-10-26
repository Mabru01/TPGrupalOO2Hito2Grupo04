package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemPedidoDTO {

    private Long idItemPedido;

    private int cantidad;

    private Long idPedido;

    private Long idPlato;

    public ItemPedidoDTO(int cantidad, long idPedido, Long idPlato) {
        this.cantidad = cantidad;
        this.idPedido = idPedido;
        this.idPlato = idPlato;
    }
}
