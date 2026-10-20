package TPGrupalOO2Hito2Grupo04.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name="user_role", uniqueConstraints=@UniqueConstraint(columnNames= {"role", "user_id"}))
public class Rol {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idRol;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="usuario_id", nullable=false)
	private Usuario usuario;

	@Column(name="rol", nullable=false, length=100)
	private String rol;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public Rol(int id, Usuario usuario, String role) {
		this.idRol = idRol;
		this.usuario = usuario;
		this.rol = rol;
	}
}
