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
    private int idPedido;

    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "idUnidadVenta", nullable = false)
    private UnidadVenta unidadVenta;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    private Set<ItemPedido> items = new HashSet<>();

    private boolean terminado;

    public Pedido(LocalDate fecha, UnidadVenta unidadVenta) {
        this.fecha = fecha;
        this.unidadVenta = unidadVenta;
    }
}