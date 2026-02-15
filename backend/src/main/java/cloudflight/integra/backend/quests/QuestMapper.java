package cloudflight.integra.backend.quests;

import org.springframework.stereotype.Component;

import cloudflight.integra.backend.quests.model.Quest;
import cloudflight.integra.backend.quests.model.QuestDto;

@Component
public class QuestMapper {
    public QuestDto toDto(Quest quest) { 
        return new QuestDto(quest.getId(),quest.getTitle(), quest.getDescription(), 
        quest.getDifficulty(), quest.getExp(), quest.getCityId()); 
    }

    public Quest toEntity(QuestDto quest){
        return new Quest(quest.id(), quest.title(), quest.description(), 
        quest.difficulty(), quest.exp(), quest.cityId());
    }
}
