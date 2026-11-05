package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TPGrupalOO2Hito2Grupo04.dtos.LiquidacionDTO;
import TPGrupalOO2Hito2Grupo04.dtos.ReporteRentabilidadDTO;
import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.entities.ItemPedido;
import TPGrupalOO2Hito2Grupo04.entities.Pedido;
import TPGrupalOO2Hito2Grupo04.entities.Plato;
import TPGrupalOO2Hito2Grupo04.entities.Staff;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.repositories.IPedidoRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IUnidadVentaRepository;
import TPGrupalOO2Hito2Grupo04.services.ILiquidacionService;
import TPGrupalOO2Hito2Grupo04.services.IRentabilidadService;

@Service("rentabilidadService")
public class RentabilidadService implements IRentabilidadService {

    private final IPedidoRepository pedidoRepository;
    private final IUnidadVentaRepository unidadVentaRepository;
    private final ILiquidacionService liquidacionService;

    public RentabilidadService(IPedidoRepository pedidoRepository, IUnidadVentaRepository unidadVentaRepository,
                               ILiquidacionService liquidacionService) {
        this.pedidoRepository = pedidoRepository;
        this.unidadVentaRepository = unidadVentaRepository;
        this.liquidacionService = liquidacionService;
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteRentabilidadDTO calcularRentabilidad(long idUnidadVenta) {
        Festival festival = traerUnidad(idUnidadVenta).getFestival();
        return calcularRentabilidadEntreFechas(idUnidadVenta, festival.getFechaInicio(), festival.getFechaFin());
    }

    // Rentabilidad neta = (ventas - costo de los platos) - sueldos - canon.
    // Sueldos y canon son del festival completo: en un período se toma la parte proporcional a sus días
    @Override
    @Transactional(readOnly = true)
    public ReporteRentabilidadDTO calcularRentabilidadEntreFechas(long idUnidadVenta, LocalDate desde, LocalDate hasta) {
        UnidadVenta unidad = traerUnidad(idUnidadVenta);
        Festival festival = unidad.getFestival();
        validarPeriodo(festival, desde, hasta);

        int diasFestival = festival.getDuracionEnDias();
        int diasPeriodo = (int) ChronoUnit.DAYS.between(desde, hasta) + 1;

        // Ventas y costo de los platos de los pedidos hechos en el período
        List<Pedido> pedidos = pedidoRepository.findPedidosDeUnidadEntreFechas(idUnidadVenta, desde, hasta);
        BigDecimal ventas = BigDecimal.ZERO;
        BigDecimal costoPlatos = BigDecimal.ZERO;
        for (Pedido pedido : pedidos) {
            for (ItemPedido item : pedido.getItems()) {
                BigDecimal cantidad = BigDecimal.valueOf(item.getCantidad());
                ventas = ventas.add(precioDe(item.getPlato()).multiply(cantidad));
                costoPlatos = costoPlatos.add(costoDe(item.getPlato()).multiply(cantidad));
            }
        }

        // Sueldos: la liquidación de cada empleado que trabajó en el festival
        List<ReporteRentabilidadDTO.SueldoEmpleado> sueldosPorEmpleado = new ArrayList<>();
        BigDecimal sueldos = BigDecimal.ZERO;
        for (Staff staff : liquidacionService.getEmpleadosDeUnidad(idUnidadVenta)) {
            if (!trabajoEnElFestival(staff, festival)) {
                continue;
            }
            LiquidacionDTO liquidacion = liquidacionService.liquidar(staff.getIdStaff(), festival.getIdFestival());
            BigDecimal sueldoPeriodo = proporcional(liquidacion.getTotal(), diasPeriodo, diasFestival);
            sueldosPorEmpleado.add(new ReporteRentabilidadDTO.SueldoEmpleado(liquidacion.getEmpleado(),
                    liquidacion.getTipo(), liquidacion.getTotal(), sueldoPeriodo));
            sueldos = sueldos.add(sueldoPeriodo);
        }

        BigDecimal canon = proporcional(BigDecimal.valueOf(unidad.calcularCanon()), diasPeriodo, diasFestival);

        BigDecimal gananciaBruta = ventas.subtract(costoPlatos);
        BigDecimal rentabilidadNeta = gananciaBruta.subtract(sueldos).subtract(canon);
        BigDecimal margen = ventas.signum() == 0 ? BigDecimal.ZERO
                : rentabilidadNeta.multiply(BigDecimal.valueOf(100)).divide(ventas, 1, RoundingMode.HALF_UP);

        boolean festivalCompleto = desde.equals(festival.getFechaInicio()) && hasta.equals(festival.getFechaFin());

        return new ReporteRentabilidadDTO(festival.getNombre(), unidad.getNombreComercial(),
                festival.getFechaInicio(), festival.getFechaFin(), desde, hasta, diasPeriodo, diasFestival,
                festivalCompleto, pedidos.size(), redondear(ventas), redondear(costoPlatos), redondear(gananciaBruta),
                redondear(sueldos), canon, redondear(rentabilidadNeta), margen, sueldosPorEmpleado);
    }

    private UnidadVenta traerUnidad(long idUnidadVenta) {
        UnidadVenta unidad = unidadVentaRepository.findById(idUnidadVenta).orElseThrow(
                () -> new IllegalArgumentException("ERROR: Unidad de venta not found with id: " + idUnidadVenta)
        );
        if (unidad.getFestival().getCosto() == null) {
            throw new IllegalArgumentException("El festival " + unidad.getFestival().getNombre()
                    + " no tiene costos cargados: no se pueden calcular los sueldos.");
        }
        return unidad;
    }

    // Las dos fechas son obligatorias, en orden y dentro del festival
    private void validarPeriodo(Festival festival, LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Elegí las dos fechas del período.");
        }
        if (hasta.isBefore(desde)) {
            throw new IllegalArgumentException("La fecha hasta no puede ser anterior a la fecha desde.");
        }
        if (desde.isBefore(festival.getFechaInicio()) || hasta.isAfter(festival.getFechaFin())) {
            throw new IllegalArgumentException("Las fechas tienen que estar dentro del festival "
                    + festival.getNombre() + ".");
        }
    }

    // Un empleado dado de baja antes de que empiece el festival no cobra en él
    private boolean trabajoEnElFestival(Staff staff, Festival festival) {
        return staff.getFechaEgreso() == null || !staff.getFechaEgreso().isBefore(festival.getFechaInicio());
    }

    // Precio y costo son float: se pasan por String para no arrastrar decimales de más
    private BigDecimal precioDe(Plato plato) {
        return new BigDecimal(Float.toString(plato.getPrecio()));
    }

    private BigDecimal costoDe(Plato plato) {
        return new BigDecimal(Float.toString(plato.getCosto()));
    }

    // Parte de un monto del festival que corresponde a los días del período
    private BigDecimal proporcional(BigDecimal monto, int diasPeriodo, int diasFestival) {
        return monto.multiply(BigDecimal.valueOf(diasPeriodo))
                .divide(BigDecimal.valueOf(diasFestival), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal redondear(BigDecimal monto) {
        return monto.setScale(2, RoundingMode.HALF_UP);
    }
}
