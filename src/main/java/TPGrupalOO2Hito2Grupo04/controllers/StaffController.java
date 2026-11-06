package TPGrupalOO2Hito2Grupo04.controllers;

import TPGrupalOO2Hito2Grupo04.dtos.*;
import TPGrupalOO2Hito2Grupo04.entities.Cajero;
import TPGrupalOO2Hito2Grupo04.entities.Cocinero;
import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.entities.Staff;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.services.IFestivalService;
import TPGrupalOO2Hito2Grupo04.services.IStaffService;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.services.IUnidadVentaService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/staff")
public class StaffController {
    private static final String STAFF_ATTR = "staffDTO";

    private final IStaffService staffService;
    private final IUnidadVentaService unidadVentaService;
    private final IFestivalService festivalService;
    private final ModelMapper modelMapper;


    public StaffController(IStaffService staffService, IUnidadVentaService unidadVentaService, IFestivalService festivalService, ModelMapper modelMapper) {
        this.staffService = staffService;
        this.unidadVentaService = unidadVentaService;
        this.festivalService = festivalService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('RESPONSABLE', 'ROLE_RESPONSABLE')")
    public ModelAndView index(Authentication auth) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_INDEX);
        mAV.addObject("staffs", empleadosDeMiUnidad(auth, staffService.getAllByRolEmpleado()));
        return mAV;
    }

    @GetMapping("/nuevo/cocinero")
    @PreAuthorize("hasAnyAuthority('RESPONSABLE', 'ROLE_RESPONSABLE')")
    public ModelAndView nuevoCocinero(Authentication auth) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_NEW_COCINERO);
        mAV.addObject("unidadesVenta", List.of(unidadDelResponsable(auth))); // Para el desplegable (select)
        mAV.addObject(STAFF_ATTR, new CocineroDTO());
        return mAV;
    }

    @GetMapping("/nuevo/cajero")
    @PreAuthorize("hasAnyAuthority('RESPONSABLE', 'ROLE_RESPONSABLE')")
    public ModelAndView nuevoCajero(Authentication auth) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_NEW_CAJERO);
        mAV.addObject(STAFF_ATTR, new CajeroDTO());
        mAV.addObject("unidadesVenta", List.of(unidadDelResponsable(auth))); // Para el desplegable (select)
        return mAV;
    }

    @PostMapping("/guardar/cocinero")
    @PreAuthorize("hasAnyAuthority('RESPONSABLE', 'ROLE_RESPONSABLE')")
    public ModelAndView guardarCocinero(
            @ModelAttribute("staffDTO") CocineroDTO staffDTO, Authentication auth) {
        try {
            // El empleado siempre queda en la unidad del responsable
            staffDTO.setIdUnidadVenta(unidadDelResponsable(auth).getIdUnidadVenta());
            staffService.registrarStaff(staffDTO);
            return new ModelAndView(new RedirectView(ViewRouteHelper.STAFF_ROOT));
        } catch (IllegalArgumentException e) {
            ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_NEW_COCINERO);
            mAV.addObject("unidadesVenta", List.of(unidadDelResponsable(auth)));
            mAV.addObject(STAFF_ATTR, new CocineroDTO());
            mAV.addObject("error", e.getMessage());
            return mAV;
        }
    }

    @PostMapping("/guardar/cajero")
    @PreAuthorize("hasAnyAuthority('RESPONSABLE', 'ROLE_RESPONSABLE')")
    public ModelAndView guardarCajero(
            @ModelAttribute("staffDTO") CajeroDTO staffDTO, Authentication auth) {
        try {
            // El empleado siempre queda en la unidad del responsable
            staffDTO.setIdUnidadVenta(unidadDelResponsable(auth).getIdUnidadVenta());
            staffService.registrarStaff(staffDTO);
            return new ModelAndView(new RedirectView(ViewRouteHelper.STAFF_ROOT));
        } catch (IllegalArgumentException e) {
            ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_NEW_CAJERO);
            mAV.addObject(STAFF_ATTR, new CajeroDTO());
            mAV.addObject("unidadesVenta", List.of(unidadDelResponsable(auth)));
            mAV.addObject("error", e.getMessage());
            return mAV;
        }
    }

    @GetMapping("/modificar")
    @PreAuthorize("hasAnyAuthority('RESPONSABLE', 'ROLE_RESPONSABLE')")
    public ModelAndView modificar(@RequestParam(required = false) Integer idStaff, Authentication auth) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_UPDATE);
        mAV.addObject("staffs", empleadosDeMiUnidad(auth, staffService.getAllByRolEmpleado()));
        mAV.addObject("unidadesVenta", List.of(unidadDelResponsable(auth)));
        if (idStaff != null) {
            try {
                verificarQueSeaDeMiUnidad(auth, idStaff);
                StaffDTO staffDTO = staffService.obtenerDTOPorId(idStaff);
                mAV.addObject(STAFF_ATTR, staffDTO);
            } catch (IllegalArgumentException e) {
                mAV.addObject("error", e.getMessage());
            }
        }
        return mAV;
    }

    @PostMapping("/modificar")
    @PreAuthorize("hasAnyAuthority('RESPONSABLE', 'ROLE_RESPONSABLE')")
    public ModelAndView modificarStaff(@ModelAttribute("staffDTO") StaffDTO staffDTO, Authentication auth) {
        try {
            verificarQueSeaDeMiUnidad(auth, staffDTO.getIdStaff());
            staffDTO.setIdUnidadVenta(unidadDelResponsable(auth).getIdUnidadVenta());
            staffService.modificarStaff(staffDTO);
            return new ModelAndView(new RedirectView(ViewRouteHelper.STAFF_ROOT));
        } catch (IllegalArgumentException e) {
            ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_UPDATE);
            mAV.addObject("staffs", empleadosDeMiUnidad(auth, staffService.getAllByRolEmpleado()));
            mAV.addObject("unidadesVenta", List.of(unidadDelResponsable(auth)));
            mAV.addObject(STAFF_ATTR, staffDTO);
            mAV.addObject("error", e.getMessage());
            return mAV;
        }
    }

    @GetMapping("/baja")
    @PreAuthorize("hasAnyAuthority('RESPONSABLE', 'ROLE_RESPONSABLE')")
    public ModelAndView baja(@RequestParam(required = false) Integer idStaff, Authentication auth) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_DROP);
        mAV.addObject("staffs", empleadosDeMiUnidad(auth, staffService.getAllByRolEmpleado()));
        if (idStaff != null) {
            try {
                verificarQueSeaDeMiUnidad(auth, idStaff);
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
    @PreAuthorize("hasAnyAuthority('RESPONSABLE', 'ROLE_RESPONSABLE')")
    public ModelAndView eliminarStaff(@ModelAttribute("staffDTO") StaffDTO staffDTO, Authentication auth) {
        try {
            verificarQueSeaDeMiUnidad(auth, staffDTO.getIdStaff());
            staffService.darBajaEmpleado(staffDTO);
            return new ModelAndView(new RedirectView(ViewRouteHelper.STAFF_ROOT));
        } catch (IllegalArgumentException e) {
            ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_DROP);
            mAV.addObject("staffs", empleadosDeMiUnidad(auth, staffService.getAllByRolEmpleado()));
            mAV.addObject(STAFF_ATTR, staffDTO);
            mAV.addObject("error", e.getMessage());
            return mAV;
        }
    }

    @GetMapping("/reactivar")
    @PreAuthorize("hasAnyAuthority('RESPONSABLE', 'ROLE_RESPONSABLE')")
    public ModelAndView reactivar(@RequestParam(required = false) Integer idStaff, Authentication auth) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_REACTIVATE);
        mAV.addObject("staffs", empleadosDeMiUnidad(auth, staffService.getStaffsDadosDeBaja()));
        if (idStaff != null) {
            try {
                verificarQueSeaDeMiUnidad(auth, idStaff);
                Staff staff = staffService.findById(idStaff)
                        .orElseThrow(() -> new IllegalArgumentException("ERROR: Empleado no encontrado"));
                mAV.addObject(STAFF_ATTR, modelMapper.map(staff, StaffDTO.class));
            } catch (IllegalArgumentException e) {
                mAV.addObject("error", e.getMessage());
            }
        }
        return mAV;
    }

    @PostMapping("/reactivar")
    @PreAuthorize("hasAnyAuthority('RESPONSABLE', 'ROLE_RESPONSABLE')")
    public ModelAndView reactivarStaff(@ModelAttribute("staffDTO") StaffDTO staffDTO, Authentication auth) {
        try {
            verificarQueSeaDeMiUnidad(auth, staffDTO.getIdStaff());
            boolean resultado = staffService.reactivar(staffDTO.getIdStaff());
            if (!resultado) {
                throw new IllegalArgumentException("No se pudo reactivar el empleado. Verifique que esté dado de baja.");
            }
            return new ModelAndView(new RedirectView(ViewRouteHelper.STAFF_ROOT));
        } catch (IllegalArgumentException e) {
            ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_REACTIVATE);
            mAV.addObject("staffs", empleadosDeMiUnidad(auth, staffService.getStaffsDadosDeBaja()));
            mAV.addObject(STAFF_ATTR, staffDTO);
            mAV.addObject("error", e.getMessage());
            return mAV;
        }
    }

    @GetMapping("/perfil")
    @PreAuthorize("hasAuthority('EMPLEADO')")
    public ModelAndView perfil(Authentication authentication) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_PERFIL);
        String mail = authentication.getName();
        Staff staff = staffService.findByMail(mail);
        mAV.addObject("staff", staff);
        return mAV;
    }

    @GetMapping("/calendario")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView calendario() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_CALENDARIO);
        List<Festival> festivales = festivalService.getAll();
        // Mapear solo los campos necesarios para evitar referencias circulares al serializar
        List<Map<String, Object>> festivalesDTO = festivales.stream()
            .map(f -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("nombre", f.getNombre());
                dto.put("temporada", f.getTemporada());
                dto.put("fechaInicio", f.getFechaInicio().toString());
                dto.put("fechaFin", f.getFechaFin().toString());
                return dto;
            })
            .toList();
        mAV.addObject("festivales", festivalesDTO);
        mAV.addObject("hoy", LocalDate.now());
        return mAV;
    }

    @GetMapping("/cambiar/contrasenia")
    @PreAuthorize("hasAuthority('EMPLEADO')")
    public ModelAndView cambiarContrasenia() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_CAMBIAR_CONTRASENA);
        mAV.addObject("contraseniaDTO", new CambiarContraseniaDTO());
        return mAV;
    }

    @PostMapping("/cambiar/contrasenia")
    @PreAuthorize("hasAuthority('EMPLEADO')")
    public ModelAndView cambiarContrasenia(
            @ModelAttribute("contraseniaDTO") CambiarContraseniaDTO dto,
            Authentication authentication) {

        try {
            // Obtener el empleado logueado
            String mail = authentication.getName();
            Staff staff = staffService.findByMail(mail);

            // Validar y cambiar la contraseña
            staffService.empleadoCambiarContrasenia(
                staff.getUsuario().getId(),
                dto.getContraseniaActual(),
                dto.getNuevaContrasenia(),
                dto.getConfirmarContrasenia()
            );

            // Logout automático
            return new ModelAndView(new RedirectView("/logout?success=true"));

        } catch (IllegalArgumentException e) {
            ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_CAMBIAR_CONTRASENA);
            mAV.addObject("contraseniaDTO", dto);
            mAV.addObject("error", e.getMessage());
            return mAV;
        }
    }

    // Unidad de venta del responsable logueado: solo administra a los empleados de esa unidad
    private UnidadVenta unidadDelResponsable(Authentication auth) {
        return unidadVentaService.findByResponsable(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("No tenés una unidad de venta a cargo"));
    }

    private List<Staff> empleadosDeMiUnidad(Authentication auth, List<Staff> staffs) {
        long idUnidad = unidadDelResponsable(auth).getIdUnidadVenta();
        return staffs.stream()
                .filter(s -> s.getUnidadVenta() != null && s.getUnidadVenta().getIdUnidadVenta() == idUnidad)
                .toList();
    }

    // Evita que se modifique, se dé de baja o se reactive a un empleado de otra unidad cambiando el id
    private void verificarQueSeaDeMiUnidad(Authentication auth, int idStaff) {
        long idUnidad = unidadDelResponsable(auth).getIdUnidadVenta();
        boolean esDeMiUnidad = staffService.findById(idStaff)
                .map(s -> s.getUnidadVenta() != null && s.getUnidadVenta().getIdUnidadVenta() == idUnidad)
                .orElse(false);
        if (!esDeMiUnidad) {
            throw new IllegalArgumentException("El empleado no pertenece a tu unidad de venta.");
        }
    }
}
