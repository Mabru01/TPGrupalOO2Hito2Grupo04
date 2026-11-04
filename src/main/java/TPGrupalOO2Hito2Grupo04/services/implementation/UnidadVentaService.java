package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

import TPGrupalOO2Hito2Grupo04.repositories.IFestivalRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IUsuarioRepository;
import com.lowagie.text.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;

import TPGrupalOO2Hito2Grupo04.entities.*;
import TPGrupalOO2Hito2Grupo04.dtos.*;
import TPGrupalOO2Hito2Grupo04.repositories.*;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service("unidadVentaService")
public class UnidadVentaService implements IUnidadVentaService {
    private static final Logger logger = LoggerFactory.getLogger(UnidadVentaService.class);

    private final IUnidadVentaRepository unidadVentaRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IFestivalRepository festivalRepository;
    private final IRolRepository rolRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UnidadVentaService(IUnidadVentaRepository unidadVentaRepository, IUsuarioRepository usuarioRepository, IFestivalRepository festivalRepository, IRolRepository rolRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.unidadVentaRepository = unidadVentaRepository;
        this.usuarioRepository = usuarioRepository;
        this.festivalRepository = festivalRepository;
        this.rolRepository = rolRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UnidadVenta> getAll() {
        return unidadVentaRepository.findAll();
    }

    @Override
    public Optional<UnidadVenta> findById(long id) {
        return unidadVentaRepository.findById(id);
    }

    @Override
    public UnidadVenta findByCodigoUnico(String codigoUnico) {

        return unidadVentaRepository.findByCodigoUnico(codigoUnico).orElseThrow(
                () -> new IllegalArgumentException("ERROR: Unidad de venta not found with cod: " + codigoUnico)
        );
    }

    @Override
    public UnidadVenta insertOrUpdate(UnidadVenta unidadVenta) {
        return unidadVentaRepository.save(unidadVenta);
    }

    @Override
    public boolean remove(long id) {
        try {
            unidadVentaRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException | DataIntegrityViolationException e) {
            logger.warn("Could not remove UnidadVenta with id {}", id, e);
            return false;
        } catch (DataAccessException e) {
            logger.error("Database error while removing UnidadVenta with id {}", id, e);
            return false;
        }
    }
    @Override
    public UnidadVenta registrarUnidadVentaConResponsable(UnidadVentaDTO dto) {

        // 1. Validar que el email no exista
        if (usuarioRepository.findByMail(dto.getEmailResponsable()).isPresent()) {
            throw new IllegalArgumentException("El email del responsable ya se encuentra registrado.");
        }

        // 2. Buscar el único festival activo
        LocalDate hoy = LocalDate.now();
        Festival festival = festivalRepository.findFestivalActivo(hoy)
                .orElseThrow(() -> new RuntimeException("No hay ningún festival activo."));

        // 3. Obtener (o crear) el rol RESPONSABLE
        Rol rolResponsable = rolRepository.findByRol("RESPONSABLE")
                .orElseGet(() -> {
                    Rol r = new Rol();
                    r.setRol("RESPONSABLE");
                    return rolRepository.save(r);
                });

        // 4. Crear el usuario con ese rol y la contraseña hasheada
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setMail(dto.getEmailResponsable());
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getPasswordResponsable()));
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setRol(rolResponsable);

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // 5. Instanciar y mapear la entidad hija
        UnidadVenta unidadVenta;

        if (dto instanceof FoodTruckDTO ftDto) {
            unidadVenta = modelMapper.map(ftDto, FoodTruck.class);
        } else if (dto instanceof PuestoDesarmableDTO pdDto) {
            unidadVenta = modelMapper.map(pdDto, PuestoDesarmable.class);
        } else {
            throw new IllegalArgumentException("Tipo de unidad de venta desconocido.");
        }

        unidadVenta.setResponsable(usuarioGuardado);
        unidadVenta.setFestival(festival);
        unidadVenta.setActiva(true);
        unidadVenta.setCodigoUnico(generarCodigoUnico());
        return unidadVentaRepository.save(unidadVenta);
    }

    public String generarCodigoUnico() {
        String codigo;
        boolean existe;
        do {
            // Genera un código aleatorio de 8 caracteres (por ejemplo, usando UUID o Random)
            codigo = UUID.randomUUID().toString().substring(0, 5).toUpperCase();

            // Validar si ya existe

            existe = unidadVentaRepository.existsByCodigoUnico(codigo);
        } while (existe); // Si ya existe, repite el ciclo; si es único, sale.

        return codigo;
    }

    @Override
    public double calcularCanon(Long idUnidadVenta) {
        UnidadVenta unidad = unidadVentaRepository.findById(idUnidadVenta)
                .orElseThrow(() -> new IllegalArgumentException("Sin unidad de venta asignada"));

        return unidad.calcularCanon();
    }

    @Override
    public Optional<UnidadVenta> findByResponsable(String mail) {
        return unidadVentaRepository.findByResponsable_Mail(mail);
    }

    @Override
    public void darDeBaja(Long id) {
        UnidadVenta unidad = unidadVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidad de venta no encontrada"));

        unidad.setActiva(false); // Cambiamos el estado
        unidadVentaRepository.save(unidad); // Guardamos la actualización
    }

    @Override
    public void reactivar(Long id) {
        UnidadVenta unidad = unidadVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidad de venta no encontrada"));

        unidad.setActiva(true); // Cambiamos el estado
        unidadVentaRepository.save(unidad); // Guardamos la actualización
    }


    @Override
    public void modificarFoodTruck(Long id, FoodTruckDTO dto) {
        FoodTruck ft = (FoodTruck) unidadVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FoodTruck no encontrado"));

        // Actualizás los campos comunes y los propios de FoodTruck

        ft.setNombreComercial(dto.getNombreComercial());
        ft.setSuperficie(dto.getSuperficie());
        ft.setPatente(dto.getPatente());
        ft.setUsaLuz(dto.isUsaLuz());

        unidadVentaRepository.save(ft);
    }

    @Override
    public void modificarPuestoDesarmable(Long id, PuestoDesarmableDTO dto) {
        PuestoDesarmable pd = (PuestoDesarmable) unidadVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Puesto Desarmable no encontrado"));

        pd.setNombreComercial(dto.getNombreComercial());
        pd.setSuperficie(dto.getSuperficie());
        pd.setCantidad(dto.getCantidad());
        pd.setTiempoMontaje(dto.getTiempoMontaje());

        unidadVentaRepository.save(pd);
    }

    @Override
    public List<ReporteMayoresCanonDTO> obtenerUnidadesConMayorCanon(Long idFestival, int cantidad){
        List<UnidadVenta> unidades = unidadVentaRepository.findByFestivalIdFestivalAndActivaTrue(idFestival);

        return unidades.stream()
                // Transformacion de cada UnidadVenta en un ReporteMayoresCanonDTO
                .map(u -> new ReporteMayoresCanonDTO(
                        u.getNombreComercial(),
                        u.getCodigoUnico(),
                        (u instanceof FoodTruck ? "Food Truck" : "Puesto Desarmable"), // Determinamos el tipo
                        u.calcularCanon() //
                ))
                // Ordenamos de mayor a menor
                .sorted(Comparator.comparingDouble(ReporteMayoresCanonDTO::getCanon).reversed())
                .limit(cantidad)
                // Convertimos el Stream de nuevo a una List
                .collect(Collectors.toList());

    }

    @Override
    public byte[] generarReporteMayoresCanonPdf(List<ReporteMayoresCanonDTO> reporte, Long idFestival) {
        // Formato para los pesos argentinos ($ 1.234,56)
        DecimalFormat pesos = new DecimalFormat("$ #,##0.00", new DecimalFormatSymbols(Locale.of("es", "AR")));
        Color bordo = new Color(59, 31, 43); // Color institucional del proyecto

        // Fuentes
        Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, bordo);
        Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font blanca = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            // Encabezado
            documento.add(new Paragraph("Epicentro Gourmet", titulo));
            documento.add(new Paragraph("Reporte de Unidades con Mayor Canon - Festival ID: " + idFestival, subtitulo));
            documento.add(new Paragraph(" ")); // Espacio en blanco

            // Tabla de 4 columnas
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{35, 20, 25, 20}); // Proporción de ancho

            // Celdas de Encabezado
            String[] headers = {"Nombre Comercial", "Código Único", "Tipo de Unidad", "Canon ($)"};
            for (int i = 0; i < headers.length; i++) {
                PdfPCell celda = new PdfPCell(new Phrase(headers[i], blanca));
                celda.setBackgroundColor(bordo);
                if (i == 3) celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                celda.setPadding(6);
                tabla.addCell(celda);
            }

            // Filas con los datos del DTO
            for (ReporteMayoresCanonDTO dto : reporte) {
                // Nombre
                PdfPCell c1 = new PdfPCell(new Phrase(dto.getNombreComercial(), normal));
                c1.setPadding(6);
                tabla.addCell(c1);

                // Código
                PdfPCell c2 = new PdfPCell(new Phrase(dto.getCodigoUnico(), normal));
                c2.setPadding(6);
                tabla.addCell(c2);

                // Tipo
                PdfPCell c3 = new PdfPCell(new Phrase(dto.getTipoUnidad(), normal));
                c3.setPadding(6);
                tabla.addCell(c3);

                // Canon (Alineado a la derecha)
                PdfPCell c4 = new PdfPCell(new Phrase(pesos.format(dto.getCanon()), normal));
                c4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                c4.setPadding(6);
                tabla.addCell(c4);
            }

            documento.add(tabla);

        } catch (DocumentException e) {
            throw new IllegalStateException("No se pudo generar el PDF del reporte", e);
        } finally {
            documento.close();
        }

        return salida.toByteArray();
    }


    @Override
    public List<RankingRecaudacionUnidadDTO> obtenerRankingRecaudacion(Long idFestival) {
        // las unidades del festival con los pedidos y sus ítems cargados
        List<UnidadVenta> unidades = unidadVentaRepository.findByFestivalWithPedidosAndItems(idFestival);

        //  cada unidad sumando el precio total de cada pedido
        return unidades.stream().map(u -> {
                    double totalRecaudacion = 0.0;
                    if (u.getPedidos() != null) {
                        totalRecaudacion = u.getPedidos().stream()
                                .mapToDouble(Pedido::getPrecioTotal)
                                .sum();
                    }

                    return new RankingRecaudacionUnidadDTO(
                            u.getNombreComercial(),
                            u.getCodigoUnico(),
                            totalRecaudacion
                    );
                })
                // mayor a menor
                .sorted(Comparator.comparing(RankingRecaudacionUnidadDTO::getRecaudacionTotal).reversed())
                .collect(Collectors.toList());
    }



}