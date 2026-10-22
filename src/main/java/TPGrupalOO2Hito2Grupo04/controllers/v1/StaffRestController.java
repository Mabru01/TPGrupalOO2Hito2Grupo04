package TPGrupalOO2Hito2Grupo04.controllers.v1;

import TPGrupalOO2Hito2Grupo04.services.IStaffService;
import TPGrupalOO2Hito2Grupo04.dtos.StaffDTO;
import TPGrupalOO2Hito2Grupo04.entities.Staff;
import  TPGrupalOO2Hito2Grupo04.dtos.RequestStaffDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/persons")
public class StaffRestController {

    private final IStaffService staffService;

    private final ModelMapper modelMapper;

    public StaffRestController(IStaffService staffService, ModelMapper modelMapper){
        this.staffService = staffService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StaffDTO>> getStaffs(){
        return new ResponseEntity<>(
                staffService.getAll().stream()
                        .map(person -> modelMapper.map(person, StaffDTO.class))
                        .toList()
                ,HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StaffDTO> getPersonById(@PathVariable(name = "id") int id) {
        Optional<Staff> staffOptional = staffService.findById(id);
        return staffOptional
                .map(person -> new ResponseEntity<>(modelMapper.map(person, StaffDTO.class), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/dni", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StaffDTO> getStaffByDni(@RequestParam(value = "dni") long dni) {
        return new ResponseEntity<>(
                modelMapper.map(staffService.findByDni(dni), StaffDTO.class)
                ,HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StaffDTO> postStaff(@RequestBody RequestStaffDTO requestStaffDTO){
        return new ResponseEntity<>(
                modelMapper.map(staffService.insertOrUpdate(
                        modelMapper.map(requestStaffDTO, Staff.class)
                ),StaffDTO.class)
                , HttpStatus.CREATED);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StaffDTO> putStaff(@RequestBody StaffDTO staffDTO) {
        Optional<Staff> staff = staffService.findById(staffDTO.getIdStaff());
        return staff
                .map(existing -> {
                    existing.setNombre(staffDTO.getNombre());
                    existing.setFechaNacimiento(staffDTO.getFechaNacimiento());
                    return new ResponseEntity<>(
                            modelMapper.map(staffService.insertOrUpdate(existing), StaffDTO.class),
                            HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deletePersonById(@PathVariable(name = "id") long id) {
        return new ResponseEntity<>(
                "Deleted Staff with Id " + id + " : " + staffService.remove(id)
                ,HttpStatus.OK);
    }

}
