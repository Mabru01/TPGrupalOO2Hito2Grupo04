package TPGrupalOO2Hito2Grupo04.controllers;

import TPGrupalOO2Hito2Grupo04.entities.Cajero;
import TPGrupalOO2Hito2Grupo04.entities.Pedido;
import TPGrupalOO2Hito2Grupo04.entities.Usuario;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.repositories.ICajeroRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IPedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@PreAuthorize("hasAnyAuthority('EMPLEADO', 'ROLE_EMPLEADO')")
public class CierreCajaController {

    @Autowired
    private ICajeroRepository cajeroRepository;

    @Autowired
    private IPedidoRepository pedidoRepository;

    private Cajero cajeroLogueado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String mail = auth.getName();
        Cajero cajero = cajeroRepository.findByUsuarioMailWithUnidadVenta(mail)
                .orElseThrow(() -> new AccessDeniedException("Solo los cajeros pueden cerrar caja"));

        if (!(cajero instanceof Cajero)) {
            throw new RuntimeException("Solo los cajeros pueden cerrar caja");
        }

        return cajero;
    }

    @GetMapping("/cierreCaja")
    public String index(Model model) {
        Cajero cajero = cajeroLogueado();
        model.addAttribute("cajero", cajero);
        return ViewRouteHelper.CIERRE_CAJA_INDEX;
    }

    @GetMapping("/cierreCaja/nuevo")
    public String nuevo(@RequestParam(required = false) LocalDate fecha, Model model) {
        Cajero cajero = cajeroLogueado();

        LocalDate fechaSeleccionada = fecha != null ? fecha : LocalDate.now();
        model.addAttribute("fecha", fechaSeleccionada);
        model.addAttribute("cajero", cajero);

        List<Pedido> pedidos = pedidoRepository.findPedidosDeUnidadEntreFechas(
                cajero.getUnidadVenta().getIdUnidadVenta(),
                fechaSeleccionada,
                fechaSeleccionada
        );

        double totalRecaudado = pedidos.stream()
                .mapToDouble(Pedido::getPrecioTotal)
                .sum();

        double promedioPorPedido = pedidos.isEmpty() ? 0.0 : totalRecaudado / pedidos.size();

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("cantidadPedidos", pedidos.size());
        model.addAttribute("totalRecaudado", totalRecaudado);
        model.addAttribute("promedioPorPedido", promedioPorPedido);

        return ViewRouteHelper.CIERRE_CAJA_NUEVO;
    }

    @PostMapping("/cierreCaja/cerrar")
    public String cerrar(@RequestParam LocalDate fecha, Model model) {
        Cajero cajero = cajeroLogueado();

        List<Pedido> pedidos = pedidoRepository.findPedidosDeUnidadEntreFechas(
                cajero.getUnidadVenta().getIdUnidadVenta(),
                fecha,
                fecha
        );

        double totalRecaudado = pedidos.stream()
                .mapToDouble(Pedido::getPrecioTotal)
                .sum();

        double promedioPorPedido = pedidos.isEmpty() ? 0.0 : totalRecaudado / pedidos.size();

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("cajero", cajero);
        resumen.put("fecha", fecha);
        resumen.put("cantidadPedidos", pedidos.size());
        resumen.put("totalRecaudado", totalRecaudado);
        resumen.put("promedioPorPedido", promedioPorPedido);
        resumen.put("pedidos", pedidos);

        model.addAttribute("resumen", resumen);
        model.addAttribute("exito", true);

        return ViewRouteHelper.CIERRE_CAJA_RESULTADO;
    }
}
