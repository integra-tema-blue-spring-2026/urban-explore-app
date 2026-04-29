package cloudflight.integra.backend.repository;

import cloudflight.integra.backend.model.UserQuestProgress;
import cloudflight.integra.backend.model.utils.enums.QuestProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserQuestProgressRepository extends JpaRepository<UserQuestProgress, UUID> {
    List<UserQuestProgress> findAllByUserIdAndStatus(UUID userId, QuestProgressStatus status);

    Optional<UserQuestProgress> findByUserIdAndQuestId(UUID userId, UUID questId);
}
