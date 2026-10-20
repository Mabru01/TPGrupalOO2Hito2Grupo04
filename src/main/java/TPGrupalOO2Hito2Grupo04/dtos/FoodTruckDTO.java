package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FoodtruckDTO extends UnidadVentaDTO {

	private String patente;
	private boolean usaLuz;

    public FoodtruckDTO(long idUnidadVenta, String nombreComercial, double superficie,
                        String codigoUnico, int idResponsable, int idFestival,
                        String patente, boolean usaLuz) {
        super(idUnidadVenta, nombreComercial, superficie, codigoUnico, idResponsable, idFestival);
        this.patente = patente;
        this.usaLuz = usaLuz;
    }
}