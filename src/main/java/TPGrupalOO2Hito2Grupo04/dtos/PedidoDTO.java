package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PedidoDTO {

    private Long idPedido;

    private LocalDate fechaTransaccion;

    private Long idUnidadVenta;

    private boolean terminado;

    // Lista para recibir los ítems seleccionados en el formulario de alta
    private List<ItemPedidoDTO> items = new ArrayList<>();

    public PedidoDTO(LocalDate fechaTransaccion, Long idUnidadVenta, boolean terminado) {
        this.fechaTransaccion = fechaTransaccion;
        this.idUnidadVenta = idUnidadVenta;
        this.terminado = terminado;
    }
}
