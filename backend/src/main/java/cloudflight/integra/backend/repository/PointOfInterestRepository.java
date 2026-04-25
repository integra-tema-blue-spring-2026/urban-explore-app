package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.utils.enums.PointOfInterestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, UUID> {
	List<PointOfInterest> findAllByStatus(PointOfInterestStatus status);
}
