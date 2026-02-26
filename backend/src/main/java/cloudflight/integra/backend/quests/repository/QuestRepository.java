package cloudflight.integra.backend.quests.repository;

import org.springframework.stereotype.Repository;
import cloudflight.integra.backend.quests.model.entity.Quest;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface QuestRepository extends JpaRepository<Quest, UUID>{

}
