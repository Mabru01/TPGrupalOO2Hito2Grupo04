package TPGrupalOO2Hito2Grupo04.controllers;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.services.IFestivalService;

@Controller
public class HomeController {

    private final IFestivalService festivalService;

    public HomeController(IFestivalService festivalService) {
        this.festivalService = festivalService;
    }

    //GET Example: SERVER/index
    // Página de inicio pública: festivales precargados y las acciones de registrarse e iniciar sesión
    @GetMapping("/index")
    public ModelAndView index() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.INDEX);
        mAV.addObject("festivales", festivalService.getAllWithUnidades());
        mAV.addObject("hoy", LocalDate.now());
        return mAV;
    }

    @GetMapping("/")
    public RedirectView redirectToIndex() {
        return new RedirectView(ViewRouteHelper.ROUTE_INDEX);
    }
}
