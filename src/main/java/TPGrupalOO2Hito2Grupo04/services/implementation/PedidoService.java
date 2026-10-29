package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.repositories.IPedidoRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IPlatoRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IUnidadVentaRepository;
import TPGrupalOO2Hito2Grupo04.services.IPedidoService;

@Service("pedidoService")
public class PedidoService implements IPedidoService {

    private static final Logger logger =
            LoggerFactory.getLogger(PedidoService.class);

    private final IPedidoRepository pedidoRepository;
    private final IPlatoRepository platoRepository;
    private final IUnidadVentaRepository unidadVentaRepository;
    private final ModelMapper modelMapper;

    public PedidoService(IPedidoRepository pedidoRepository, IPlatoRepository platoRepository, IUnidadVentaRepository unidadVentaRepository,
                         ModelMapper modelMapper) {
        this.pedidoRepository = pedidoRepository;
        this.platoRepository = platoRepository;
        this.unidadVentaRepository = unidadVentaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Pedido> traerTodo() {
        return pedidoRepository.findAll();
    }

    @Override
    public Optional<Pedido> traerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    public Pedido insertarOActualizar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

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

    @Override
    @Transactional
    public Pedido insertarPedido(PedidoDTO pedidoDTO) throws Exception {
        try {
            // Buscar la Unidad de Venta
            UnidadVenta unidadVenta = unidadVentaRepository.findById(pedidoDTO.getIdUnidadVenta())
                    .orElseThrow(() -> new Exception("Unidad de venta no encontrada con ID: " + pedidoDTO.getIdUnidadVenta()));

            Pedido pedido = new Pedido();
            pedido.setFechaTransaccion(pedidoDTO.getFechaTransaccion() != null ? pedidoDTO.getFechaTransaccion() : LocalDate.now());
            pedido.setUnidadVenta(unidadVenta);
            pedido.setTerminado(false);

            Pedido pedidoGuardado = pedidoRepository.save(pedido);

            // Proceso de los ítems del pedido si existen en el DTO
            if (pedidoDTO.getItems() != null && !pedidoDTO.getItems().isEmpty()) {
                for (ItemPedidoDTO itemDTO : pedidoDTO.getItems()) {
                    Plato plato = platoRepository.findById(itemDTO.getIdPlato())
                            .orElseThrow(() -> new Exception("Plato no encontrado con ID: " + itemDTO.getIdPlato()));

                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setCantidad(itemDTO.getCantidad());
                    itemPedido.setPlato(plato);

                    // Asigna el pedido automáticamente con uso de Helper
                    pedido.agregarItem(itemPedido);
                }

                // Guardamos el pedido usando CascadeType.ALL)
                pedidoGuardado = pedidoRepository.save(pedido);
            }

            logger.info("Pedido guardado exitosamente con ID: {}", pedidoGuardado.getIdPedido());
            return pedidoGuardado;

        } catch (Exception e) {
            logger.error("Error al intentar guardar el nuevo pedido", e);
            throw new Exception("Error al procesar el alta del pedido: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Pedido actualizarPedido(Long id, PedidoDTO pedidoDTO) throws Exception {
        // Buscar si el pedido existe
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new Exception("Pedido no encontrado con ID: " + id));

        // Buscar la Unidad de Venta nueva
        UnidadVenta unidadVenta = unidadVentaRepository.findById(pedidoDTO.getIdUnidadVenta())
                .orElseThrow(() -> new Exception("Unidad de venta no encontrada con ID: " + pedidoDTO.getIdUnidadVenta()));

        pedidoExistente.setFechaTransaccion(pedidoDTO.getFechaTransaccion());
        pedidoExistente.setUnidadVenta(unidadVenta);

        // Actualizar los ítems utilizando orphanRemoval
        pedidoExistente.getItems().clear(); // Borra los anteriores con orphanRemoval = true

        if (pedidoDTO.getItems() != null && !pedidoDTO.getItems().isEmpty()) {
            for (ItemPedidoDTO itemDTO : pedidoDTO.getItems()) {
                Plato plato = platoRepository.findById(itemDTO.getIdPlato())
                        .orElseThrow(() -> new Exception("Plato no encontrado con ID: " + itemDTO.getIdPlato()));

                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setCantidad(itemDTO.getCantidad());
                itemPedido.setPlato(plato);

                // Uso de helper para mantener la bidireccionalidad
                pedidoExistente.agregarItem(itemPedido);
            }
        }

        return pedidoRepository.save(pedidoExistente);
    }

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

        // Si la cantidad es 0, significa que no hay pedidos reales, retornamos null
        if (cantidad <= 0) {
            return null;
        }

        PlatoDTO platoDto = modelMapper.map(plato, PlatoDTO.class);
        platoDto.setCantidad(cantidad);

        return platoDto;
    }
}