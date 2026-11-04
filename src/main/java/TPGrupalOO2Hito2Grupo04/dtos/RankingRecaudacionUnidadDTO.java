package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class RankingRecaudacionUnidadDTO {
    private String nombreComercial;
    private String codigoUnico;
    private double recaudacionTotal;

}
