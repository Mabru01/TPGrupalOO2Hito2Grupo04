package TPGrupalOO2Hito2Grupo04.controllers;

import java.time.LocalDate;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.services.IFestivalService;

// El administrador ve el detalle de cada festival: datos, unidades de venta y costos
@Controller
@RequestMapping("/festival")
@PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN')")
public class FestivalController {

    private final IFestivalService festivalService;

    public FestivalController(IFestivalService festivalService) {
        this.festivalService = festivalService;
    }

    //GET Example: SERVER/festival/1
    @GetMapping("/{id}")
    public ModelAndView detalle(@PathVariable long id) {
        try {
            Festival festival = festivalService.findByIdWithUnidades(id);
            ModelAndView mAV = new ModelAndView(ViewRouteHelper.FESTIVAL_DETAIL);
            mAV.addObject("festival", festival);
            mAV.addObject("hoy", LocalDate.now());
            return mAV;
        } catch (IllegalArgumentException e) {
            // Festival inexistente: vuelve al inicio
            return new ModelAndView("redirect:" + ViewRouteHelper.ROUTE_INDEX);
        }
    }
}
