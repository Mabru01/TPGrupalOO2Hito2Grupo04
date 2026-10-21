package TPGrupalOO2Hito2Grupo04.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED) //See more in https://www.baeldung.com/hibernate-inheritance
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idStaff;

    private long dni;

    private String nombre;

    private String apellido;

    private LocalDate fechaNacimiento;

    private LocalDate fechaIngreso;

    private float sueldoBase;

    private LocalDate fechaEgreso;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    //  Usuario
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    //  UnidadVenta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_venta_id")
    private UnidadVenta unidadVenta;

    public Staff(long dni, String nombre, String apellido, LocalDate fechaNacimiento,
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