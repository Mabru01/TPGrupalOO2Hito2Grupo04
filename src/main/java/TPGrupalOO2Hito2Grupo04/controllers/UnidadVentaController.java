package TPGrupalOO2Hito2Grupo04.controllers;

import TPGrupalOO2Hito2Grupo04.dtos.FoodTruckDTO;
import TPGrupalOO2Hito2Grupo04.dtos.PuestoDesarmableDTO;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/unidadVenta")
public class UnidadVentaController {

    private static final String UNIDAD_VENTA_ATTR = "unidadVentaDTO";

    private final IUnidadVentaService unidadVentaService;

    public UnidadVentaController(IUnidadVentaService unidadVentaService) {
        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping("")
    public ModelAndView index() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.UnidadVenta_INDEX);
        mAV.addObject("unidadVentas", unidadVentaService.getAll());
        return mAV;
    }

    @GetMapping("/nuevo/food-truck")
    public ModelAndView nuevoFoodTruck() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.UnidadVenta_NEW_FOOD_TRUCK);
        mAV.addObject(UNIDAD_VENTA_ATTR, new FoodTruckDTO());
        return mAV;
    }

    @GetMapping("/nuevo/puesto-desarmable")
    public ModelAndView nuevoPuestoDesarmable() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.UnidadVenta_NEW_PUESTO);
        mAV.addObject(UNIDAD_VENTA_ATTR, new PuestoDesarmableDTO());
        return mAV;
    }

    @PostMapping("/guardar/food-truck")
    public String guardarFoodTruck(@ModelAttribute("unidadVentaDTO") FoodTruckDTO dto, Model model) {
        try {
            unidadVentaService.registrarUnidadVentaConResponsable(dto);
            return "redirect:" + ViewRouteHelper.ROUTE_INDEX;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return ViewRouteHelper.UnidadVenta_NEW_FOOD_TRUCK; //
        }
    }

    @PostMapping("/guardar/puesto-desarmable")
    public String guardarPuestoDesarmable(
            @ModelAttribute("unidadVentaDTO") PuestoDesarmableDTO unidadVentaDTO,
            Model model) {
        try {
            unidadVentaService.registrarUnidadVentaConResponsable(unidadVentaDTO);
            return "redirect:" + ViewRouteHelper.ROUTE_INDEX;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return ViewRouteHelper.UnidadVenta_NEW_PUESTO;
        }
    }



}