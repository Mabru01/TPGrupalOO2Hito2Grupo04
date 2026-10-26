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
                            Usuario responsable, Festival festival, int cantidad, int tiempoMontaje) {
        super(nombreComercial, superficie, codigoUnico, responsable, festival);
        this.cantidad = cantidad;
        this.tiempoMontaje = tiempoMontaje;
    }

    @Override
    public double calcularCanon() {
        double base = this.superficie * 500.0;
        return base - (this.tiempoMontaje * 10.0);
    }

}