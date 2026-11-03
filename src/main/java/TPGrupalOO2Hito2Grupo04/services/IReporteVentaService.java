package TPGrupalOO2Hito2Grupo04.services;

import TPGrupalOO2Hito2Grupo04.dtos.ReporteVentaDTO;

public interface IReporteVentaService {

    // Recaudación de la unidad en el festival en el que participa
    ReporteVentaDTO generarReporte(long idUnidadVenta);
}
