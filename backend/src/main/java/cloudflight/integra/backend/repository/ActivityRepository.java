package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.Activity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    
    @Query(value = """
        SELECT a.* FROM activities a 
        WHERE a.user_id IN (
            SELECT u.id FROM users u
            JOIN user_followers f ON u.id = f.user_id
            WHERE f.follower_id = :userId
        )
        ORDER BY a.created_at DESC
    """, nativeQuery = true)
    List<Activity> findFriendsActivities(@Param("userId") UUID userId, Pageable pageable);

    List<Activity> findByUserId(UUID userId);

    List<Activity> findByTargetId(UUID targetId);
}
