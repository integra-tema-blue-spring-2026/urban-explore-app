package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, UUID> {
    @Query("SELECT p FROM PointOfInterest p JOIN FETCH p.city WHERE p.id IN :ids")
    List<PointOfInterest> findAllByIdWithCity(@Param("ids") Iterable<UUID> ids);

}
