package TPGrupalOO2Hito2Grupo04.controllers;

import TPGrupalOO2Hito2Grupo04.dtos.FoodTruckDTO;
import TPGrupalOO2Hito2Grupo04.dtos.PuestoDesarmableDTO;
import TPGrupalOO2Hito2Grupo04.entities.FoodTruck;
import TPGrupalOO2Hito2Grupo04.entities.PuestoDesarmable;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.repositories.IUnidadVentaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import java.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/unidadVenta")
public class UnidadVentaController {

    @Autowired
    private IUnidadVentaRepository unidadVentaRepository;

    private static final String UNIDAD_VENTA_ATTR = "unidadVentaDTO";
    private static final String LISTA_UNIDADES_ATTR = "unidadVentas";

    private final IUnidadVentaService unidadVentaService;

    public UnidadVentaController(IUnidadVentaService unidadVentaService) {
        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN')")
    public ModelAndView index(Principal principal, Authentication authentication) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.UnidadVenta_INDEX);
        mAV.addObject(LISTA_UNIDADES_ATTR, unidadVentaService.getAll());
        return mAV;
    }

    @GetMapping("/mi-unidad")
    @PreAuthorize("hasAuthority('RESPONSABLE')")
    public ModelAndView verMiUnidad(Principal principal) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.UnidadVenta_INDEX);

        String mail = principal.getName();
        Optional<UnidadVenta> optionalUnidad = unidadVentaService.findByResponsable(mail);

        List<UnidadVenta> unidades = optionalUnidad
                .map(List::of)
                .orElseGet(Collections::emptyList);

        mAV.addObject(LISTA_UNIDADES_ATTR, unidades);
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
            return ViewRouteHelper.UnidadVenta_NEW_FOOD_TRUCK;
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

    @GetMapping("/modificar")
    @PreAuthorize("hasAuthority('RESPONSABLE')")
    public ModelAndView vistaModificar(Principal principal) {
        ModelAndView mav = new ModelAndView();

        try {
            UnidadVenta unidad = unidadVentaRepository.findByResponsable_Mail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("No tenés unidad de venta asignada"));

            if (!unidad.isActiva()) {
                throw new IllegalStateException("No se puede modificar una unidad de venta que se encuentra dada de baja. Debe reactivarla primero.");
            }

            if (unidad instanceof FoodTruck) {
                mav.setViewName(ViewRouteHelper.UnidadVenta_SET_FOOD_TRUCK);
                mav.addObject("foodTruckDTO", unidad);
            } else if (unidad instanceof PuestoDesarmable) {
                mav.setViewName(ViewRouteHelper.UnidadVenta_SET_PUESTO);
                mav.addObject("puestoDesarmableDTO", unidad);
            }

        } catch (IllegalStateException e) {
            mav.setViewName("redirect:" + ViewRouteHelper.UnidadVenta_MINE + "?error=" + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
        }

        return mav;
    }

    @PostMapping("/actualizar/foodtruck/{id}")
    @PreAuthorize("hasAuthority('RESPONSABLE')")
    public RedirectView actualizarFoodTruck(@PathVariable Long id, @ModelAttribute FoodTruckDTO dto) {
        unidadVentaService.modificarFoodTruck(id, dto);
        return new RedirectView(ViewRouteHelper.UnidadVenta_MINE);
    }

    @PostMapping("/actualizar/puestoDesarmable/{id}")
    @PreAuthorize("hasAuthority('RESPONSABLE')")
    public RedirectView actualizarPuestoDesarmable(@PathVariable Long id, @ModelAttribute PuestoDesarmableDTO dto) {
        unidadVentaService.modificarPuestoDesarmable(id, dto);
        return new RedirectView(ViewRouteHelper.UnidadVenta_MINE);
    }

    @PostMapping("/baja/{id}")
    @PreAuthorize("hasAuthority('RESPONSABLE')")
    public RedirectView darDeBaja(@PathVariable Long id) {
        unidadVentaService.darDeBaja(id);
        return new RedirectView(ViewRouteHelper.UnidadVenta_MINE);
    }

    @PostMapping("/reactivar/{id}")
    @PreAuthorize("hasAuthority('RESPONSABLE')")
    public RedirectView reactivar(@PathVariable Long id) {
        unidadVentaService.reactivar(id);
        return new RedirectView(ViewRouteHelper.UnidadVenta_MINE);
    }

}