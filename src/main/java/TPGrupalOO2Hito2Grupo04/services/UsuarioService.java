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
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		TPGrupalOO2Hito2Grupo04.entities.Usuario usuario = usuarioRepository.findByUsernameAndFetchUserRolesEagerly(username);
		return buildUsuario(usuario, buildGrantedAuthorities(usuario.getRol()));
	}

	private Usuario buildUsuario(TPGrupalOO2Hito2Grupo04.entities.Usuario usuario, List<GrantedAuthority> grantedAuthorities) {
		return new Usuario(usuario.getMail(), usuario.getPassword(), usuario.isActivo(),
						true, true, true, //accountNonExpired, credentialsNonExpired, accountNonLocked,
						grantedAuthorities);
	}

	private List<GrantedAuthority> buildGrantedAuthorities(Rol rol) {
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		for(Rol rol: rol) {
			grantedAuthorities.add(new SimpleGrantedAuthority(rol.getRol()));
		}
		return new ArrayList<>(grantedAuthorities);
	}
}
