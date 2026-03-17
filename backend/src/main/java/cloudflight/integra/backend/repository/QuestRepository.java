package cloudflight.integra.backend.repository;

import org.springframework.stereotype.Repository;
import cloudflight.integra.backend.model.Quest;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface QuestRepository extends JpaRepository<Quest, UUID>{

}
