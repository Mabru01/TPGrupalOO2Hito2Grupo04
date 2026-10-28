package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Period;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;

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
import org.springframework.transaction.annotation.Transactional;

import TPGrupalOO2Hito2Grupo04.dtos.LiquidacionDTO;
import TPGrupalOO2Hito2Grupo04.entities.Cajero;
import TPGrupalOO2Hito2Grupo04.entities.Cocinero;
import TPGrupalOO2Hito2Grupo04.entities.Costo;
import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.entities.Staff;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.repositories.IFestivalRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IStaffRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IUnidadVentaRepository;
import TPGrupalOO2Hito2Grupo04.services.ILiquidacionService;
import TPGrupalOO2Hito2Grupo04.services.IStaffService;

@Service("liquidacionService")
public class LiquidacionService implements ILiquidacionService {

    private final IStaffService staffService;
    private final IFestivalRepository festivalRepository;
    private final IStaffRepository staffRepository;
    private final IUnidadVentaRepository unidadVentaRepository;

    public LiquidacionService(IStaffService staffService, IFestivalRepository festivalRepository,
                              IStaffRepository staffRepository, IUnidadVentaRepository unidadVentaRepository) {
        this.staffService = staffService;
        this.festivalRepository = festivalRepository;
        this.staffRepository = staffRepository;
        this.unidadVentaRepository = unidadVentaRepository;
    }

    @Override
    public List<Staff> getEmpleados() {
        return staffService.getAll();
    }

    @Override
    public Optional<Staff> getEmpleadoPorMail(String mail) {
        return staffRepository.findByUsuarioMail(mail);
    }

    @Override
    public Optional<UnidadVenta> getUnidadDeResponsable(String mail) {
        return unidadVentaRepository.findByResponsable_Mail(mail);
    }

    @Override
    public List<Staff> getEmpleadosDeUnidad(long idUnidadVenta) {
        return staffRepository.findByUnidadVentaIdUnidadVenta(idUnidadVenta);
    }

    @Override
    @Transactional(readOnly = true)
    public LiquidacionDTO liquidarEnSuFestival(int idStaff) {
        Staff staff = staffService.findById(idStaff).orElseThrow(
                () -> new IllegalArgumentException("ERROR: Staff not found with id: " + idStaff)
        );
        UnidadVenta unidad = staff.getUnidadVenta();
        if (unidad == null || unidad.getFestival() == null) {
            throw new IllegalArgumentException(staff.getNombre() + " " + staff.getApellido()
                    + " no está asignado a una unidad de venta de un festival");
        }
        return liquidar(idStaff, unidad.getFestival().getIdFestival());
    }

    // Cocina: porcentaje * (sueldo base + plus por categoría)
    // Cajero: porcentaje * (sueldo base + antigüedad)
    @Override
    @Transactional(readOnly = true)
    public LiquidacionDTO liquidar(int idStaff, long idFestival) {
        Staff staff = staffService.findById(idStaff).orElseThrow(
                () -> new IllegalArgumentException("ERROR: Staff not found with id: " + idStaff)
        );
        Festival festival = festivalRepository.findByIdFestival(idFestival).orElseThrow(
                () -> new IllegalArgumentException("ERROR: Festival not found with id: " + idFestival)
        );
        Costo costo = festival.getCosto();
        if (costo == null) {
            throw new IllegalArgumentException("ERROR: El festival " + festival.getNombre() + " no tiene costos cargados");
        }

        String tipo = "Staff";
        String conceptoAdicional = "Sin adicional";
        BigDecimal montoAdicional = BigDecimal.ZERO;

        if (staff instanceof Cocinero cocinero) {
            tipo = "Cocina (" + cocinero.getCategoria() + ")";
            conceptoAdicional = "Plus por categoría";
            montoAdicional = costo.getPlusCategoria(cocinero.getCategoria());
        } else if (staff instanceof Cajero) {
            int anios = Math.max(0, Period.between(staff.getFechaIngreso(), festival.getFechaInicio()).getYears());
            tipo = "Cajero";
            conceptoAdicional = "Antigüedad (" + anios + " años)";
            montoAdicional = costo.getAntiguedadPorAnio().multiply(BigDecimal.valueOf(anios));
        }

        BigDecimal porcentaje = festival.calcularPorcentajeSueldo();
        BigDecimal total = porcentaje.multiply(costo.getSueldoBase().add(montoAdicional))
                .setScale(2, RoundingMode.HALF_UP);

        String unidad = staff.getUnidadVenta() != null ? staff.getUnidadVenta().getNombreComercial() : "-";

        return new LiquidacionDTO(staff.getIdStaff(), staff.getNombre() + " " + staff.getApellido(), staff.getDni(),
                tipo, unidad, festival.getNombre(), festival.getDuracionEnDias(), porcentaje, costo.getSueldoBase(),
                conceptoAdicional, montoAdicional, total);
    }

    // Recibo de sueldo en PDF con el desglose de la liquidación
    @Override
    public byte[] generarReciboPdf(LiquidacionDTO liquidacion) {
        DecimalFormat pesos = new DecimalFormat("$ #,##0.00", new DecimalFormatSymbols(Locale.of("es", "AR")));
        Color bordo = new Color(59, 31, 43);
        Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, bordo);
        Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font negrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font blanca = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 50, 50, 50, 50);
        try {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            documento.add(new Paragraph("Epicentro Gourmet", titulo));
            documento.add(new Paragraph("Recibo de sueldo · emitido el "
                    + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), subtitulo));
            documento.add(new Paragraph(" "));

            PdfPTable datos = new PdfPTable(2);
            datos.setWidthPercentage(100);
            datos.setWidths(new float[]{35, 65});
            agregarFila(datos, "Empleado", liquidacion.getEmpleado(), negrita, normal);
            agregarFila(datos, "DNI", String.valueOf(liquidacion.getDni()), negrita, normal);
            agregarFila(datos, "Puesto", liquidacion.getTipo(), negrita, normal);
            agregarFila(datos, "Unidad de venta", liquidacion.getUnidad(), negrita, normal);
            agregarFila(datos, "Festival", liquidacion.getFestival() + " (" + liquidacion.getDiasFestival() + " días)", negrita, normal);
            documento.add(datos);
            documento.add(new Paragraph(" "));

            PdfPTable detalle = new PdfPTable(2);
            detalle.setWidthPercentage(100);
            detalle.setWidths(new float[]{65, 35});
            PdfPCell concepto = new PdfPCell(new Phrase("Concepto", blanca));
            PdfPCell importe = new PdfPCell(new Phrase("Importe", blanca));
            concepto.setBackgroundColor(bordo);
            importe.setBackgroundColor(bordo);
            importe.setHorizontalAlignment(Element.ALIGN_RIGHT);
            concepto.setPadding(6);
            importe.setPadding(6);
            detalle.addCell(concepto);
            detalle.addCell(importe);
            agregarImporte(detalle, "Sueldo base", pesos.format(liquidacion.getSueldoBase()), normal);
            agregarImporte(detalle, liquidacion.getConceptoAdicional(), pesos.format(liquidacion.getMontoAdicional()), normal);
            agregarImporte(detalle, "Porcentaje según duración del festival",
                    liquidacion.getPorcentaje().multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + " %", normal);
            agregarImporte(detalle, "Total a cobrar", pesos.format(liquidacion.getTotal()), negrita);
            documento.add(detalle);

            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Total = porcentaje × (sueldo base + adicional)", subtitulo));
        } catch (DocumentException e) {
            throw new IllegalStateException("No se pudo generar el recibo de sueldo", e);
        } finally {
            documento.close();
        }
        return salida.toByteArray();
    }

    private void agregarFila(PdfPTable tabla, String etiqueta, String valor, Font fuenteEtiqueta, Font fuenteValor) {
        PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta, fuenteEtiqueta));
        PdfPCell celdaValor = new PdfPCell(new Phrase(valor, fuenteValor));
        celdaEtiqueta.setPadding(6);
        celdaValor.setPadding(6);
        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
    }

    private void agregarImporte(PdfPTable tabla, String concepto, String importe, Font fuente) {
        PdfPCell celdaConcepto = new PdfPCell(new Phrase(concepto, fuente));
        PdfPCell celdaImporte = new PdfPCell(new Phrase(importe, fuente));
        celdaImporte.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celdaConcepto.setPadding(6);
        celdaImporte.setPadding(6);
        tabla.addCell(celdaConcepto);
        tabla.addCell(celdaImporte);
    }
}
