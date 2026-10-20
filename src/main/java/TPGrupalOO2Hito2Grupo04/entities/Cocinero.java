package TPGrupalOO2Hito2Grupo04.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Cocinero extends Persona {

    private String especialidad;
    private String plusCategoria;
}