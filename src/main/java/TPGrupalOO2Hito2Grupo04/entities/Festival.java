package TPGrupalOO2Hito2Grupo04.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="nombre", unique=true, nullable=false, length=100)
    private String nombre;

    private String temporada;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="festival")
    private Set<UnidadVenta> unidadesVenta = new HashSet<>();

    @OneToOne(fetch=FetchType.LAZY, mappedBy="festival")
    private Costo costo;

    public Festival(long id, String nombre, String temporada, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.nombre = nombre;
        this.temporada = temporada;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public Festival(String nombre, String temporada, LocalDate fechaInicio, LocalDate fechaFin) {
        this.nombre = nombre;
        this.temporada = temporada;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Cantidad de días del festival, contando el día de inicio y el de fin
    public int getDuracionEnDias() {
        return (int) ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
    }

    // Hasta 15 días: 50%. De 16 a 30: 100%. Más de 30: (int(dias / 15) + 1) * 50%
    public BigDecimal calcularPorcentajeSueldo() {
        int dias = getDuracionEnDias();
        if (dias <= 15) {
            return new BigDecimal("0.5");
        }
        if (dias <= 30) {
            return BigDecimal.ONE;
        }
        return new BigDecimal("0.5").multiply(BigDecimal.valueOf(dias / 15 + 1));
    }
}
