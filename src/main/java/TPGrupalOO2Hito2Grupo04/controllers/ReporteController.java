package TPGrupalOO2Hito2Grupo04.controllers;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.services.IReporteVentaService;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;

// Reportes del responsable sobre la unidad de venta que tiene a cargo
@Controller
@RequestMapping("/reporte")
@PreAuthorize("hasAuthority('RESPONSABLE')")
public class ReporteController {

    private final IReporteVentaService reporteVentaService;
    private final IUnidadVentaService unidadVentaService;

    public ReporteController(IReporteVentaService reporteVentaService, IUnidadVentaService unidadVentaService) {
        this.reporteVentaService = reporteVentaService;
        this.unidadVentaService = unidadVentaService;
    }

    //GET Example: SERVER/reporte/recaudacion
    @GetMapping("/recaudacion")
    public ModelAndView recaudacion(Principal principal) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.REPORTE_RECAUDACION);
        // La unidad sale del usuario logueado: un responsable solo ve la recaudación de la suya
        UnidadVenta unidad = unidadVentaService.findByResponsable(principal.getName()).orElse(null);
        if (unidad == null) {
            mAV.addObject("error", "No tenés una unidad de venta a cargo.");
            return mAV;
        }
        mAV.addObject("reporte", reporteVentaService.generarReporte(unidad.getIdUnidadVenta()));
        return mAV;
    }
}
