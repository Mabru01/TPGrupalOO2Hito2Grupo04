package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemPedidoDTO {

    private long idItemPedido;

    private int cantidad;

    private long idPedido;

    private long idPlato;

    public ItemPedidoDTO(int cantidad, long idPedido, long idPlato) {
        this.cantidad = cantidad;
        this.idPedido = idPedido;
        this.idPlato = idPlato;
    }
}
