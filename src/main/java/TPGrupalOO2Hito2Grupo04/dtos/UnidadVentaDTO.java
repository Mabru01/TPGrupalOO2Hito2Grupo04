package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class UnidadVentaDTO {

    private String nombreComercial;
    private double superficie;
    private String codigoUnico;
    private String emailResponsable;
    private String passwordResponsable;
    private long idFestival;      // ID del Festival

    public UnidadVentaDTO(String nombreComercial, double superficie,
                          String codigoUnico, String emailResponsable, String passwordResponsable, long idFestival) {
        this.nombreComercial = nombreComercial;
        this.superficie = superficie;
        this.codigoUnico = codigoUnico;
        this.emailResponsable = emailResponsable;
        this.passwordResponsable = passwordResponsable;
        this.idFestival = idFestival;
    }
}
