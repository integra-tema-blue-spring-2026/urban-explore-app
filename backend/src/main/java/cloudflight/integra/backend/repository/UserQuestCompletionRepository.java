package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.UserQuestCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserQuestCompletionRepository extends JpaRepository<UserQuestCompletion, UUID> {
    List<UserQuestCompletion> findByUserId(UUID userId);
    List<UserQuestCompletion> findByQuestId(UUID questId);
    Optional<UserQuestCompletion> findByUserIdAndQuestId(UUID userId, UUID questId);
}
