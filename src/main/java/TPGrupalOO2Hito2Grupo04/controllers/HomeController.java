package TPGrupalOO2Hito2Grupo04.controllers;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import TPGrupalOO2Hito2Grupo04.services.IFestivalService;

@Controller
public class HomeController {

    private final IFestivalService festivalService;

    public HomeController(IFestivalService festivalService) {
        this.festivalService = festivalService;
    }

    // Página de inicio pública: festivales precargados y las acciones de registro e inicio de sesión
    @GetMapping({"/", "/index"})
    public ModelAndView index() {
        ModelAndView mAV = new ModelAndView("home/index");
        mAV.addObject("festivales", festivalService.getAll());
        mAV.addObject("hoy", LocalDate.now());
        return mAV;
    }
}
