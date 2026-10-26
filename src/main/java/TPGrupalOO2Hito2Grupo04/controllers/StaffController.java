package TPGrupalOO2Hito2Grupo04.controllers;

import TPGrupalOO2Hito2Grupo04.dtos.*;
import TPGrupalOO2Hito2Grupo04.entities.Cajero;
import TPGrupalOO2Hito2Grupo04.entities.Cocinero;
import TPGrupalOO2Hito2Grupo04.entities.Staff;
import TPGrupalOO2Hito2Grupo04.services.IStaffService;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;

@Controller
@RequestMapping("/staff")
public class StaffController {
    private static final String STAFF_ATTR = "staffDTO";

    private final IStaffService staffService;
    private final IUnidadVentaService unidadVentaService;
    private final ModelMapper modelMapper;


    public StaffController(IStaffService staffService, IUnidadVentaService unidadVentaService, ModelMapper modelMapper) {
        this.staffService = staffService;
        this.unidadVentaService = unidadVentaService;
        this.modelMapper = modelMapper;
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

    @GetMapping("/modificar")
    public ModelAndView modificar(@RequestParam(required = false) Integer idStaff) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_UPDATE);
        mAV.addObject("staffs", staffService.getAll());
        mAV.addObject("unidadesVenta", unidadVentaService.getAll());
        if (idStaff != null) {
            try {
                mAV.addObject(STAFF_ATTR, staffService.obtenerDTOPorId(idStaff));
            } catch (IllegalArgumentException e) {
                mAV.addObject("error", e.getMessage());
            }
        }
        return mAV;
    }

    @PostMapping("/modificar")
    public RedirectView modificarStaff(@ModelAttribute("staffDTO") StaffDTO staffDTO) {
        staffService.modificarStaff(staffDTO);
        return new RedirectView(ViewRouteHelper.STAFF_ROOT);
    }

    @GetMapping("/baja")
    public ModelAndView baja(@RequestParam(required = false) Integer idStaff) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_DROP);
        mAV.addObject("staffs", staffService.getAll());
        if (idStaff != null) {
            try {
                Staff staff = staffService.findById(idStaff)
                        .orElseThrow(() -> new IllegalArgumentException("ERROR: Empleado no encontrado"));
                mAV.addObject(STAFF_ATTR, modelMapper.map(staff, StaffDTO.class));
            } catch (IllegalArgumentException e) {
                mAV.addObject("error", e.getMessage());
            }
        }
        return mAV;
    }

    @PostMapping("/baja")
    public RedirectView eliminarStaff(@ModelAttribute("staffDTO") StaffDTO staffDTO) {
        staffService.darBajaEmpleado(staffDTO);
        return new RedirectView(ViewRouteHelper.STAFF_ROOT);
    }
}
