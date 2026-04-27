package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, UUID>,
	JpaSpecificationExecutor<PointOfInterest> {

}
