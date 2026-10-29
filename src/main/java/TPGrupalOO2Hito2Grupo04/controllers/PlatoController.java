package TPGrupalOO2Hito2Grupo04.controllers;

import TPGrupalOO2Hito2Grupo04.dtos.PlatoDTO;
import TPGrupalOO2Hito2Grupo04.entities.Plato;
import TPGrupalOO2Hito2Grupo04.services.IPlatoService;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;
import TPGrupalOO2Hito2Grupo04.services.IFestivalService;
import TPGrupalOO2Hito2Grupo04.services.IPedidoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/plato")
public class PlatoController {

    private static final String PLATO_ATTR = "platoDTO";

    private final IPlatoService platoService;
    private final IUnidadVentaService unidadVentaService;
    private final ModelMapper modelMapper;
    private final IFestivalService festivalService;
    private final IPedidoService pedidoService;

    public PlatoController(IPlatoService platoService,
                           IUnidadVentaService unidadVentaService,
                           ModelMapper modelMapper,
                           IFestivalService festivalService,
                           IPedidoService pedidoService) {
        this.platoService = platoService;
        this.unidadVentaService = unidadVentaService;
        this.modelMapper = modelMapper;
        this.festivalService = festivalService;
        this.pedidoService = pedidoService;
    }

    // Mostrar la tabla con todos los platos
    @GetMapping({"", "/"})
    public ModelAndView index(
            @RequestParam(value = "idUnidadVenta", required = false) Long idUnidadVenta,
            @RequestParam(value = "idFestival", required = false) Long idFestival) {

        ModelAndView mAV = new ModelAndView("plato/index");

        mAV.addObject("platos", platoService.traerTodos());
        mAV.addObject("unidadesVenta", unidadVentaService.getAll());
        mAV.addObject("festivales", festivalService.getAll());

        if (idUnidadVenta != null && idFestival != null) {
            // Llamamos al metodo original que devuelve el PlatoDTO
            PlatoDTO platoEstrella = pedidoService.encontrarPlatoEstrella(idUnidadVenta, idFestival);

            mAV.addObject("platoEstrella", platoEstrella);

            unidadVentaService.findById(idUnidadVenta).ifPresent(uv -> {
                String nombreUV = uv.getNombreComercial() != null ? uv.getNombreComercial() : "Unidad sin nombre";
                mAV.addObject("unidadSeleccionadaTexto", nombreUV);
            });

            // Busqueda de el nombre del festival asociado
            festivalService.findById(idFestival).ifPresent(f ->
                    mAV.addObject("festivalSeleccionadoNombre", f.getNombre())
            );

            mAV.addObject("idUnidadSeleccionada", idUnidadVenta);
            mAV.addObject("idFestivalSeleccionado", idFestival);
            mAV.addObject("busquedaRealizada", true);
        }

        return mAV;
    }

    // Formulario para dar de alta un nuevo plato
    @GetMapping("/nuevo")
    public ModelAndView nuevoPlato() {
        ModelAndView mAV = new ModelAndView("plato/new");
        mAV.addObject(PLATO_ATTR, new PlatoDTO());
        mAV.addObject("unidadesVenta", unidadVentaService.getAll());
        return mAV;
    }

    // Guardar o registrar el plato
    @PostMapping("/guardar")
    public ModelAndView guardarPlato(@ModelAttribute(PLATO_ATTR) PlatoDTO platoDTO) {
        ModelAndView mAV = new ModelAndView();
        try {
            Plato platoRegistrado = platoService.registrarPlato(platoDTO);
            mAV.setViewName("plato/success");
            mAV.addObject("plato", platoRegistrado);
        } catch (IllegalArgumentException e) {
            mAV.setViewName("plato/new");
            mAV.addObject(PLATO_ATTR, platoDTO);
            mAV.addObject("unidadesVenta", unidadVentaService.getAll());
            mAV.addObject("error", e.getMessage());
        }
        return mAV;
    }

    // Eliminar un plato por ID
    @GetMapping("/eliminar/{id}")
    public String eliminarPlato(@PathVariable("id") Long id, RedirectAttributes redirect) {
        boolean eliminado = platoService.eliminar(id);
        if (eliminado) {
            redirect.addFlashAttribute("exito", "Plato eliminado correctamente.");
        } else {
            redirect.addFlashAttribute("error", "No se pudo eliminar el plato.");
        }
        return "redirect:/plato";
    }

    // Carga de datos para modificar.
    @GetMapping("/editar/{id}")
    public ModelAndView editarPlato(@PathVariable("id") Long id) {
        ModelAndView mAV = new ModelAndView("plato/edit");

        Optional<Plato> platoOpt = platoService.traerPorId(id);
        if (platoOpt.isPresent()) {
            Plato plato = platoOpt.get();
            PlatoDTO platoDTO = modelMapper.map(plato, PlatoDTO.class);
            if (plato.getUnidadVenta() != null) {
                platoDTO.setIdUnidadVenta(plato.getUnidadVenta().getIdUnidadVenta());
            }
            mAV.addObject(PLATO_ATTR, platoDTO);
        } else {
            mAV.addObject(PLATO_ATTR, new PlatoDTO());
            mAV.addObject("error", "El plato solicitado no existe.");
        }

        mAV.addObject("unidadesVenta", unidadVentaService.getAll());
        return mAV;
    }

    // Modificar un plato existente.
    @PostMapping("/actualizar")
    public ModelAndView actualizarPlato(@ModelAttribute(PLATO_ATTR) PlatoDTO platoDTO) {
        ModelAndView mAV = new ModelAndView();
        try {
            // Se reutiliza logica de guardado.
            Plato platoActualizado = platoService.registrarPlato(platoDTO);
            mAV.setViewName("plato/success-update");
            mAV.addObject("plato", platoActualizado);
        } catch (IllegalArgumentException e) {
            mAV.setViewName("plato/edit");
            mAV.addObject(PLATO_ATTR, platoDTO);
            mAV.addObject("unidadesVenta", unidadVentaService.getAll());
            mAV.addObject("error", e.getMessage());
        }
        return mAV;
    }
}
