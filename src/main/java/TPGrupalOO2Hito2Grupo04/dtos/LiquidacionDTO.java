package TPGrupalOO2Hito2Grupo04.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionDTO {

    private int idStaff;

    private String empleado;

    private long dni;

    private String tipo;

    private String unidad;

    private String festival;

    private int diasFestival;

    private BigDecimal porcentaje;

    private BigDecimal sueldoBase;

    private String conceptoAdicional;

    private BigDecimal montoAdicional;

    private BigDecimal total;
}
