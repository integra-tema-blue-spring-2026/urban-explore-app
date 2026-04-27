package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;

@Transactional(readOnly = true)
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    
    @Query("""
        SELECT r FROM Review r 
        LEFT JOIN FETCH r.user 
        LEFT JOIN FETCH r.pointOfInterest 
        WHERE r.pointOfInterest.id = :poiId
    """)
    List<Review> findByPointOfInterestId(@Param("poiId") UUID poiId);
    
    List<Review> findByUserId(UUID userId);
    
    @Query("""
        SELECT r FROM Review r 
        LEFT JOIN FETCH r.user 
        LEFT JOIN FETCH r.pointOfInterest 
        WHERE r.pointOfInterest.id = :poiId AND r.user.id = :userId
    """)
    List<Review> findByPointOfInterestIdAndUserId(@Param("poiId") UUID poiId, @Param("userId") UUID userId);
}
