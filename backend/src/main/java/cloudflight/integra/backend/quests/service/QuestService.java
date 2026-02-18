package cloudflight.integra.backend.quests.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cloudflight.integra.backend.quests.model.entity.Quest;
import cloudflight.integra.backend.quests.repository.QuestRepository;

@Service
public class QuestService {
    private final QuestRepository repo;

    public QuestService(QuestRepository repo){ this.repo = repo; }

    public List<Quest> getAllQuests(){
        return repo.findAll();
    }

    public Optional<Quest> findById(Long id){
        return repo.findById(id);
    }

    public Quest create(Quest quest){
        return repo.save(quest);
    }

    public void delete(Long id){
        repo.deleteById(id);
    }

    public Optional<Quest> update(Long id, Quest quest) {
        if (repo.findById(id).isPresent()) {
            quest.setId(id);
            return Optional.of(repo.save(quest));
        }
        return Optional.empty();
    }
}
