package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CocineroDTO extends StaffDTO{

    private String categoria;

    private String especialidad;

    public CocineroDTO(long dni, String nombre, String apellido, LocalDate fechaNacimiento,
                       LocalDate fechaIngreso, float sueldoBase, LocalDate fechaEgreso,
                       Long idUnidadVenta, String categoria, String especialidad, String emailEmpleado,
                       String passwordStaff, Long idUsuario) {
        super(dni, nombre, apellido, fechaNacimiento, fechaIngreso, sueldoBase, fechaEgreso,
                idUnidadVenta, emailEmpleado, passwordStaff, idUsuario);
        this.categoria = categoria;
        this.especialidad = especialidad;
    }
}