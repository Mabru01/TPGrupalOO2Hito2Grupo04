package TPGrupalOO2Hito2Grupo04.controllers;

import TPGrupalOO2Hito2Grupo04.dtos.*;
import TPGrupalOO2Hito2Grupo04.services.IStaffService;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasAnyAuthority('RESPONSABLE', 'EMPLEADO')")
public class StaffController {
    private static final String STAFF_ATTR = "staffDTO";

    private final IStaffService staffService;
    private final IUnidadVentaService unidadVentaService;


    public StaffController(IStaffService staffService, IUnidadVentaService unidadVentaService) {
        this.staffService = staffService;
        this.unidadVentaService = unidadVentaService;
    }

    @GetMapping("")
    public ModelAndView index() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_INDEX);
        mAV.addObject("staffs", staffService.getAll());
        return mAV;
    }

    @GetMapping("/nuevo/cocinero")
    public ModelAndView nuevoCocinero() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_NEW_COCINERO);
        mAV.addObject("unidadesVenta", unidadVentaService.getAll()); // Para el desplegable (select)
        mAV.addObject(STAFF_ATTR, new CocineroDTO());
        return mAV;
    }

    @GetMapping("/nuevo/cajero")
    public ModelAndView nuevoCajero() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_NEW_CAJERO);
        mAV.addObject(STAFF_ATTR, new CajeroDTO());
        mAV.addObject("unidadesVenta", unidadVentaService.getAll()); // Para el desplegable (select)
        return mAV;
    }

    @PostMapping("/guardar/cocinero")
    public RedirectView guardarCocinero(
            @ModelAttribute("staffDTO") CocineroDTO staffDTO) {
        staffService.registrarStaff(staffDTO);
        return new RedirectView(ViewRouteHelper.STAFF_ROOT);
    }

    @PostMapping("/guardar/cajero")
    public RedirectView guardarCocinero(
            @ModelAttribute("staffDTO") CajeroDTO staffDTO) {
        staffService.registrarStaff(staffDTO);
        return new RedirectView(ViewRouteHelper.STAFF_ROOT);
    }
}
