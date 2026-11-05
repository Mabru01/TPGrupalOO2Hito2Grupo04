package TPGrupalOO2Hito2Grupo04.controllers;

import java.time.LocalDate;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import TPGrupalOO2Hito2Grupo04.dtos.FestivalDTO;
import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.services.IFestivalService;

// El administrador da de alta festivales con sus costos, ve el detalle de cada uno y los modifica
@Controller
@RequestMapping("/festival")
@PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN')")
public class FestivalController {

    private static final String FESTIVAL_ATTR = "festivalDTO";
    private static final String ERROR_FORMATO = "Revisá los datos: hay fechas o montos con un formato no válido.";

    private final IFestivalService festivalService;

    public FestivalController(IFestivalService festivalService) {
        this.festivalService = festivalService;
    }

    @GetMapping("/nuevo")
    public ModelAndView nuevo() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.FESTIVAL_FORM);
        mAV.addObject(FESTIVAL_ATTR, festivalService.nuevoConCostosSugeridos());
        return mAV;
    }

    @PostMapping("/guardar")
    public ModelAndView guardar(@ModelAttribute(FESTIVAL_ATTR) FestivalDTO festivalDTO, BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException(ERROR_FORMATO);
            }
            Festival festival = festivalService.crearFestivalConCostos(festivalDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Se creó el festival " + festival.getNombre() + ".");
            return new ModelAndView("redirect:" + ViewRouteHelper.ROUTE_INDEX);
        } catch (IllegalArgumentException e) {
            ModelAndView mAV = new ModelAndView(ViewRouteHelper.FESTIVAL_FORM);
            mAV.addObject("error", e.getMessage());
            return mAV;
        }
    }

    //GET Example: SERVER/festival/1
    @GetMapping("/{id}")
    public ModelAndView detalle(@PathVariable long id) {
        try {
            Festival festival = festivalService.findByIdWithUnidades(id);
            ModelAndView mAV = new ModelAndView(ViewRouteHelper.FESTIVAL_DETAIL);
            mAV.addObject("festival", festival);
            mAV.addObject("hoy", LocalDate.now());
            mAV.addObject("puedeModificar", !festival.yaTermino(LocalDate.now()));
            return mAV;
        } catch (IllegalArgumentException e) {
            return new ModelAndView("redirect:" + ViewRouteHelper.ROUTE_INDEX);
        }
    }

    @GetMapping("/{id}/modificar")
    public ModelAndView modificar(@PathVariable long id) {
        Festival festival = festivalService.findById(id).orElse(null);
        // Un festival que no existe o que ya terminó no se modifica
        if (festival == null || festival.yaTermino(LocalDate.now())) {
            return new ModelAndView("redirect:" + ViewRouteHelper.ROUTE_INDEX);
        }
        ModelAndView mAV = vistaModificar(festival);
        mAV.addObject(FESTIVAL_ATTR, festivalService.datosParaModificar(id));
        return mAV;
    }

    @PostMapping("/{id}/modificar")
    public ModelAndView actualizar(@PathVariable long id, @ModelAttribute(FESTIVAL_ATTR) FestivalDTO festivalDTO,
                                   BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        Festival festival = festivalService.findById(id).orElse(null);
        if (festival == null) {
            return new ModelAndView("redirect:" + ViewRouteHelper.ROUTE_INDEX);
        }
        try {
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException(ERROR_FORMATO);
            }
            festivalService.modificarFestival(id, festivalDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Se guardaron los cambios del festival.");
            return new ModelAndView("redirect:" + ViewRouteHelper.FESTIVAL_ROOT + "/" + id);
        } catch (IllegalArgumentException e) {
            ModelAndView mAV = vistaModificar(festival);
            mAV.addObject("error", e.getMessage());
            return mAV;
        }
    }

    // El mismo formulario del alta, pero apuntando a la modificación de este festival
    private ModelAndView vistaModificar(Festival festival) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.FESTIVAL_FORM);
        mAV.addObject("idFestival", festival.getIdFestival());
        mAV.addObject("inicioBloqueado", festival.yaEmpezo(LocalDate.now()));
        return mAV;
    }
}
