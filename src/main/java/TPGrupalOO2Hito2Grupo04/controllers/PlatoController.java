package TPGrupalOO2Hito2Grupo04.controllers;

import TPGrupalOO2Hito2Grupo04.dtos.PlatoDTO;
import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.entities.Plato;
import TPGrupalOO2Hito2Grupo04.entities.Staff;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.repositories.IStaffRepository;
import TPGrupalOO2Hito2Grupo04.services.IPlatoService;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;
import TPGrupalOO2Hito2Grupo04.services.IFestivalService;
import TPGrupalOO2Hito2Grupo04.services.IPedidoService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/plato")
@PreAuthorize("hasAuthority('RESPONSABLE')") // Limitado exclusivamente a Responsables
public class PlatoController {

    private static final String PLATO_ATTR = "platoDTO";

    private final IPlatoService platoService;
    private final IUnidadVentaService unidadVentaService;
    private final IStaffRepository staffRepository;
    private final ModelMapper modelMapper;
    private final IFestivalService festivalService;
    private final IPedidoService pedidoService;

    public PlatoController(IPlatoService platoService,
                           IUnidadVentaService unidadVentaService,
                           IStaffRepository staffRepository,
                           ModelMapper modelMapper,
                           IFestivalService festivalService,
                           IPedidoService pedidoService) {
        this.platoService = platoService;
        this.unidadVentaService = unidadVentaService;
        this.staffRepository = staffRepository;
        this.modelMapper = modelMapper;
        this.festivalService = festivalService;
        this.pedidoService = pedidoService;
    }

    // Mostrar la tabla con los platos de la unidad del Responsable
    // Mostrar la tabla con los platos de la unidad del Responsable
    @GetMapping({"", "/"})
    public ModelAndView index(
            @RequestParam(value = "idFestival", required = false) Long idFestival) {

        ModelAndView mAV = new ModelAndView("plato/index");

        // 1. Obtener usuario logueado
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Staff staff = staffRepository.findByUsuarioMail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Staff no encontrado para el usuario actual"));

        UnidadVenta unidadVenta = staff.getUnidadVenta();
        Festival festival = unidadVenta.getFestival();

        // 2. Filtrar los platos exclusivamente de la unidad del responsable
        List<Plato> platos = platoService.traerTodos().stream()
                .filter(p -> p.getUnidadVenta() != null && p.getUnidadVenta().getIdUnidadVenta() == unidadVenta.getIdUnidadVenta())
                .toList();

        mAV.addObject("platos", platos);
        mAV.addObject("unidadVenta", unidadVenta);
        mAV.addObject("festival", festival);

        mAV.addObject("festivales", festivalService.getAll());

        if (idFestival != null) {
            PlatoDTO platoEstrella = pedidoService.encontrarPlatoEstrella(unidadVenta.getIdUnidadVenta(), idFestival);
            mAV.addObject("platoEstrella", platoEstrella);
            mAV.addObject("festivalSeleccionadoNombre", festival.getNombre());
            mAV.addObject("idFestivalSeleccionado", idFestival);
            mAV.addObject("busquedaRealizada", true);
        }

        return mAV;
    }

    // Formulario para dar de alta un nuevo plato (Ya no requiere lista de unidades)
    @GetMapping("/nuevo")
    public ModelAndView nuevoPlato() {
        ModelAndView mAV = new ModelAndView("plato/new");

        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Staff staff = staffRepository.findByUsuarioMail(emailUsuario).orElseThrow();

        mAV.addObject(PLATO_ATTR, new PlatoDTO());
        mAV.addObject("unidadVenta", staff.getUnidadVenta());
        mAV.addObject("festival", staff.getUnidadVenta().getFestival());

        return mAV;
    }

    // Guardar o registrar el plato
    @PostMapping("/guardar")
    public ModelAndView guardarPlato(@ModelAttribute(PLATO_ATTR) PlatoDTO platoDTO) {
        ModelAndView mAV = new ModelAndView();
        try {
            // El servicio ya se encarga de asignar la unidad automáticamente por sesión si el DTO viene sin ID de unidad
            Plato platoRegistrado = platoService.registrarPlato(platoDTO);
            mAV.setViewName("plato/success");
            mAV.addObject("plato", platoRegistrado);
        } catch (IllegalArgumentException e) {
            mAV.setViewName("plato/new");
            String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
            Staff staff = staffRepository.findByUsuarioMail(emailUsuario).orElseThrow();

            mAV.addObject(PLATO_ATTR, platoDTO);
            mAV.addObject("unidadVenta", staff.getUnidadVenta());
            mAV.addObject("festival", staff.getUnidadVenta().getFestival());
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
            mAV.addObject("unidadVenta", plato.getUnidadVenta());
            mAV.addObject("festival", plato.getUnidadVenta() != null ? plato.getUnidadVenta().getFestival() : null);
        } else {
            mAV.addObject(PLATO_ATTR, new PlatoDTO());
            mAV.addObject("error", "El plato solicitado no existe.");
        }

        return mAV;
    }

    // Modificar un plato existente.
    @PostMapping("/actualizar")
    public ModelAndView actualizarPlato(@ModelAttribute(PLATO_ATTR) PlatoDTO platoDTO) {
        ModelAndView mAV = new ModelAndView();
        try {
            Plato platoActualizado = platoService.registrarPlato(platoDTO);
            mAV.setViewName("plato/success-update");
            mAV.addObject("plato", platoActualizado);
        } catch (IllegalArgumentException e) {
            mAV.setViewName("plato/edit");
            mAV.addObject(PLATO_ATTR, platoDTO);

            Optional<Plato> platoOpt = platoService.traerPorId(platoDTO.getIdPlato());
            platoOpt.ifPresent(p -> {
                mAV.addObject("unidadVenta", p.getUnidadVenta());
                mAV.addObject("festival", p.getUnidadVenta() != null ? p.getUnidadVenta().getFestival() : null);
            });

            mAV.addObject("error", e.getMessage());
        }
        return mAV;
    }
}