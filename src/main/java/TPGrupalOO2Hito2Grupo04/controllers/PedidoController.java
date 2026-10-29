package TPGrupalOO2Hito2Grupo04.controllers;

import TPGrupalOO2Hito2Grupo04.dtos.PedidoDTO;
import TPGrupalOO2Hito2Grupo04.dtos.ItemPedidoDTO;
import TPGrupalOO2Hito2Grupo04.entities.Pedido;
import TPGrupalOO2Hito2Grupo04.services.IPedidoService;
import TPGrupalOO2Hito2Grupo04.services.IPlatoService;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final IUnidadVentaService unidadVentaService;

    public PedidoController(IPedidoService pedidoService,
                            IPlatoService platoService,
                            IUnidadVentaService unidadVentaService) {
        this.pedidoService = pedidoService;
        this.platoService = platoService;
        this.unidadVentaService = unidadVentaService;
    }

    // Listado principal de pedidos
    @GetMapping("")
    public String listarPedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.traerTodo());
        return "pedido/index";
    }

    // Mostrar el formulario para dar de alta un nuevo pedido
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("pedidoDTO", new PedidoDTO());
        model.addAttribute("platos", platoService.traerTodos());
        model.addAttribute("unidadesVenta", unidadVentaService.getAll());
        return "pedido/new";
    }

    // Procesar el guardado del pedido
    @PostMapping("/guardar")
    public String guardarPedido(@ModelAttribute("pedidoDTO") PedidoDTO pedidoDTO,
                                RedirectAttributes redirectAttributes) {
        try {
            pedidoService.insertarPedido(pedidoDTO);
            redirectAttributes.addFlashAttribute("exito", "¡Pedido registrado correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo registrar el pedido: " + e.getMessage());
        }
        return "redirect:/pedido";
    }

    // Mostrar formulario de modificación cargando el pedido existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Optional<Pedido> pedidoOpt = pedidoService.traerPorId(id);
        if (pedidoOpt.isEmpty()) {
            return "redirect:/pedido?error=noencontrado";
        }

        Pedido pedido = pedidoOpt.get();

        // Se mapea la entidad a un DTO para la vista
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setIdPedido(pedido.getIdPedido());
        pedidoDTO.setFechaTransaccion(pedido.getFechaTransaccion());
        pedidoDTO.setIdUnidadVenta(pedido.getUnidadVenta().getIdUnidadVenta());

        // Se mapean los ítems existentes al DTO
        List<ItemPedidoDTO> itemDTOs = pedido.getItems().stream().map(item -> {
            ItemPedidoDTO iDto = new ItemPedidoDTO();
            iDto.setIdPlato(item.getPlato().getIdPlato());
            iDto.setCantidad(item.getCantidad());
            return iDto;
        }).collect(Collectors.toList());

        pedidoDTO.setItems(itemDTOs);

        model.addAttribute("pedidoDTO", pedidoDTO);
        model.addAttribute("unidadesVenta", unidadVentaService.getAll());
        model.addAttribute("platos", platoService.traerTodos());

        return "pedido/new";
    }

    // Proceso de actualización
    @PostMapping("/actualizar/{id}")
    public String actualizarPedido(@PathVariable("id") Long id, @ModelAttribute("pedidoDTO") PedidoDTO pedidoDTO) {
        try {
            pedidoService.actualizarPedido(id, pedidoDTO);
            return "redirect:/pedido?exito=actualizado";
        } catch (Exception e) {
            return "redirect:/pedido/editar/" + id + "?error=" + e.getMessage();
        }
    }

    // Eliminacion
    @GetMapping("/eliminar/{id}")
    public String eliminarPedido(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean eliminado = pedidoService.eliminar(id);
            if (eliminado) {
                redirectAttributes.addFlashAttribute("exito", "¡Pedido eliminado correctamente!");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo encontrar el pedido a eliminar.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el pedido: " + e.getMessage());
        }
        return "redirect:/pedido";
    }
}