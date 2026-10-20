package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class UnidadVentaDTO {

    private long idUnidadVenta;
    protected String nombreComercial;
    protected double superficie;
    protected String codigoUnico;
    protected long idResponsable; // ID de la Persona responsable
    private long idFestival;      // ID del Festival

    public UnidadVentaDTO(long idUnidadVenta, String nombreComercial, double superficie,
                          String codigoUnico, long idResponsable, long idFestival) {
        this.idUnidadVenta = idUnidadVenta;
        this.nombreComercial = nombreComercial;
        this.superficie = superficie;
        this.codigoUnico = codigoUnico;
        this.idResponsable = idResponsable;
        this.idFestival = idFestival;
    }
}
