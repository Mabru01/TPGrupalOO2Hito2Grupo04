package TPGrupalOO2Hito2Grupo04.controllers;

import TPGrupalOO2Hito2Grupo04.services.IStaffService;
import TPGrupalOO2Hito2Grupo04.dtos.StaffDTO;
import TPGrupalOO2Hito2Grupo04.helpers.ViewRouteHelper;
import TPGrupalOO2Hito2Grupo04.entities.Staff;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
@RequestMapping("/person")
public class StaffController {
    private static final String STAFF_ATTR = "staff";

    private final IStaffService staffService;
    private final ModelMapper modelMapper;

    public StaffController(IStaffService staffService, ModelMapper modelMapper) {
        this.staffService = staffService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("")
    public ModelAndView index() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_INDEX);
        mAV.addObject("staffs", staffService.getAll());
        return mAV;
    }

    @GetMapping("/new")
    public ModelAndView create() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_NEW);
        mAV.addObject(STAFF_ATTR, new StaffDTO());
        return mAV;
    }

    @PostMapping("/create")
    public RedirectView create(@ModelAttribute(STAFF_ATTR) StaffDTO staffDTO) {
        staffService.insertOrUpdate(modelMapper.map(staffDTO, Staff.class));
        return new RedirectView(ViewRouteHelper.STAFF_ROOT);
    }

    @GetMapping("/{id}")
    public ModelAndView get(@PathVariable("id") int id) {
        Optional<Staff> staffOptional = staffService.findById(id);
        if (staffOptional.isEmpty()) {
            return new ModelAndView(new RedirectView(ViewRouteHelper.STAFF_ROOT));
        }
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_UPDATE);
        StaffDTO staffDTO = modelMapper.map(staffOptional.get(), StaffDTO.class);
        mAV.addObject(STAFF_ATTR, staffDTO);
        return mAV;
    }

    @GetMapping("/partial/{id}")
    public ModelAndView getPartial(@PathVariable("id") int id) {
        Optional<Staff> staffOptional = staffService.findById(id);
        if (staffOptional.isEmpty()) {
            return new ModelAndView(new RedirectView(ViewRouteHelper.STAFF_ROOT));
        }
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_PARTIAL_VIEW);
        StaffDTO staffDTO = modelMapper.map(staffOptional.get(), StaffDTO.class);
        mAV.addObject(STAFF_ATTR, staffDTO);
        return mAV;
    }

    @GetMapping("/by_dni/{dni}")
    public ModelAndView getByDni(@PathVariable("dni") long dni) {
        Staff staff;
        try {
            staff = staffService.findByDni(dni);
        } catch (IllegalArgumentException e) {
            return new ModelAndView(new RedirectView(ViewRouteHelper.STAFF_ROOT));
        }
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.STAFF_UPDATE);
        StaffDTO staffDTO = modelMapper.map(staff, StaffDTO.class);
        mAV.addObject(STAFF_ATTR, staffDTO);
        return mAV;
    }

    @PostMapping("/update")
    public RedirectView update(@ModelAttribute(STAFF_ATTR) StaffDTO staffDTO) {
        staffService.findById(staffDTO.getIdStaff()).ifPresent(staffToUpdate -> {
            staffToUpdate.setNombre(staffDTO.getNombre());
            staffService.insertOrUpdate(staffToUpdate);
        });
        return new RedirectView(ViewRouteHelper.STAFF_ROOT);
    }

    @PostMapping("/delete/{id}")
    public RedirectView delete(@PathVariable("id") int id) {
        staffService.remove(id);
        return new RedirectView(ViewRouteHelper.STAFF_ROOT);
    }
}
