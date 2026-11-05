package TPGrupalOO2Hito2Grupo04.services;

import java.time.LocalDate;

import TPGrupalOO2Hito2Grupo04.dtos.ReporteRentabilidadDTO;

public interface IRentabilidadService {

    // Rentabilidad neta de la unidad en su festival completo
    ReporteRentabilidadDTO calcularRentabilidad(long idUnidadVenta);

    // Rentabilidad neta de la unidad entre dos fechas de su festival
    ReporteRentabilidadDTO calcularRentabilidadEntreFechas(long idUnidadVenta, LocalDate desde, LocalDate hasta);
}
