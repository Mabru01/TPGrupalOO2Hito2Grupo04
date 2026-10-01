package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlatoDTO {

    private int idPlato;

    private String nombre;

    private float precio;

    private float costo;

    private int idUnidadVenta;

    public PlatoDTO(String nombre, float precio, float costo, int idUnidadVenta) {
        this.nombre = nombre;
        this.precio = precio;
        this.costo = costo;
        this.idUnidadVenta = idUnidadVenta;
    }
}
