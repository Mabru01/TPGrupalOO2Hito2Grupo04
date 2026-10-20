package TPGrupalOO2Hito2Grupo04.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class PuestoDesarmable extends UnidadVenta {

    private int cantidad;
    private int tiempoMontaje;

    public PuestoDesarmable(String nombreComercial, double superficie, String codigoUnico,
                            Persona responsable, Festival festival, int cantidad, int tiempoMontaje) {
        super(nombreComercial, superficie, codigoUnico, responsable, festival);
        this.cantidad = cantidad;
        this.tiempoMontaje = tiempoMontaje;
    }
}