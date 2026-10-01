package TPGrupalOO2Hito2Grupo04.repositories;

import TPGrupalOO2Hito2Grupo04.entities.FoodTruck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("foodTruckRepository")
public interface IFoodTruckRepository extends JpaRepository<FoodTruck, Long> {

    // Consulta exclusiva de FoodTruck
    public abstract Optional<FoodTruck> findByPatente(String patente);
}