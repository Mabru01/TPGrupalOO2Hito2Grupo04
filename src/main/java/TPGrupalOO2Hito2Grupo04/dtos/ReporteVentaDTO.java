package TPGrupalOO2Hito2Grupo04.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Reporte de recaudación de la unidad en su festival (no persiste)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentaDTO {

    private String festival;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private String unidad;

    private int cantidadPedidos;

    private int platosVendidos;

    private BigDecimal recaudacionTotal;

    private BigDecimal promedioPorPedido;

    // Detalle ordenado de mayor a menor recaudación
    private List<VentaPlato> ventasPorPlato;

    // Lo que recaudó un plato en el festival
    @Getter
    @AllArgsConstructor
    public static class VentaPlato {

        private String plato;

        private int cantidad;

        private BigDecimal precioUnitario;

        private BigDecimal subtotal;

        // Porcentaje sobre la recaudación total
        private BigDecimal porcentaje;
    }
}
