package TPGrupalOO2Hito2Grupo04.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor

public class FoodTruck extends UnidadVenta{

    private String patente;
    private boolean usaLuz;

    public FoodTruck(String nombreComercial, double superficie, String codigoUnico,
                     Persona responsable, Festival festival, String patente, boolean usaLuz) {
        super(nombreComercial, superficie, codigoUnico, responsable, festival);
        this.patente = patente;
        this.usaLuz = usaLuz;
    }
}
