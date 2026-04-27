package cloudflight.integra.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloudflight.integra.backend.model.Quest;
import cloudflight.integra.backend.model.UserQuestProgress;
import cloudflight.integra.backend.model.utils.enums.QuestProgressStatus;
import cloudflight.integra.backend.repository.QuestRepository;
import cloudflight.integra.backend.repository.UserQuestProgressRepository;

import cloudflight.integra.backend.exceptions.custom.QuestNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import cloudflight.integra.backend.events.QuestCompletedEvent;

@Service
public class QuestService {
    private final QuestRepository questRepo;
    private final UserQuestProgressRepository userQuestProgressRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public QuestService(QuestRepository questRepo, UserQuestProgressRepository userQuestProgressRepository, ApplicationEventPublisher applicationEventPublisher){ 
        this.questRepo = questRepo;
        this.userQuestProgressRepository = userQuestProgressRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<Quest> getAllQuests(){
        return questRepo.findAll();
    }

    public Quest findById(UUID id){
        return questRepo.findById(id).orElseThrow(() ->
            new QuestNotFoundException("Quest with ID " + id + " not found"));
    }

    public Quest create(Quest quest){
        return questRepo.save(quest);
    }

    public void delete(UUID id){
        if (!questRepo.existsById(id)){
            throw new QuestNotFoundException("Quest with ID " + id + " not found");
        }
        questRepo.deleteById(id);
    }

    public Quest update(UUID id, Quest quest) {
        if (!questRepo.existsById(id)) {
            throw new QuestNotFoundException("Quest with ID " + id + " not found");
        }
        quest.setId(id);
        return questRepo.save(quest);
    }

    @Transactional
    public UserQuestProgress updateQuestProgress(UUID userId, UUID questId, QuestProgressStatus status) {
        UserQuestProgress progress = userQuestProgressRepository.findByUserIdAndQuestId(userId, questId)
            .orElseThrow(() -> new QuestNotFoundException(
                "Quest progress not found for user " + userId + " and quest " + questId));

        progress.setStatus(status);
        if (status == QuestProgressStatus.COMPLETED) {
            progress.setCompletedAt(LocalDateTime.now());
            applicationEventPublisher.publishEvent(new QuestCompletedEvent(userId, progress.getQuest()));
        }

        return userQuestProgressRepository.save(progress);
    }
}
