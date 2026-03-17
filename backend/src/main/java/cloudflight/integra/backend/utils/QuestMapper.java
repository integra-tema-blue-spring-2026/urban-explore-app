package cloudflight.integra.backend.utils;

import org.springframework.stereotype.Component;

import cloudflight.integra.backend.quests.model.entity.Quest;
import cloudflight.integra.backend.quests.model.dto.CreateQuestDto;
import cloudflight.integra.backend.quests.model.dto.QuestDto;

@Component
public class QuestMapper {
    public QuestDto toDto(Quest quest) { 
        return new QuestDto(quest.getId(), quest.getTitle(), quest.getDescription(), 
        quest.getDifficulty(), quest.getExp(), quest.getCityId()); 
    }

    public Quest toEntity(QuestDto quest){
        return new Quest(quest.getId(), quest.getTitle(), quest.getDescription(), 
        quest.getDifficulty(), quest.getExp(), quest.getCityId());
    }

    public Quest toEntityFromCreateQuestDto(CreateQuestDto quest){
        return new Quest(null, quest.getTitle(), quest.getDescription(), 
        quest.getDifficulty(), quest.getExp(), quest.getCityId());
    }
}
    
