package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FoodTruckDTO extends UnidadVentaDTO {

	private String patente;
	private boolean usaLuz;

    public FoodTruckDTO(String nombreComercial, double superficie,
                        String codigoUnico, boolean activa, String emailResponsable, String passwordResponsable, long idFestival,
                        String patente, boolean usaLuz) {
        super(nombreComercial, superficie, codigoUnico, activa, emailResponsable, passwordResponsable, idFestival);
        this.patente = patente;
        this.usaLuz = usaLuz;
    }
}