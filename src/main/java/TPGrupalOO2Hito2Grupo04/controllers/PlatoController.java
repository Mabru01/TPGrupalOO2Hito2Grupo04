package TPGrupalOO2Hito2Grupo04.controllers;

import TPGrupalOO2Hito2Grupo04.dtos.PlatoDTO;
import TPGrupalOO2Hito2Grupo04.entities.Plato;
import TPGrupalOO2Hito2Grupo04.services.IPlatoService;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/plato")
public class PlatoController {

    private static final String PLATO_ATTR = "platoDTO";

    private final IPlatoService platoService;
    private final IUnidadVentaService unidadVentaService;

    public PlatoController(IPlatoService platoService, IUnidadVentaService unidadVentaService) {
        this.platoService = platoService;
        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping("")
    public ModelAndView index() {
        ModelAndView mAV = new ModelAndView("plato/index");
        mAV.addObject("platos", platoService.getAll());
        return mAV;
    }

    @GetMapping("/nuevo")
    public ModelAndView nuevoPlato() {
        ModelAndView mAV = new ModelAndView("plato/new");
        mAV.addObject(PLATO_ATTR, new PlatoDTO());
        mAV.addObject("unidadesVenta", unidadVentaService.getAll()); // Para el desplegable (select)
        return mAV;
    }

    @PostMapping("/guardar")
    public ModelAndView guardarPlato(@ModelAttribute(PLATO_ATTR) PlatoDTO platoDTO) {
        ModelAndView mAV = new ModelAndView();
        try {
            Plato platoRegistrado = platoService.registrarPlato(platoDTO);
            mAV.setViewName("plato/exito");
            mAV.addObject("plato", platoRegistrado);
        } catch (IllegalArgumentException e) {
            // Si hay error (ej: nombre duplicado), volvemos al formulario
            mAV.setViewName("plato/new");
            mAV.addObject(PLATO_ATTR, platoDTO);
            mAV.addObject("unidadesVenta", unidadVentaService.getAll()); // Recargamos el select para que no se rompa.
            mAV.addObject("error", e.getMessage()); // Se muestra mensaje del error.
        }
        return mAV;
    }
}
