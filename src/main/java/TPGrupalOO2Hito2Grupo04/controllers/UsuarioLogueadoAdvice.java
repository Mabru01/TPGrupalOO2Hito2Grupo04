package TPGrupalOO2Hito2Grupo04.controllers;

import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import TPGrupalOO2Hito2Grupo04.entities.Cajero;
import TPGrupalOO2Hito2Grupo04.entities.Cocinero;
import TPGrupalOO2Hito2Grupo04.entities.Staff;
import TPGrupalOO2Hito2Grupo04.entities.UnidadVenta;
import TPGrupalOO2Hito2Grupo04.repositories.IStaffRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IUnidadVentaRepository;

// Agrega a todas las vistas quién está logueado (puesto, nombre y unidad) para mostrarlo en el navbar
@ControllerAdvice
public class UsuarioLogueadoAdvice {

    private final IStaffRepository staffRepository;
    private final IUnidadVentaRepository unidadVentaRepository;

    public UsuarioLogueadoAdvice(IStaffRepository staffRepository, IUnidadVentaRepository unidadVentaRepository) {
        this.staffRepository = staffRepository;
        this.unidadVentaRepository = unidadVentaRepository;
    }

    @ModelAttribute
    public void usuarioLogueado(Authentication auth, Model model) {
        if (auth == null) return;
        String mail = auth.getName();

        if (tieneRol(auth, "ADMIN")) {
            model.addAttribute("puestoUsuario", "Admin");
        } else if (tieneRol(auth, "RESPONSABLE")) {
            model.addAttribute("puestoUsuario", "Responsable");
            Optional<UnidadVenta> unidad = unidadVentaRepository.findByResponsable_Mail(mail);
            unidad.ifPresent(u -> model.addAttribute("unidadUsuario", u.getNombreComercial()));
        } else if (tieneRol(auth, "EMPLEADO")) {
            // El puesto del empleado sale de la subclase de Staff
            Optional<Staff> staff = staffRepository.findByUsuarioMail(mail);
            if (staff.isEmpty()) {
                model.addAttribute("puestoUsuario", "Empleado");
                return;
            }
            Object empleado = Hibernate.unproxy(staff.get());
            String puesto = empleado instanceof Cajero ? "Cajero"
                    : empleado instanceof Cocinero ? "Cocinero" : "Empleado";
            model.addAttribute("puestoUsuario", puesto);
            model.addAttribute("nombreUsuario", staff.get().getNombre() + " " + staff.get().getApellido());
            if (staff.get().getUnidadVenta() != null) {
                model.addAttribute("unidadUsuario", staff.get().getUnidadVenta().getNombreComercial());
            }
        }
    }

    private boolean tieneRol(Authentication auth, String rol) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rol));
    }
}
