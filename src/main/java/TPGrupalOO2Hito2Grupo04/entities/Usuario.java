package TPGrupalOO2Hito2Grupo04.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="username", unique=true, nullable=false, length=45)
    private String mail;

    @Column(name="password", nullable=false, length=60)
    private String password;

    private boolean activo;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    public Usuario(String mail, String password, boolean activo, Rol rol) {
        this.mail = mail;
        this.password = password;
        this.activo = activo;
        this.rol = rol;
    }
}