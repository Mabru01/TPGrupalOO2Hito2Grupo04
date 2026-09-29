package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PersonaDTO {
    private int idPersona;

    private long dni;

    private String nombre;

    private String apellido;

    private LocalDate fechaNacimiento;

    private LocalDate fechaIngreso;

    private float sueldoBase;

    private LocalDate fechaEgreso;

    //private UnidadVenta unidadVenta;

    public PersonaDTO(long dni, String nombre, String apellido, LocalDate fechaNacimiento,
                      LocalDate fechaIngreso, float sueldoBase, LocalDate fechaEgreso) {
        this.setDni(dni);
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaIngreso = fechaIngreso;
        this.sueldoBase = sueldoBase;
        this.fechaEgreso = fechaEgreso;
    }
}


