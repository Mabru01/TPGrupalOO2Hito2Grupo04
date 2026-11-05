package TPGrupalOO2Hito2Grupo04.services.implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TPGrupalOO2Hito2Grupo04.dtos.FestivalDTO;
import TPGrupalOO2Hito2Grupo04.entities.Costo;
import TPGrupalOO2Hito2Grupo04.entities.Festival;
import TPGrupalOO2Hito2Grupo04.repositories.ICostoRepository;
import TPGrupalOO2Hito2Grupo04.repositories.IFestivalRepository;
import TPGrupalOO2Hito2Grupo04.services.IFestivalService;

@Service("festivalService")
public class FestivalService implements IFestivalService {
    private static final Logger logger = LoggerFactory.getLogger(FestivalService.class);

    private static final List<String> TEMPORADAS = List.of("Verano", "Otoño", "Invierno", "Primavera");

    private final IFestivalRepository festivalRepository;
    private final ICostoRepository costoRepository;

    public FestivalService(IFestivalRepository festivalRepository, ICostoRepository costoRepository) {
        this.festivalRepository = festivalRepository;
        this.costoRepository = costoRepository;
    }

    @Override
    public List<Festival> getAll() {
        return festivalRepository.findAll();
    }

    @Override
    public List<Festival> getAllWithUnidades() {
        return festivalRepository.findAllAndFetchUnidadesEagerly();
    }

    @Override
    public Optional<Festival> findById(long id) {
        return festivalRepository.findByIdFestival(id);
    }

    @Override
    public Festival findByNombre(String nombre) {
        return festivalRepository.findByNombre(nombre).orElseThrow(
                () -> new IllegalArgumentException("ERROR: Festival not found with nombre: " + nombre)
        );
    }

    @Override
    public Festival findByIdWithUnidades(long id) {
        return festivalRepository.findByIdAndFetchUnidadesEagerly(id).orElseThrow(
                () -> new IllegalArgumentException("ERROR: Festival not found with id: " + id)
        );
    }

    @Override
    public Festival insertOrUpdate(Festival festival) {
        return festivalRepository.save(festival);
    }

    @Override
    public boolean remove(long id) {
        try {
            festivalRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException | DataIntegrityViolationException e) {
            logger.warn("Could not remove Festival with id {}", id, e);
            return false;
        } catch (DataAccessException e) {
            logger.error("Database error while removing Festival with id {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FestivalDTO nuevoConCostosSugeridos() {
        FestivalDTO dto = new FestivalDTO();
        festivalRepository.findFirstByOrderByFechaInicioDesc()
                .map(Festival::getCosto)
                .ifPresent(costo -> {
                    dto.setCostoSuperficie(costo.getCostoSuperficie());
                    dto.setCostoMontaje(costo.getCostoMontaje());
                    dto.setPlusElectricidad(costo.getPlusElectricidad());
                    dto.setSueldoBase(costo.getSueldoBase());
                    dto.setAntiguedadPorAnio(costo.getAntiguedadPorAnio());
                    dto.setPlusCocinero(costo.getPlusCocinero());
                    dto.setPlusCocineroAyudante(costo.getPlusCocineroAyudante());
                    dto.setPlusLavaplatos(costo.getPlusLavaplatos());
                });
        return dto;
    }

    // El festival y sus costos se guardan juntos: si algo falla no queda un festival sin costos
    @Override
    @Transactional
    public Festival crearFestivalConCostos(FestivalDTO dto) {
        validarFestival(dto, null);
        validarCostos(dto);

        Festival festival = new Festival(dto.getNombre().trim(), dto.getTemporada(), dto.getFechaInicio(), dto.getFechaFin());
        festivalRepository.save(festival);

        Costo costo = new Costo(dto.getCostoSuperficie(), dto.getCostoMontaje(), dto.getPlusElectricidad(),
                dto.getSueldoBase(), festival);
        copiarCostos(dto, costo);
        costoRepository.save(costo);

        festival.setCosto(costo);
        return festival;
    }

    @Override
    @Transactional(readOnly = true)
    public FestivalDTO datosParaModificar(long idFestival) {
        Festival festival = traerFestival(idFestival);
        FestivalDTO dto = new FestivalDTO();
        dto.setNombre(festival.getNombre());
        dto.setTemporada(festival.getTemporada());
        dto.setFechaInicio(festival.getFechaInicio());
        dto.setFechaFin(festival.getFechaFin());
        Costo costo = festival.getCosto();
        if (costo != null) {
            dto.setCostoSuperficie(costo.getCostoSuperficie());
            dto.setCostoMontaje(costo.getCostoMontaje());
            dto.setPlusElectricidad(costo.getPlusElectricidad());
            dto.setSueldoBase(costo.getSueldoBase());
            dto.setAntiguedadPorAnio(costo.getAntiguedadPorAnio());
            dto.setPlusCocinero(costo.getPlusCocinero());
            dto.setPlusCocineroAyudante(costo.getPlusCocineroAyudante());
            dto.setPlusLavaplatos(costo.getPlusLavaplatos());
        }
        return dto;
    }

    // Próximo: se modifica todo. En curso: todo menos la fecha de inicio. Finalizado: no se modifica
    @Override
    @Transactional
    public Festival modificarFestival(long idFestival, FestivalDTO dto) {
        Festival festival = traerFestival(idFestival);
        if (festival.yaTermino(LocalDate.now())) {
            throw new IllegalArgumentException("El festival " + festival.getNombre()
                    + " ya terminó: no se puede modificar.");
        }
        validarFestival(dto, festival);
        validarCostos(dto);

        festival.setNombre(dto.getNombre().trim());
        festival.setTemporada(dto.getTemporada());
        festival.setFechaInicio(dto.getFechaInicio());
        festival.setFechaFin(dto.getFechaFin());
        festivalRepository.save(festival);

        Costo costo = festival.getCosto();
        if (costo == null) {
            costo = new Costo(dto.getCostoSuperficie(), dto.getCostoMontaje(), dto.getPlusElectricidad(),
                    dto.getSueldoBase(), festival);
            festival.setCosto(costo);
        }
        costo.setCostoSuperficie(dto.getCostoSuperficie());
        costo.setCostoMontaje(dto.getCostoMontaje());
        costo.setPlusElectricidad(dto.getPlusElectricidad());
        costo.setSueldoBase(dto.getSueldoBase());
        copiarCostos(dto, costo);
        costoRepository.save(costo);
        return festival;
    }

    private Festival traerFestival(long idFestival) {
        return festivalRepository.findByIdFestival(idFestival).orElseThrow(
                () -> new IllegalArgumentException("ERROR: Festival not found with id: " + idFestival)
        );
    }

    private void copiarCostos(FestivalDTO dto, Costo costo) {
        costo.setAntiguedadPorAnio(dto.getAntiguedadPorAnio());
        costo.setPlusCocinero(dto.getPlusCocinero());
        costo.setPlusCocineroAyudante(dto.getPlusCocineroAyudante());
        costo.setPlusLavaplatos(dto.getPlusLavaplatos());
    }

    // existente es null en el alta y el festival que se modifica en la modificación
    private void validarFestival(FestivalDTO dto, Festival existente) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del festival es obligatorio.");
        }
        if (dto.getNombre().trim().length() > 100) {
            throw new IllegalArgumentException("El nombre no puede superar los 100 caracteres.");
        }
        boolean nombreRepetido = existente == null
                ? festivalRepository.existsByNombreIgnoreCase(dto.getNombre().trim())
                : festivalRepository.existsByNombreIgnoreCaseAndIdFestivalNot(dto.getNombre().trim(), existente.getIdFestival());
        if (nombreRepetido) {
            throw new IllegalArgumentException("Ya existe un festival con el nombre " + dto.getNombre().trim() + ".");
        }
        if (!TEMPORADAS.contains(dto.getTemporada())) {
            throw new IllegalArgumentException("Elegí la temporada del festival.");
        }
        if (dto.getFechaInicio() == null || dto.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias.");
        }
        LocalDate hoy = LocalDate.now();
        if (existente != null && existente.yaEmpezo(hoy)) {
            // Festival en curso: la fecha de inicio queda como estaba y no puede terminar antes de hoy
            if (!dto.getFechaInicio().equals(existente.getFechaInicio())) {
                throw new IllegalArgumentException("El festival ya empezó: no se puede cambiar la fecha de inicio.");
            }
            if (dto.getFechaFin().isBefore(hoy)) {
                throw new IllegalArgumentException("El festival está en curso: la fecha de fin no puede ser anterior a hoy.");
            }
        } else if (dto.getFechaInicio().isBefore(hoy)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior a hoy.");
        }
        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio.");
        }
        // El registro de unidades toma el festival en curso: no puede haber dos festivales el mismo día
        boolean superpuesto = existente == null
                ? festivalRepository.existeFestivalEntre(dto.getFechaInicio(), dto.getFechaFin())
                : festivalRepository.existeOtroFestivalEntre(dto.getFechaInicio(), dto.getFechaFin(), existente.getIdFestival());
        if (superpuesto) {
            throw new IllegalArgumentException("Las fechas se superponen con otro festival.");
        }
    }

    private void validarCostos(FestivalDTO dto) {
        BigDecimal[] costos = {dto.getCostoSuperficie(), dto.getCostoMontaje(), dto.getPlusElectricidad(),
                dto.getSueldoBase(), dto.getAntiguedadPorAnio(), dto.getPlusCocinero(),
                dto.getPlusCocineroAyudante(), dto.getPlusLavaplatos()};
        for (BigDecimal costo : costos) {
            if (costo == null || costo.signum() < 0) {
                throw new IllegalArgumentException("Todos los costos son obligatorios y no pueden ser negativos.");
            }
        }
        if (dto.getSueldoBase().signum() == 0) {
            throw new IllegalArgumentException("El sueldo base tiene que ser mayor a 0.");
        }
    }
}
