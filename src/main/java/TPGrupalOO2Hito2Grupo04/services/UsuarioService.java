package TPGrupalOO2Hito2Grupo04.services;

import TPGrupalOO2Hito2Grupo04.entities.Rol;
import TPGrupalOO2Hito2Grupo04.entities.Usuario;
import TPGrupalOO2Hito2Grupo04.repositories.IUsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("usuarioService")
public class UsuarioService implements UserDetailsService {

	private IUsuarioRepository usuarioRepository;

	public UsuarioService(IUsuarioRepository userRepository) {
		this.usuarioRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
		TPGrupalOO2Hito2Grupo04.entities.Usuario usuario = usuarioRepository.findByMailAndFetchRolEagerly(mail);
		return buildUsuario(usuario, buildGrantedAuthorities(usuario.getRol()));
	}

	private UserDetails buildUsuario(TPGrupalOO2Hito2Grupo04.entities.Usuario usuario,
									 List<GrantedAuthority> authorities) {
		return new org.springframework.security.core.userdetails.User(
				usuario.getMail(),
				usuario.getPassword(),
				true, true, true, true,
				authorities);
	}

	private List<GrantedAuthority> buildGrantedAuthorities(Rol rol) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.add(new SimpleGrantedAuthority(rol.getRol()));
		return grantedAuthorities;
	}
}
