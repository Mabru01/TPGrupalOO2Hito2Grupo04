package TPGrupalOO2Hito2Grupo04.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter  @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UnidadVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUnidadVenta")
    private long idUnidadVenta;

    @Column(name = "nombreComercial", nullable = false)
    protected String nombreComercial;

    @Column(name = "superficie", nullable = false)
    protected double superficie;

    @Column(name = "codigoUnico", nullable = false)
    protected String codigoUnico;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario", nullable = false)
    protected Usuario responsable;

    @Column (name = "activa", nullable = false)
    private boolean activa;

    // many-to-one name="festival" column="idFestival" not-null="true"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idFestival", nullable = false)
    private Festival festival;

    @OneToMany(mappedBy = "unidadVenta", fetch = FetchType.LAZY)
    @OrderBy("idStaff ASC")
    private Set<Staff> staff = new HashSet<>();

    // --- PLATOS ---
    @OneToMany(mappedBy = "unidadVenta", fetch = FetchType.LAZY)
    @OrderBy("idPlato ASC")
    private Set<Plato> platos = new HashSet<>();

    // --- PEDIDOS ---
    @OneToMany(mappedBy = "unidadVenta", fetch = FetchType.LAZY)
    @OrderBy("idPedido ASC")
    private Set<Pedido> pedidos = new HashSet<>();

    public UnidadVenta(String nombreComercial, double superficie, String codigoUnico,
                       Usuario responsable, Festival festival) {
        this.nombreComercial = nombreComercial;
        this.superficie = superficie;
        this.codigoUnico = codigoUnico;
        this.responsable = responsable;
        this.festival = festival;
        this.activa = true;
    }

    public abstract double calcularCanon();

}