package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlatoDTO {

    private long idPlato;

    private String nombre;

    private float precio;

    private float costo;

    private long idUnidadVenta;

    public PlatoDTO(String nombre, float precio, float costo, long idUnidadVenta) {
        this.nombre = nombre;
        this.precio = precio;
        this.costo = costo;
        this.idUnidadVenta = idUnidadVenta;
    }
}
