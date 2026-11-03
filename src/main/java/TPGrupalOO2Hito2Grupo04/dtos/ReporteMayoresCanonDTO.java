package TPGrupalOO2Hito2Grupo04.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ReporteMayoresCanonDTO {

    private String nombreComercial;
    private String codigoUnico;
    private String tipoUnidad;
    private double canon;

}
