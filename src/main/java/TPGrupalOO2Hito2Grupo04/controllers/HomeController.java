package TPGrupalOO2Hito2Grupo04.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.services.IFestivalService;

@Controller
public class HomeController {

    private final IFestivalService festivalService;

    public HomeController(IFestivalService festivalService) {
        this.festivalService = festivalService;
    }

    //GET Example: SERVER/index
    // Página de inicio pública: festivales precargados y las acciones de registrarse e iniciar sesión.
    // Sin sesión: solo los festivales en curso. Con sesión: también los próximos.
    // Los finalizados solo los ven el administrador y el responsable.
    @GetMapping("/index")
    public ModelAndView index(Authentication authentication) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.INDEX);
        LocalDate hoy = LocalDate.now();
        boolean conSesion = authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        boolean verFinalizados = puedeVerFinalizados(authentication);

        List<Festival> festivales = festivalService.getAllWithUnidades();
        if (!conSesion) {
            festivales = festivales.stream()
                    .filter(f -> !f.getFechaInicio().isAfter(hoy) && !f.getFechaFin().isBefore(hoy))
                    .toList();
        } else if (!verFinalizados) {
            festivales = festivales.stream()
                    .filter(f -> !f.getFechaFin().isBefore(hoy))
                    .toList();
        }

        mAV.addObject("festivales", festivales);
        mAV.addObject("verFinalizados", verFinalizados);
        mAV.addObject("hoy", hoy);
        return mAV;
    }

    @GetMapping("/")
    public RedirectView redirectToIndex() {
        return new RedirectView(ViewRouteHelper.ROUTE_INDEX);
    }

    // Los roles se guardan sin prefijo (ADMIN, RESPONSABLE); se acepta también con ROLE_
    private boolean puedeVerFinalizados(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String rol = authority.getAuthority().replace("ROLE_", "");
            if (rol.equals("ADMIN") || rol.equals("RESPONSABLE")) {
                return true;
            }
        }
        return false;
    }
}
