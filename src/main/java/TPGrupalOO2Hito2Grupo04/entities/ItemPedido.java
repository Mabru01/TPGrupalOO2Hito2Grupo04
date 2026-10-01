package TPGrupalOO2Hito2Grupo04.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idItemPedido;

    private int cantidad;

    @ManyToOne
    @JoinColumn(name = "idPedido", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "idPlato", nullable = false)
    private Plato plato;

    public ItemPedido(Plato plato, int cantidad, Pedido pedido) {
        this.plato = plato;
        this.cantidad = cantidad;
        this.pedido = pedido;
    }
}