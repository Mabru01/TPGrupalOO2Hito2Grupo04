package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Period;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TPGrupalOO2Hito2Grupo04.dtos.LiquidacionDTO;
import TPGrupalOO2Hito2Grupo04.entities.Cajero;
import TPGrupalOO2Hito2Grupo04.entities.Cocinero;
import TPGrupalOO2Hito2Grupo04.entities.Costo;
import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.entities.Staff;
import TPGrupalOO2Hito2Grupo04.repositories.IFestivalRepository;
import TPGrupalOO2Hito2Grupo04.services.ILiquidacionService;
import TPGrupalOO2Hito2Grupo04.services.IStaffService;

@Service("liquidacionService")
public class LiquidacionService implements ILiquidacionService {

    private final IStaffService staffService;
    private final IFestivalRepository festivalRepository;

    public LiquidacionService(IStaffService staffService, IFestivalRepository festivalRepository) {
        this.staffService = staffService;
        this.festivalRepository = festivalRepository;
    }

    @Override
    public List<Staff> getEmpleados() {
        return staffService.getAll();
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
            tipo = "Cocina (" + cocinero.getPlusCategoria() + ")";
            conceptoAdicional = "Plus por categoría";
            montoAdicional = costo.getPlusCategoria(cocinero.getPlusCategoria());
        } else if (staff instanceof Cajero) {
            int anios = Math.max(0, Period.between(staff.getFechaIngreso(), festival.getFechaInicio()).getYears());
            tipo = "Cajero";
            conceptoAdicional = "Antigüedad (" + anios + " años)";
            montoAdicional = costo.getAntiguedadPorAnio().multiply(BigDecimal.valueOf(anios));
        }

        BigDecimal porcentaje = festival.calcularPorcentajeSueldo();
        BigDecimal total = porcentaje.multiply(costo.getSueldoBase().add(montoAdicional))
                .setScale(2, RoundingMode.HALF_UP);

        return new LiquidacionDTO(staff.getNombre() + " " + staff.getApellido(), tipo, festival.getNombre(),
                festival.getDuracionEnDias(), porcentaje, costo.getSueldoBase(), conceptoAdicional,
                montoAdicional, total);
    }
}
