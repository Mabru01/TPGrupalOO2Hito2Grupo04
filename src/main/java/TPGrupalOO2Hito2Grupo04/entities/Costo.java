package TPGrupalOO2Hito2Grupo04.entities;

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
    private long id;

    private double costoSuperficie;

    private double costoMontaje;

    private double plusElectricidad;

    private double sueldoBase;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="festival_id", nullable=false, unique=true)
    private Festival festival;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Costo(double costoSuperficie, double costoMontaje, double plusElectricidad, double sueldoBase, Festival festival) {
        this.costoSuperficie = costoSuperficie;
        this.costoMontaje = costoMontaje;
        this.plusElectricidad = plusElectricidad;
        this.sueldoBase = sueldoBase;
        this.festival = festival;
    }
}