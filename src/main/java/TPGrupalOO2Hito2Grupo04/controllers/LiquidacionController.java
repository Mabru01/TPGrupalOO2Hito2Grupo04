package TPGrupalOO2Hito2Grupo04.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.services.IFestivalService;
import TPGrupalOO2Hito2Grupo04.services.ILiquidacionService;

@Controller
@RequestMapping("/liquidacion")
public class LiquidacionController {

    private final ILiquidacionService liquidacionService;
    private final IFestivalService festivalService;

    public LiquidacionController(ILiquidacionService liquidacionService, IFestivalService festivalService) {
        this.liquidacionService = liquidacionService;
        this.festivalService = festivalService;
    }

    //GET Example: SERVER/liquidacion?festivalId=1&staffId=2
    @GetMapping("")
    public ModelAndView index(@RequestParam(name = "festivalId", required = false) Long festivalId,
                              @RequestParam(name = "staffId", required = false) Integer staffId) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.LIQUIDACION_INDEX);
        mAV.addObject("festivales", festivalService.getAll());
        mAV.addObject("empleados", liquidacionService.getEmpleados());
        mAV.addObject("festivalId", festivalId);
        mAV.addObject("staffId", staffId);
        if (festivalId != null && staffId != null) {
            mAV.addObject("liquidacion", liquidacionService.liquidar(staffId, festivalId));
        }
        return mAV;
    }
}
