package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CambiarContraseniaDTO {
    private String contraseniaActual;
    private String nuevaContrasenia;
    private String confirmarContrasenia;
}
