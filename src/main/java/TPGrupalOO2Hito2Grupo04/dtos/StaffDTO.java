package TPGrupalOO2Hito2Grupo04.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class StaffDTO {
    private int idStaff;

    private long dni;

    private String nombre;

    private String apellido;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaNacimiento;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaIngreso;

    private float sueldoBase;

    private LocalDate fechaEgreso;

    private String emailStaff;

    private String passwordStaff;

    private Long idUnidadVenta;

    private Long idUsuario;

    public StaffDTO(long dni, String nombre, String apellido, LocalDate fechaNacimiento,
                    LocalDate fechaIngreso, float sueldoBase, LocalDate fechaEgreso,
                    Long idUnidadVenta, String emailStaff, String passwordStaff, Long idUsuario) {
        this.setDni(dni);
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaIngreso = fechaIngreso;
        this.sueldoBase = sueldoBase;
        this.fechaEgreso = fechaEgreso;
        this.idUnidadVenta = idUnidadVenta;
        this.emailStaff = emailStaff;
        this.passwordStaff = passwordStaff;
        this.idUsuario = idUsuario;
    }
}


