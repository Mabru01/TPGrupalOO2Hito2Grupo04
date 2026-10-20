package TPGrupalOO2Hito2Grupo04.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPlato;

    private String nombre;

    private float precio;

    private float costo;

    @ManyToOne
    @JoinColumn(name = "idUnidadVenta", nullable = false)
    private UnidadVenta unidadVenta;

    public Plato(String nombre, float precio, float costo, UnidadVenta unidadVenta) {
        this.nombre = nombre;
        this.precio = precio;
        this.costo = costo;
        this.unidadVenta = unidadVenta;
    }
}
