package TPGrupalOO2Hito2Grupo04.controllers;

import TPGrupalOO2Hito2Grupo04.dtos.FoodTruckDTO;
import TPGrupalOO2Hito2Grupo04.dtos.PuestoDesarmableDTO;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;

import org.springframework.security.access.prepost.PreAuthorize;
import java.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/unidadVenta")
public class UnidadVentaController {

    private static final String UNIDAD_VENTA_ATTR = "unidadVentaDTO";

    private final IUnidadVentaService unidadVentaService;

    public UnidadVentaController(IUnidadVentaService unidadVentaService) {
        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN')")
    public ModelAndView index(Principal principal, Authentication authentication) {
        List<UnidadVenta> unidades;
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.UnidadVenta_INDEX);
        mAV.addObject("unidadVentas", unidadVentaService.getAll());

        return mAV;
    }


    @GetMapping("/mi-unidad")
    @PreAuthorize("hasAuthority('RESPONSABLE')")
    public ModelAndView verMiUnidad(Principal principal) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.UnidadVenta_INDEX);

        // Email del responsable autenticado en la sesión
        String mail = principal.getName();

        // Consulta al servicio que devuelve Optional<UnidadVenta>
        Optional<UnidadVenta> optionalUnidad = unidadVentaService.findByResponsable(mail);

        // Si la unidad existe, la envuelve en una lista; si no, envía una lista vacía
        List<UnidadVenta> unidades = optionalUnidad
                .map(List::of)
                .orElseGet(Collections::emptyList);

        mAV.addObject("unidadVentas", unidades);

        return mAV;
    }


    @GetMapping("/nuevo/food-truck")
    @PreAuthorize("isAnonymous()")
    public ModelAndView nuevoFoodTruck() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.UnidadVenta_NEW_FOOD_TRUCK);
        mAV.addObject(UNIDAD_VENTA_ATTR, new FoodTruckDTO());
        return mAV;
    }

    @GetMapping("/nuevo/puesto-desarmable")
    @PreAuthorize("isAnonymous()")
    public ModelAndView nuevoPuestoDesarmable() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.UnidadVenta_NEW_PUESTO);
        mAV.addObject(UNIDAD_VENTA_ATTR, new PuestoDesarmableDTO());
        return mAV;
    }

    @PostMapping("/guardar/food-truck")
    @PreAuthorize("isAnonymous()")
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
    @PreAuthorize("isAnonymous()")
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