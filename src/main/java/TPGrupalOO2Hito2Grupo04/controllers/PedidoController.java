package TPGrupalOO2Hito2Grupo04.controllers;

import TPGrupalOO2Hito2Grupo04.dtos.PedidoDTO;
import TPGrupalOO2Hito2Grupo04.dtos.ItemPedidoDTO;
import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.entities.Pedido;
import TPGrupalOO2Hito2Grupo04.entities.Staff;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.entities.Plato;
import TPGrupalOO2Hito2Grupo04.repositories.IStaffRepository;
import TPGrupalOO2Hito2Grupo04.services.IPedidoService;
import TPGrupalOO2Hito2Grupo04.services.IPlatoService;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pedido")
@PreAuthorize("hasAnyAuthority('RESPONSABLE', 'EMPLEADO')")
public class PedidoController {

    private final IPedidoService pedidoService;
    private final IPlatoService platoService;
    private final IStaffRepository staffRepository;
    private final IUnidadVentaService unidadVentaService;

    public PedidoController(IPedidoService pedidoService,
                            IPlatoService platoService,
                            IStaffRepository staffRepository,
                            IUnidadVentaService unidadVentaService) {
        this.pedidoService = pedidoService;
        this.platoService = platoService;
        this.staffRepository = staffRepository;
        this.unidadVentaService = unidadVentaService;
    }

    // Listado principal de pedidos.
    @GetMapping("")
    public String listarPedidos(Model model) {
        UnidadVenta unidadVenta = unidadDelUsuario();
        Festival festival = unidadVenta != null ? unidadVenta.getFestival() : null;

        List<Pedido> pedidosUnidad = pedidoService.traerTodo().stream()
                .filter(p -> p.getUnidadVenta() != null && p.getUnidadVenta().getIdUnidadVenta() == unidadVenta.getIdUnidadVenta())
                .collect(Collectors.toList());

        model.addAttribute("pedidos", pedidosUnidad);
        model.addAttribute("unidadVenta", unidadVenta);
        model.addAttribute("festival", festival);

        return "pedido/index";
    }

    // Mostrar el formulario para dar de alta un nuevo pedido
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        UnidadVenta unidadVenta = unidadDelUsuario();
        Festival festival = unidadVenta != null ? unidadVenta.getFestival() : null;

        PedidoDTO pedidoDTO = new PedidoDTO();
        if (unidadVenta != null) {
            pedidoDTO.setIdUnidadVenta(unidadVenta.getIdUnidadVenta());
        }

        List<Plato> platosUnidad = platoService.traerTodos().stream()
                .filter(p -> p.getUnidadVenta() != null && unidadVenta != null && p.getUnidadVenta().getIdUnidadVenta() == unidadVenta.getIdUnidadVenta())
                .collect(Collectors.toList());

        model.addAttribute("pedidoDTO", pedidoDTO);
        model.addAttribute("unidadVenta", unidadVenta);
        model.addAttribute("festival", festival);
        model.addAttribute("platos", platosUnidad);

        return "pedido/new";
    }

    // Procesar el guardado del pedido
    @PostMapping("/guardar")
    public String guardarPedido(@ModelAttribute("pedidoDTO") PedidoDTO pedidoDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            pedidoDTO.setIdUnidadVenta(unidadDelUsuario().getIdUnidadVenta());

            pedidoService.insertarPedido(pedidoDTO);
            redirectAttributes.addFlashAttribute("exito", "Pedido registrado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo registrar el pedido: " + e.getMessage());
        }
        return "redirect:/pedido";
    }

    // Mostrar formulario de modificación cargando el pedido existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        if (!esDeMiUnidad(id)) {
            return "redirect:/pedido";
        }
        Optional<Pedido> pedidoOpt = pedidoService.traerPorId(id);
        if (pedidoOpt.isEmpty()) {
            return "redirect:/pedido?error=noencontrado";
        }

        Pedido pedido = pedidoOpt.get();
        UnidadVenta unidadVenta = pedido.getUnidadVenta();
        Festival festival = unidadVenta != null ? unidadVenta.getFestival() : null;

        // Se mapea la entidad a un DTO para la vista
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setIdPedido(pedido.getIdPedido());
        pedidoDTO.setFechaTransaccion(pedido.getFechaTransaccion());
        if (unidadVenta != null) {
            pedidoDTO.setIdUnidadVenta(unidadVenta.getIdUnidadVenta());
        }

        List<ItemPedidoDTO> itemDTOs = pedido.getItems().stream().map(item -> {
            ItemPedidoDTO iDto = new ItemPedidoDTO();
            iDto.setIdPlato(item.getPlato().getIdPlato());
            iDto.setCantidad(item.getCantidad());
            return iDto;
        }).collect(Collectors.toList());

        pedidoDTO.setItems(itemDTOs);

        List<Plato> platosUnidad = platoService.traerTodos().stream()
                .filter(p -> p.getUnidadVenta() != null && unidadVenta != null && p.getUnidadVenta().getIdUnidadVenta() == unidadVenta.getIdUnidadVenta())
                .collect(Collectors.toList());

        model.addAttribute("pedidoDTO", pedidoDTO);
        model.addAttribute("unidadVenta", unidadVenta);
        model.addAttribute("festival", festival);
        model.addAttribute("platos", platosUnidad);

        return "pedido/new";
    }

    // Proceso de actualización.
    @PostMapping("/actualizar/{id}")
    public String actualizarPedido(@PathVariable("id") Long id, @ModelAttribute("pedidoDTO") PedidoDTO pedidoDTO, RedirectAttributes redirectAttributes) {
        if (!esDeMiUnidad(id)) {
            return "redirect:/pedido";
        }
        try {
            pedidoDTO.setIdUnidadVenta(unidadDelUsuario().getIdUnidadVenta());

            pedidoService.actualizarPedido(id, pedidoDTO);
            redirectAttributes.addFlashAttribute("exito", "Pedido actualizado correctamente");
            return "redirect:/pedido";
        } catch (Exception e) {
            return "redirect:/pedido/editar/" + id + "?error=" + e.getMessage();
        }
    }

    // Eliminacion.
    @GetMapping("/eliminar/{id}")
    public String eliminarPedido(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (!esDeMiUnidad(id)) {
            return "redirect:/pedido";
        }
        try {
            boolean eliminado = pedidoService.eliminar(id);
            if (eliminado) {
                redirectAttributes.addFlashAttribute("exito", "Pedido eliminado correctamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo encontrar el pedido a eliminar.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el pedido: " + e.getMessage());
        }
        return "redirect:/pedido";
    }

    // Cambiar el estado
    @GetMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable("id") Long id, RedirectAttributes redirect) {
        if (!esDeMiUnidad(id)) {
            return "redirect:/pedido";
        }
        try {
            pedidoService.cambiarEstado(id);
            redirect.addFlashAttribute("exito", "El estado del pedido fue actualizado.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "No se pudo actualizar el estado del pedido.");
        }
        return "redirect:/pedido";
    }

    // Unidad del usuario logueado: el responsable la que tiene a cargo, el empleado en la que trabaja
    private UnidadVenta unidadDelUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuario = auth.getName();
        boolean esResponsable = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("RESPONSABLE"));

        if (esResponsable) {
            return unidadVentaService.findByResponsable(emailUsuario)
                    .orElseThrow(() -> new RuntimeException("El responsable no tiene una unidad de venta a cargo"));
        }
        return staffRepository.findByUsuarioMail(emailUsuario)
                .map(Staff::getUnidadVenta)
                .orElseThrow(() -> new RuntimeException("El empleado no tiene una unidad de venta asignada"));
    }

    // Verifica que el pedido exista y sea de la unidad del usuario logueado
    private boolean esDeMiUnidad(Long idPedido) {
        long idUnidad = unidadDelUsuario().getIdUnidadVenta();
        return pedidoService.traerPorId(idPedido)
                .map(p -> p.getUnidadVenta() != null && p.getUnidadVenta().getIdUnidadVenta() == idUnidad)
                .orElse(false);
    }
}
