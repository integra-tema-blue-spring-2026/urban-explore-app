package cloudflight.integra.backend.quests;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloudflight.integra.backend.quests.model.QuestDto;


@RestController
@RequestMapping("/api/quests")
public class QuestController {
    private final QuestService service;
    private final QuestMapper mapper;

    public QuestController(QuestService service, QuestMapper mapper){
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<QuestDto> getAllQuests(){
        return service.getAllQuests().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public QuestDto getQuestById(@PathVariable Long id){
        return service.findById(id).map(mapper::toDto).orElse(null);
    }

    @PostMapping
    public QuestDto create(@RequestBody QuestDto quest){
        return mapper.toDto(service.create(mapper.toEntity(quest)));
    }

    @PutMapping("/{id}")
    public QuestDto update(@PathVariable Long id, @RequestBody QuestDto quest){
        return service.update(id, mapper.toEntity(quest)).map(mapper::toDto).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    } 
}
