package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemPedidoDTO {

    private int idItemPedido;

    private int cantidad;

    private int idPedido;

    private int idPlato;

    public ItemPedidoDTO(int cantidad, int idPedido, int idPlato) {
        this.cantidad = cantidad;
        this.idPedido = idPedido;
        this.idPlato = idPlato;
    }
}
