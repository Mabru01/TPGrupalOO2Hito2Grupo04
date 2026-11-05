package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.modelmapper.ModelMapper;

import TPGrupalOO2Hito2Grupo04.dtos.PedidoDTO;
import TPGrupalOO2Hito2Grupo04.dtos.ItemPedidoDTO;
import TPGrupalOO2Hito2Grupo04.dtos.PlatoDTO;
import TPGrupalOO2Hito2Grupo04.entities.Pedido;
import TPGrupalOO2Hito2Grupo04.entities.ItemPedido;
import TPGrupalOO2Hito2Grupo04.entities.Plato;
import TPGrupalOO2Hito2Grupo04.entities.Staff;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.repositories.IPedidoRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IPlatoRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IStaffRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IUnidadVentaRepository;
import TPGrupalOO2Hito2Grupo04.services.IPedidoService;

@Service("pedidoService")
public class PedidoService implements IPedidoService {

    private static final Logger logger =
            LoggerFactory.getLogger(PedidoService.class);

    private final IPedidoRepository pedidoRepository;
    private final IPlatoRepository platoRepository;
    private final IUnidadVentaRepository unidadVentaRepository;
    private final IStaffRepository staffRepository;
    private final ModelMapper modelMapper;

    public PedidoService(IPedidoRepository pedidoRepository,
                         IPlatoRepository platoRepository,
                         IUnidadVentaRepository unidadVentaRepository,
                         IStaffRepository staffRepository,
                         ModelMapper modelMapper) {
        this.pedidoRepository = pedidoRepository;
        this.platoRepository = platoRepository;
        this.unidadVentaRepository = unidadVentaRepository;
        this.staffRepository = staffRepository;
        this.modelMapper = modelMapper;
    }

    // Se traen todos los pedidos con sus items y platos asociados
    @Override
    @Transactional(readOnly = true)
    public List<Pedido> traerTodo() {
        return pedidoRepository.findAllWithItemsAndPlatos();
    }

    // Se busca un pedido por id
    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> traerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    // Se guarda o actualiza un pedido directamente
    @Override
    public Pedido insertarOActualizar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    // Se elimina un pedido controlando excepciones
    @Override
    public boolean eliminar(long id) {
        try {
            pedidoRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException |
                 DataIntegrityViolationException e) {
            logger.warn("No se pudo eliminar el Pedido con id {}", id, e);
            return false;
        } catch (DataAccessException e) {
            logger.error(
                    "Error de base de datos al eliminar el Pedido con id {}", id, e);
            return false;
        }
    }

    // Se inserta un nuevo pedido validando fechas, festivales y que los platos esten activos
    @Override
    @Transactional
    public Pedido insertarPedido(PedidoDTO pedidoDTO) throws Exception {
        try {
            UnidadVenta unidadVenta;

            if (pedidoDTO.getIdUnidadVenta() != null) {
                unidadVenta = unidadVentaRepository.findById(pedidoDTO.getIdUnidadVenta())
                        .orElseThrow(() -> new Exception("Unidad de venta no encontrada con ID: " + pedidoDTO.getIdUnidadVenta()));
            } else {
                String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();

                Staff staff = staffRepository.findByUsuarioMail(emailUsuario)
                        .orElseThrow(() -> new RuntimeException("ERROR: El empleado logueado no tiene un staff/unidad asociada"));

                unidadVenta = staff.getUnidadVenta();
                if (unidadVenta == null) {
                    throw new Exception("ERROR: El empleado logueado no tiene una unidad de venta asignada.");
                }
            }

            LocalDate fechaPedido = pedidoDTO.getFechaTransaccion() != null ? pedidoDTO.getFechaTransaccion() : LocalDate.now();
            LocalDate hoy = LocalDate.now();

            // Validación de fecha pasada
            if (fechaPedido.isBefore(hoy)) {
                throw new Exception("No se pueden registrar pedidos con fechas anteriores al día de hoy.");
            }

            // Validación de rango de fecha del festival
            Festival festival = unidadVenta.getFestival();
            if (festival != null) {
                if (fechaPedido.isBefore(festival.getFechaInicio()) || fechaPedido.isAfter(festival.getFechaFin())) {
                    throw new Exception("La fecha del pedido debe estar dentro del período del festival: "
                            + festival.getNombre() + " (" + festival.getFechaInicio() + " al " + festival.getFechaFin() + ")");
                }
            }

            Pedido pedido = new Pedido();
            pedido.setFechaTransaccion(fechaPedido);
            pedido.setUnidadVenta(unidadVenta);
            pedido.setTerminado(false);

            Pedido pedidoGuardado = pedidoRepository.save(pedido);

            if (pedidoDTO.getItems() != null && !pedidoDTO.getItems().isEmpty()) {
                for (ItemPedidoDTO itemDTO : pedidoDTO.getItems()) {
                    Plato plato = platoRepository.findById(itemDTO.getIdPlato())
                            .orElseThrow(() -> new Exception("Plato no encontrado con ID: " + itemDTO.getIdPlato()));

                    // Para un nuevo pedido no se permiten platos dados de baja
                    if (Boolean.FALSE.equals(plato.getActivo())) {
                        throw new Exception("El plato '" + plato.getNombre() + "' no esta disponible, debe seleccionar un plato activo.");
                    }

                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setCantidad(itemDTO.getCantidad());
                    itemPedido.setPlato(plato);

                    pedido.agregarItem(itemPedido);
                }

                pedidoGuardado = pedidoRepository.save(pedido);
            }

            logger.info("Pedido guardado exitosamente con ID: {}", pedidoGuardado.getIdPedido());
            return pedidoGuardado;

        } catch (Exception e) {
            logger.error("Error al intentar guardar el nuevo pedido", e);
            throw new Exception("Error al procesar el alta del pedido: " + e.getMessage());
        }
    }

    // Se actualiza un pedido existente aplicando validaciones de fechas e items
    @Override
    @Transactional
    public Pedido actualizarPedido(Long id, PedidoDTO pedidoDTO) throws Exception {
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new Exception("Pedido no encontrado con ID: " + id));

        UnidadVenta unidadVenta;
        if (pedidoDTO.getIdUnidadVenta() != null) {
            unidadVenta = unidadVentaRepository.findById(pedidoDTO.getIdUnidadVenta())
                    .orElseThrow(() -> new Exception("Unidad de venta no encontrada con ID: " + pedidoDTO.getIdUnidadVenta()));
        } else {
            String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
            Staff staff = staffRepository.findByUsuarioMail(emailUsuario)
                    .orElseThrow(() -> new RuntimeException("ERROR: El empleado logueado no tiene un staff/unidad asociada"));
            unidadVenta = staff.getUnidadVenta();
        }

        LocalDate fechaPedido = pedidoDTO.getFechaTransaccion();
        LocalDate hoy = LocalDate.now();

        if (fechaPedido != null) {
            if (fechaPedido.isBefore(hoy)) {
                throw new Exception("No se pueden registrar pedidos con fechas anteriores al día de hoy.");
            }
            Festival festival = unidadVenta.getFestival();
            if (festival != null) {
                if (fechaPedido.isBefore(festival.getFechaInicio()) || fechaPedido.isAfter(festival.getFechaFin())) {
                    throw new Exception("La fecha del pedido debe estar dentro del período del festival: "
                            + festival.getNombre() + " (" + festival.getFechaInicio() + " al " + festival.getFechaFin() + ")");
                }
            }
        }

        pedidoExistente.setFechaTransaccion(fechaPedido);
        pedidoExistente.setUnidadVenta(unidadVenta);

        // Se actualizan los ítems con orphanRemoval
        pedidoExistente.getItems().clear();

        if (pedidoDTO.getItems() != null && !pedidoDTO.getItems().isEmpty()) {
            for (ItemPedidoDTO itemDTO : pedidoDTO.getItems()) {
                Plato plato = platoRepository.findById(itemDTO.getIdPlato())
                        .orElseThrow(() -> new Exception("Plato no encontrado con ID: " + itemDTO.getIdPlato()));

                // Se valida que si está dado de baja, se rechaza la edicion
                if (Boolean.FALSE.equals(plato.getActivo())) {
                    throw new Exception("El plato '" + plato.getNombre() + "' no esta disponible, debe seleccionar un plato activo.");
                }

                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setCantidad(itemDTO.getCantidad());
                itemPedido.setPlato(plato);

                pedidoExistente.agregarItem(itemPedido);
            }
        }

        return pedidoRepository.save(pedidoExistente);
    }

    // Se busca el plato que mas vendido teniendo datos de festival y unidad venta
    @Override
    public PlatoDTO encontrarPlatoEstrella(Long idUnidadVenta, Long idFestival) {
        List<Object[]> resultados = pedidoRepository.encontrarPlatoEstrella(
                idUnidadVenta,
                idFestival,
                PageRequest.of(0, 1)
        );

        if (resultados == null || resultados.isEmpty()) {
            return null;
        }

        Object[] fila = resultados.get(0);
        Plato plato = (Plato) fila[0];

        Long cantidad = 0L;
        if (fila.length > 1 && fila[1] != null) {
            cantidad = ((Number) fila[1]).longValue();
        }

        if (cantidad <= 0) {
            return null;
        }

        PlatoDTO platoDto = modelMapper.map(plato, PlatoDTO.class);
        platoDto.setCantidad(cantidad);

        return platoDto;
    }

    // Se cambia el estado de terminado del pedido para el cocinero.
    @Override
    public void cambiarEstado(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setTerminado(!pedido.isTerminado());
        pedidoRepository.save(pedido);
    }
}