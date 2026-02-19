package cloudflight.integra.backend.quests.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cloudflight.integra.backend.quests.model.entity.Quest;
import cloudflight.integra.backend.quests.repository.QuestRepository;

import cloudflight.integra.backend.utils.exception.QuestNotFoundException;

@Service
public class QuestService {
    private final QuestRepository questRepo;

    public QuestService(QuestRepository questRepo){ this.questRepo = questRepo; }

    public List<Quest> getAllQuests(){
        return questRepo.findAll();
    }

    public Quest findById(Long id){
        return questRepo.findById(id).orElseThrow(() -> 
            new QuestNotFoundException("Quest with ID " + id + " not found"));   
    }

    public Quest create(Quest quest){
        return questRepo.save(quest);
    }

    public void delete(Long id){
        if (!questRepo.existsById(id)){
            throw new QuestNotFoundException("Quest with ID " + id + " not found");
        }
        questRepo.deleteById(id);
    }

    public Quest update(Long id, Quest quest) {
        if (!questRepo.existsById(id)) {
            throw new QuestNotFoundException("Quest with ID " + id + " not found");
        }
        quest.setId(id);
        return questRepo.save(quest);
    }
}
