package TPGrupalOO2Hito2Grupo04.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    private LocalDate fechaTransaccion;

    @ManyToOne
    @JoinColumn(name = "idUnidadVenta", nullable = false)
    private UnidadVenta unidadVenta;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ItemPedido> items = new HashSet<>();

    private boolean terminado;

    public Pedido(LocalDate fechaTransaccion, UnidadVenta unidadVenta) {
        this.fechaTransaccion = fechaTransaccion;
        this.unidadVenta = unidadVenta;
    }

    // Helper para mantener la bidireccionalidad al agregar ítems
    public void agregarItem(ItemPedido item) {
        items.add(item);
        item.setPedido(this);
    }

    // Metodo para sacar precio de un total
    public double getPrecioTotal() {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(item -> (item.getPlato() != null ? item.getPlato().getPrecio() * item.getCantidad() : 0.0))
                .sum();
    }
}