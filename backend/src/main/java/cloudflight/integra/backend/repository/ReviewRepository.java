package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByPoiId(UUID poiId);
    List<Review> findByUserId(UUID userId);
    List<Review> findByPoiIdAndUserId(UUID poiId, UUID userId);
}
