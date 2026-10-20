package TPGrupalOO2Hito2Grupo04.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED) //See more in https://www.baeldung.com/hibernate-inheritance
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPersona;

    private long dni;

    private String nombre;

    private String apellido;

    private LocalDate fechaNacimiento;

    private LocalDate fechaIngreso;

    private float sueldoBase;

    private LocalDate fechaEgreso;

    //private UnidadVenta unidadVenta;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    //@OneToMany(fetch=FetchType.LAZY, mappedBy="person")
    //private Set<Degree> degrees = new HashSet<>();

    public Persona(long dni, String nombre, String apellido, LocalDate fechaNacimiento,
                      LocalDate fechaIngreso, float sueldoBase, LocalDate fechaEgreso) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaIngreso = fechaIngreso;
        this.sueldoBase = sueldoBase;
        this.fechaEgreso = fechaEgreso;
    }
}