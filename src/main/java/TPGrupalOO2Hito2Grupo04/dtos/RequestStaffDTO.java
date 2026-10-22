package TPGrupalOO2Hito2Grupo04.dtos;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
public class RequestStaffDTO {

    private String nombre;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    public RequestStaffDTO(String name, LocalDate fechaNacimiento) {
        this.nombre = name;
        this.fechaNacimiento = fechaNacimiento;
    }
}