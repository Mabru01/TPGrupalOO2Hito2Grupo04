package TPGrupalOO2Hito2Grupo04.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Datos del formulario de alta de festival junto con sus costos
@Getter
@Setter
@NoArgsConstructor
public class FestivalDTO {

    private String nombre;

    private String temporada;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    // Costos del festival
    private BigDecimal costoSuperficie;

    private BigDecimal costoMontaje;

    private BigDecimal plusElectricidad;

    private BigDecimal sueldoBase;

    private BigDecimal antiguedadPorAnio;

    private BigDecimal plusCocinero;

    private BigDecimal plusCocineroAyudante;

    private BigDecimal plusLavaplatos;
}
