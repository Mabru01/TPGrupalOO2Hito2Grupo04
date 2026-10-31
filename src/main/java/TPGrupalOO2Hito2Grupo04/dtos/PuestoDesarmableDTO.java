package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PuestoDesarmableDTO extends UnidadVentaDTO {

	private int cantidad;
	private int tiempoMontaje;

    public PuestoDesarmableDTO(String nombreComercial, double superficie,
                               String codigoUnico, boolean activa, String emailResponsable, String passwordResponsable, int idFestival,
                               int cantidad, int tiempoMontaje) {
        super(nombreComercial, superficie, codigoUnico, activa, emailResponsable, passwordResponsable, idFestival);
        this.cantidad = cantidad;
        this.tiempoMontaje = tiempoMontaje;
    }
}