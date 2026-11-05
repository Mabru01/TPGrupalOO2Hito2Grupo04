package TPGrupalOO2Hito2Grupo04.controllers;

import TPGrupalOO2Hito2Grupo04.entities.Cocinero;
import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.entities.Pedido;
import TPGrupalOO2Hito2Grupo04.entities.Plato;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.repositories.ICocineroRepository;
import TPGrupalOO2Hito2Grupo04.services.IPedidoService;
import TPGrupalOO2Hito2Grupo04.services.IPlatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cocinero")
@PreAuthorize("hasAnyAuthority('EMPLEADO', 'ROLE_EMPLEADO')")
public class CocineroController {

    @Autowired
    private ICocineroRepository cocineroRepository;

    @Autowired
    private IPedidoService pedidoService;

    @Autowired
    private IPlatoService platoService;

    // Se trae el cocinero logueado mediante su mail de la sesion-
    private Cocinero cocineroLogueado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mail = auth.getName();
        Cocinero cocinero = cocineroRepository.findByUsuarioMailWithUnidadVenta(mail)
                .orElseThrow(() -> new RuntimeException("No se encontró el cocinero asociado a este usuario"));

        if (!(cocinero instanceof Cocinero)) {
            throw new RuntimeException("Solo los cocineros pueden acceder a esta sección");
        }

        return cocinero;
    }

    // Se muestra la vista con los platos de la unidad del cocinero
    @GetMapping("/platos")
    public String listarPlatosCocinero(Model model) {
        Cocinero cocinero = cocineroLogueado();
        UnidadVenta unidadVenta = cocinero.getUnidadVenta();
        Festival festival = unidadVenta != null ? unidadVenta.getFestival() : null;

        List<Plato> platosUnidad = platoService.traerTodos().stream()
                .filter(p -> p.getUnidadVenta() != null && unidadVenta != null && p.getUnidadVenta().getIdUnidadVenta() == unidadVenta.getIdUnidadVenta())
                .filter(p -> p.getActivo() == null || p.getActivo() == true)
                .collect(Collectors.toList());

        model.addAttribute("platos", platosUnidad);
        model.addAttribute("unidadVenta", unidadVenta);
        model.addAttribute("festival", festival);
        model.addAttribute("cajero", cocinero);

        return ViewRouteHelper.COCINERO_PLATOS;
    }

    // Listado de pedidos que le corresponden a la cocina de la unidad
    @GetMapping("/pedidos")
    public String listarPedidosCocinero(Model model) {
        Cocinero cocinero = cocineroLogueado();
        UnidadVenta unidadVenta = cocinero.getUnidadVenta();
        Festival festival = unidadVenta != null ? unidadVenta.getFestival() : null;

        List<Pedido> pedidosUnidad = pedidoService.traerTodo().stream()
                .filter(p -> p.getUnidadVenta() != null && p.getUnidadVenta().getIdUnidadVenta() == unidadVenta.getIdUnidadVenta())
                .collect(Collectors.toList());

        model.addAttribute("pedidos", pedidosUnidad);
        model.addAttribute("unidadVenta", unidadVenta);
        model.addAttribute("festival", festival);
        model.addAttribute("cajero", cocinero);

        return ViewRouteHelper.COCINERO_PEDIDOS;
    }

    // Se cambia el estado del pedido.
    @GetMapping("/pedidos/cambiar-estado/{id}")
    public String cambiarEstadoPedido(@PathVariable("id") Long id, RedirectAttributes redirect) {
        try {
            pedidoService.cambiarEstado(id);
            redirect.addFlashAttribute("exito", "El estado del pedido fue actualizado exitosamente.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "No se pudo actualizar el estado del pedido.");
        }

        return ViewRouteHelper.COCINERO_PEDIDOS_REDIRECT;
    }
}