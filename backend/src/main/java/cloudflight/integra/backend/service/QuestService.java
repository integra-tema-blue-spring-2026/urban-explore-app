package cloudflight.integra.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloudflight.integra.backend.model.Quest;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.UserQuestCompletion;
import cloudflight.integra.backend.repository.QuestRepository;
import cloudflight.integra.backend.repository.UserQuestCompletionRepository;

import cloudflight.integra.backend.exceptions.custom.QuestNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestRepository questRepo;
    private final UserQuestCompletionRepository userQuestCompletionRepository;
    private final ActivityService activityService;

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
    public UserQuestCompletion completeQuest(UUID questId, User user) {
        Quest quest = findById(questId);

        var existingCompletion = userQuestCompletionRepository.findByUserIdAndQuestId(user.getId(), questId);
        if (existingCompletion.isPresent()) {
            log.warn("User {} has already completed quest {}", user.getId(), questId);
            return existingCompletion.get();
        }

        UserQuestCompletion completion = UserQuestCompletion.builder()
            .user(user)
            .quest(quest)
            .build();

        UserQuestCompletion saved = userQuestCompletionRepository.save(completion);

        activityService.createQuestCompletionActivity(questId, quest.getTitle(), user);

        log.info("User {} completed quest {}", user.getId(), questId);
        return saved;
    }
}
