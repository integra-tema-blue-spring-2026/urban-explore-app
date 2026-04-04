package cloudflight.integra.backend.model.utils.mappers;

import org.springframework.stereotype.Component;

import cloudflight.integra.backend.model.Quest;
import cloudflight.integra.backend.model.dtos.quest.CreateQuestDto;
import cloudflight.integra.backend.model.dtos.quest.QuestDto;

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

