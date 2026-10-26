package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CajeroDTO extends StaffDTO{

    private String turno;

    public CajeroDTO(long dni, String nombre, String apellido, LocalDate fechaNacimiento,
                     LocalDate fechaIngreso, float sueldoBase, LocalDate fechaEgreso,
                     Long idUnidadVenta, String emailStaff, String passwordStaff, Long idUsuario, String turno) {
        super(dni, nombre, apellido, fechaNacimiento, fechaIngreso, sueldoBase, fechaEgreso,
                idUnidadVenta, emailStaff, passwordStaff, idUsuario);
        this.turno = turno;
    }
}



