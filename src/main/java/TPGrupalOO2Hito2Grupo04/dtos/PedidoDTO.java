package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PedidoDTO {

    private int idPedido;

    private LocalDate fecha;

    private int idUnidadVenta;

    private boolean terminado;

    public PedidoDTO(LocalDate fecha, int idUnidadVenta, boolean terminado) {
        this.fecha = fecha;
        this.idUnidadVenta = idUnidadVenta;
        this.terminado = terminado;
    }
}
