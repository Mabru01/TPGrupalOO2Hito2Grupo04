package TPGrupalOO2Hito2Grupo04.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import TPGrupalOO2Hito2Grupo04.dtos.LiquidacionDTO;
import TPGrupalOO2Hito2Grupo04.entities.Staff;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.services.IFestivalService;
import TPGrupalOO2Hito2Grupo04.services.ILiquidacionService;

// Liquidación de sueldo según el rol:
//   ADMIN: elige festival y cualquier empleado
//   RESPONSABLE: elige entre los empleados de su unidad (festival de la unidad)
//   EMPLEADO: ve solo su sueldo (festival de su unidad)
// Los permisos se controlan acá, no solo en la vista.
@Controller
@RequestMapping("/liquidacion")
public class LiquidacionController {

    private final ILiquidacionService liquidacionService;
    private final IFestivalService festivalService;

    public LiquidacionController(ILiquidacionService liquidacionService, IFestivalService festivalService) {
        this.liquidacionService = liquidacionService;
        this.festivalService = festivalService;
    }

    //GET Example: SERVER/liquidacion?festivalId=1&staffId=2
    @GetMapping("")
    public ModelAndView index(Authentication authentication,
                              @RequestParam(name = "festivalId", required = false) Long festivalId,
                              @RequestParam(name = "staffId", required = false) Integer staffId) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.LIQUIDACION_INDEX);
        String rol = rolDe(authentication);
        mAV.addObject("rol", rol);
        mAV.addObject("festivalId", festivalId);
        mAV.addObject("staffId", staffId);

        try {
            if (rol.equals("ADMIN")) {
                mAV.addObject("festivales", festivalService.getAll());
                mAV.addObject("empleados", liquidacionService.getEmpleados());
            } else if (rol.equals("RESPONSABLE")) {
                UnidadVenta unidad = unidadDelResponsable(authentication.getName());
                mAV.addObject("unidad", unidad.getNombreComercial());
                mAV.addObject("empleados", liquidacionService.getEmpleadosDeUnidad(unidad.getIdUnidadVenta()));
            }
            LiquidacionDTO liquidacion = obtenerLiquidacion(authentication, rol, staffId, festivalId);
            mAV.addObject("liquidacion", liquidacion);
        } catch (IllegalArgumentException e) {
            mAV.addObject("error", e.getMessage());
        }
        return mAV;
    }

    //GET Example: SERVER/liquidacion/recibo?festivalId=1&staffId=2
    @GetMapping("/recibo")
    public ResponseEntity<?> recibo(Authentication authentication,
                                         @RequestParam(name = "festivalId", required = false) Long festivalId,
                                         @RequestParam(name = "staffId", required = false) Integer staffId) {
        LiquidacionDTO liquidacion;
        try {
            liquidacion = obtenerLiquidacion(authentication, rolDe(authentication), staffId, festivalId);
        } catch (IllegalArgumentException e) {
            liquidacion = null;
        }
        if (liquidacion == null) {
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "/liquidacion").build();
        }
        String archivo = "recibo-sueldo-" + liquidacion.getEmpleado().toLowerCase().replace(" ", "-") + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archivo + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(liquidacionService.generarReciboPdf(liquidacion));
    }

    // Devuelve la liquidación que el usuario tiene permitido ver (o null si todavía no eligió)
    private LiquidacionDTO obtenerLiquidacion(Authentication authentication, String rol, Integer staffId, Long festivalId) {
        switch (rol) {
            case "ADMIN":
                return (staffId != null && festivalId != null) ? liquidacionService.liquidar(staffId, festivalId) : null;
            case "RESPONSABLE":
                if (staffId == null) {
                    return null;
                }
                UnidadVenta unidad = unidadDelResponsable(authentication.getName());
                List<Staff> empleados = liquidacionService.getEmpleadosDeUnidad(unidad.getIdUnidadVenta());
                boolean esDeSuUnidad = empleados.stream().anyMatch(e -> e.getIdStaff() == staffId);
                // Solo puede ver a los empleados de su unidad: cualquier otro se ignora
                return esDeSuUnidad ? liquidacionService.liquidarEnSuFestival(staffId) : null;
            default:
                Staff empleado = liquidacionService.getEmpleadoPorMail(authentication.getName()).orElseThrow(
                        () -> new IllegalArgumentException("Tu usuario no está asociado a ningún empleado"));
                return liquidacionService.liquidarEnSuFestival(empleado.getIdStaff());
        }
    }

    private UnidadVenta unidadDelResponsable(String mail) {
        Optional<UnidadVenta> unidad = liquidacionService.getUnidadDeResponsable(mail);
        return unidad.orElseThrow(() -> new IllegalArgumentException("No tenés una unidad de venta a cargo"));
    }

    // Los roles se guardan sin prefijo (ADMIN, RESPONSABLE, EMPLEADO); se acepta también con ROLE_
    private String rolDe(Authentication authentication) {
        String rol = "EMPLEADO";
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String nombre = authority.getAuthority().replace("ROLE_", "");
            if (nombre.equals("ADMIN")) {
                return "ADMIN";
            }
            if (nombre.equals("RESPONSABLE")) {
                rol = "RESPONSABLE";
            }
        }
        return rol;
    }
}
