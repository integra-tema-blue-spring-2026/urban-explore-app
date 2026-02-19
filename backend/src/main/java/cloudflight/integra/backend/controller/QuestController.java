package cloudflight.integra.backend.controller;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cloudflight.integra.backend.quests.model.dto.QuestDto;
import cloudflight.integra.backend.quests.service.QuestService;
import cloudflight.integra.backend.utils.QuestMapper;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/quests")
public class QuestController {
    private final QuestService questService;
    private final QuestMapper mapper;

    public QuestController(QuestService questService, QuestMapper mapper){
        this.questService = questService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<QuestDto> getAllQuests(){
        return questService.getAllQuests().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public QuestDto getQuestById(@Valid @PathVariable Long id){
        return mapper.toDto(questService.findById(id));
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public QuestDto create(@Valid @RequestBody QuestDto quest){
        return mapper.toDto(questService.create(mapper.toEntity(quest)));
    }

    @PutMapping("/{id}")
    public QuestDto update(@Valid @PathVariable Long id, @Valid @RequestBody QuestDto quest){
        return mapper.toDto(questService.update(id, mapper.toEntity(quest)));
    }

    @DeleteMapping("/{id}")
    public void delete(@Valid @PathVariable Long id){
        questService.delete(id);
    } 
}