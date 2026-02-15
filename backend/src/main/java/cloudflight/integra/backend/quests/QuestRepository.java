package cloudflight.integra.backend.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import cloudflight.integra.backend.quests.model.Quest;

@Repository
public class QuestRepository {
    private final Map<Long, Quest> quests = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public List<Quest> getAllQuests(){
        return new ArrayList<>(quests.values());
    }

    public Optional<Quest> findById(Long id) {
        return Optional.ofNullable(quests.get(id));
    }

    public Quest save(Quest quest){
        if (quest.getId() == null){
            quest.setId(idGen.getAndIncrement());
        }
        quests.put(quest.getId(), quest);
        return quest;
    }

    public void delete(Long id){
        quests.remove(id);
    }
}
