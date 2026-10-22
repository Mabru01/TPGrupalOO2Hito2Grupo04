package TPGrupalOO2Hito2Grupo04.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Costo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idCosto;

    private BigDecimal costoSuperficie;

    private BigDecimal costoMontaje;

    private BigDecimal plusElectricidad;

    private BigDecimal sueldoBase;

    private BigDecimal antiguedadPorAnio;

    private BigDecimal plusCocinero;

    private BigDecimal plusCocineroAyudante;

    private BigDecimal plusLavaplatos;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="festival_id", nullable=false, unique=true)
    private Festival festival;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Costo(BigDecimal costoSuperficie, BigDecimal costoMontaje, BigDecimal plusElectricidad, BigDecimal sueldoBase, Festival festival) {
        this.costoSuperficie = costoSuperficie;
        this.costoMontaje = costoMontaje;
        this.plusElectricidad = plusElectricidad;
        this.sueldoBase = sueldoBase;
        this.festival = festival;
    }

    // Recibe la categoría guardada en Cocinero.plusCategoria
    public BigDecimal getPlusCategoria(String categoria) {
        if (categoria == null) {
            return BigDecimal.ZERO;
        }
        return switch (categoria.trim().toUpperCase()) {
            case "COCINERO" -> plusCocinero;
            case "COCINERO_AYUDANTE", "COCINERO AYUDANTE", "AYUDANTE" -> plusCocineroAyudante;
            case "LAVAPLATOS" -> plusLavaplatos;
            default -> BigDecimal.ZERO;
        };
    }
}
