package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.utils.enums.CityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<City, UUID> {
    List<City> findByName(String name);
    List<City> findAllByStatus(CityStatus status);
}
