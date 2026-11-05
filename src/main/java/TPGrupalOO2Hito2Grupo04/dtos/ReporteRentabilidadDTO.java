package TPGrupalOO2Hito2Grupo04.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Rentabilidad neta de la unidad en su festival o entre dos fechas (no persiste)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRentabilidadDTO {

    private String festival;

    private String unidad;

    private LocalDate inicioFestival;

    private LocalDate finFestival;

    // Período calculado: el festival completo o el rango elegido
    private LocalDate desde;

    private LocalDate hasta;

    private int diasPeriodo;

    private int diasFestival;

    private boolean festivalCompleto;

    private int cantidadPedidos;

    private BigDecimal ventas;

    private BigDecimal costoPlatos;

    // Ventas - costo de los platos
    private BigDecimal gananciaBruta;

    private BigDecimal sueldos;

    private BigDecimal canon;

    // Ganancia bruta - sueldos - canon
    private BigDecimal rentabilidadNeta;

    // Porcentaje de las ventas que queda como rentabilidad
    private BigDecimal margen;

    private List<SueldoEmpleado> sueldosPorEmpleado;

    // Lo que se le paga a un empleado en el festival y la parte que corresponde al período
    @Getter
    @AllArgsConstructor
    public static class SueldoEmpleado {

        private String empleado;

        private String puesto;

        private BigDecimal sueldoFestival;

        private BigDecimal sueldoPeriodo;
    }
}
