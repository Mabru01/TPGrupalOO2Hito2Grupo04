package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TPGrupalOO2Hito2Grupo04.dtos.ReporteVentaDTO;
import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.entities.ItemPedido;
import TPGrupalOO2Hito2Grupo04.entities.Pedido;
import TPGrupalOO2Hito2Grupo04.entities.Plato;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.repositories.IPedidoRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IUnidadVentaRepository;
import TPGrupalOO2Hito2Grupo04.services.IReporteVentaService;

@Service("reporteVentaService")
public class ReporteVentaService implements IReporteVentaService {

    private final IPedidoRepository pedidoRepository;
    private final IUnidadVentaRepository unidadVentaRepository;

    public ReporteVentaService(IPedidoRepository pedidoRepository, IUnidadVentaRepository unidadVentaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.unidadVentaRepository = unidadVentaRepository;
    }

    // Recaudación = suma de (cantidad × precio del plato) de los pedidos de la unidad
    // hechos entre el inicio y el fin de su festival
    @Override
    @Transactional(readOnly = true)
    public ReporteVentaDTO generarReporte(long idUnidadVenta) {
        UnidadVenta unidad = unidadVentaRepository.findById(idUnidadVenta).orElseThrow(
                () -> new IllegalArgumentException("ERROR: Unidad de venta not found with id: " + idUnidadVenta)
        );
        Festival festival = unidad.getFestival();
        List<Pedido> pedidos = pedidoRepository.findPedidosDeUnidadEntreFechas(idUnidadVenta,
                festival.getFechaInicio(), festival.getFechaFin());

        // Se agrupa por plato: cuántos se vendieron y cuánto recaudaron
        Map<Long, Plato> platos = new LinkedHashMap<>();
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (Pedido pedido : pedidos) {
            for (ItemPedido item : pedido.getItems()) {
                Plato plato = item.getPlato();
                platos.putIfAbsent(plato.getIdPlato(), plato);
                cantidades.merge(plato.getIdPlato(), item.getCantidad(), Integer::sum);
            }
        }

        // Primero el total, porque cada plato muestra qué porcentaje representa
        BigDecimal total = BigDecimal.ZERO;
        int platosVendidos = 0;
        for (Long idPlato : platos.keySet()) {
            total = total.add(subtotal(platos.get(idPlato), cantidades.get(idPlato)));
            platosVendidos += cantidades.get(idPlato);
        }

        List<ReporteVentaDTO.VentaPlato> ventasPorPlato = new ArrayList<>();
        for (Long idPlato : platos.keySet()) {
            Plato plato = platos.get(idPlato);
            int cantidad = cantidades.get(idPlato);
            BigDecimal subtotal = subtotal(plato, cantidad);
            BigDecimal porcentaje = total.signum() == 0 ? BigDecimal.ZERO
                    : subtotal.multiply(BigDecimal.valueOf(100)).divide(total, 1, RoundingMode.HALF_UP);
            ventasPorPlato.add(new ReporteVentaDTO.VentaPlato(plato.getNombre(), cantidad, precioDe(plato), subtotal,
                    porcentaje));
        }
        ventasPorPlato.sort(Comparator.comparing(ReporteVentaDTO.VentaPlato::getSubtotal).reversed());

        BigDecimal promedio = pedidos.isEmpty() ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(pedidos.size()), 2, RoundingMode.HALF_UP);

        return new ReporteVentaDTO(festival.getNombre(), festival.getFechaInicio(), festival.getFechaFin(),
                unidad.getNombreComercial(), pedidos.size(), platosVendidos, total.setScale(2, RoundingMode.HALF_UP),
                promedio, ventasPorPlato);
    }

    // El precio es float: se pasa por String para no arrastrar decimales de más (15000.5f -> 15000.5)
    private BigDecimal precioDe(Plato plato) {
        return new BigDecimal(Float.toString(plato.getPrecio()));
    }

    private BigDecimal subtotal(Plato plato, int cantidad) {
        return precioDe(plato).multiply(BigDecimal.valueOf(cantidad));
    }
}
