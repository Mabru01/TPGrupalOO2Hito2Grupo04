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

    public PuestoDesarmableDTO(long idUnidadVenta, String nombreComercial, double superficie,
                               String codigoUnico, int idResponsable, int idFestival,
                               int cantidad, int tiempoMontaje) {
        super(idUnidadVenta, nombreComercial, superficie, codigoUnico, idResponsable, idFestival);
        this.cantidad = cantidad;
        this.tiempoMontaje = tiempoMontaje;
    }
}