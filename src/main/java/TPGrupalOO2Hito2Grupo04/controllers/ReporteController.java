package TPGrupalOO2Hito2Grupo04.controllers;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.services.IRentabilidadService;
import TPGrupalOO2Hito2Grupo04.services.IReporteVentaService;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;

// Reportes del responsable sobre la unidad de venta que tiene a cargo
@Controller
@RequestMapping("/reporte")
@PreAuthorize("hasAuthority('RESPONSABLE')")
public class ReporteController {

    private final IReporteVentaService reporteVentaService;
    private final IUnidadVentaService unidadVentaService;
    private final IRentabilidadService rentabilidadService;

    public ReporteController(IReporteVentaService reporteVentaService, IUnidadVentaService unidadVentaService,
                             IRentabilidadService rentabilidadService) {
        this.reporteVentaService = reporteVentaService;
        this.unidadVentaService = unidadVentaService;
        this.rentabilidadService = rentabilidadService;
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

    //GET Example: SERVER/reporte/rentabilidad?desde=2026-11-01&hasta=2026-11-10
    @GetMapping("/rentabilidad")
    public ModelAndView rentabilidad(Principal principal,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.REPORTE_RENTABILIDAD);
        UnidadVenta unidad = unidadVentaService.findByResponsable(principal.getName()).orElse(null);
        if (unidad == null) {
            mAV.addObject("error", "No tenés una unidad de venta a cargo.");
            return mAV;
        }
        mAV.addObject("festival", unidad.getFestival());
        try {
            // Sin fechas: el festival completo. Con fechas: solo ese período
            if (desde == null && hasta == null) {
                mAV.addObject("reporte", rentabilidadService.calcularRentabilidad(unidad.getIdUnidadVenta()));
            } else {
                mAV.addObject("reporte", rentabilidadService.calcularRentabilidadEntreFechas(unidad.getIdUnidadVenta(), desde, hasta));
            }
        } catch (IllegalArgumentException e) {
            mAV.addObject("error", e.getMessage());
            mAV.addObject("desde", desde);
            mAV.addObject("hasta", hasta);
        }
        return mAV;
    }
}
