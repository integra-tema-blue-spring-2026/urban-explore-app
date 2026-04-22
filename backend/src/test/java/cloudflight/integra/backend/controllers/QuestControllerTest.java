package cloudflight.integra.backend.controllers;

import cloudflight.integra.backend.controller.QuestController;
import cloudflight.integra.backend.exceptions.custom.QuestNotFoundException;
import cloudflight.integra.backend.model.Quest;
import cloudflight.integra.backend.model.dtos.quest.CreateQuestDto;
import cloudflight.integra.backend.model.dtos.quest.QuestDto;
import cloudflight.integra.backend.model.utils.enums.Enums.Difficulty;
import cloudflight.integra.backend.model.utils.mappers.QuestMapper;
import cloudflight.integra.backend.service.QuestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuestController.class)
class QuestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QuestService questService;

    @MockitoBean
    private QuestMapper mapper;

    private UUID questId;
    private UUID cityId;
    private Quest testQuest;
    private QuestDto testQuestDto;
    private CreateQuestDto createQuestDto;

    @BeforeEach
    void setUp() {
        questId = UUID.randomUUID();
        cityId = UUID.randomUUID();

        testQuest = new Quest(questId, "Explore the Old Town", "Visit all landmarks.", Difficulty.EASY, 100, cityId);

        testQuestDto = new QuestDto(
            questId,
            "Explore the Old Town",
            "Visit all landmarks.",
            Difficulty.EASY,
            100,
            cityId);

        createQuestDto = new CreateQuestDto(
            "Explore the Old Town",
            "Visit all landmarks.",
            Difficulty.EASY,
            100,
            cityId);
    }


    @Test
    void getAllQuests_ShouldReturn200WithQuestList() throws Exception {
        when(questService.getAllQuests()).thenReturn(List.of(testQuest));
        when(mapper.toDto(testQuest)).thenReturn(testQuestDto);

        mockMvc.perform(get("/quests"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Explore the Old Town"))
            .andExpect(jsonPath("$[0].difficulty").value("EASY"));
    }

    @Test
    void getAllQuests_ShouldReturn200WithEmptyList_WhenNoQuestsExist() throws Exception {
        when(questService.getAllQuests()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/quests"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    void getQuestById_ShouldReturn200WithQuest() throws Exception {
        when(questService.findById(questId)).thenReturn(testQuest);
        when(mapper.toDto(testQuest)).thenReturn(testQuestDto);

        mockMvc.perform(get("/quests/{id}", questId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Explore the Old Town"))
            .andExpect(jsonPath("$.exp").value(100));
    }

    @Test
    void getQuestById_ShouldReturn404_WhenQuestNotFound() throws Exception {
        when(questService.findById(questId))
            .thenThrow(new QuestNotFoundException("Quest with ID " + questId + " not found"));

        mockMvc.perform(get("/quests/{id}", questId))
            .andExpect(status().isNotFound());
    }


    @Test
    void create_ShouldReturn201AndCreatedQuest() throws Exception {
        when(mapper.toEntityFromCreateQuestDto(any(CreateQuestDto.class))).thenReturn(testQuest);
        when(questService.create(any(Quest.class))).thenReturn(testQuest);
        when(mapper.toDto(testQuest)).thenReturn(testQuestDto);

        mockMvc.perform(post("/quests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createQuestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Explore the Old Town"))
            .andExpect(jsonPath("$.difficulty").value("EASY"));
    }


    @Test
    void update_ShouldReturn200AndUpdatedQuest() throws Exception {
        CreateQuestDto updateDto = new CreateQuestDto(
            "Updated Title",
            "Updated desc.",
            Difficulty.HARD,
            300,
            cityId);

        QuestDto updatedDto = new QuestDto(
            questId,
            "Updated Title",
            "Updated desc.",
            Difficulty.HARD,
            300,
            cityId);

        when(mapper.toEntityFromCreateQuestDto(any(CreateQuestDto.class))).thenReturn(testQuest);
        when(questService.update(eq(questId), any(Quest.class))).thenReturn(testQuest);
        when(mapper.toDto(testQuest)).thenReturn(updatedDto);

        mockMvc.perform(put("/quests/{id}", questId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Title"))
            .andExpect(jsonPath("$.difficulty").value("HARD"));
    }

    @Test
    void update_ShouldReturn404_WhenQuestNotFound() throws Exception {
        when(mapper.toEntityFromCreateQuestDto(any(CreateQuestDto.class))).thenReturn(testQuest);
        when(questService.update(eq(questId), any(Quest.class)))
            .thenThrow(new QuestNotFoundException("Quest with ID " + questId + " not found"));

        mockMvc.perform(put("/quests/{id}", questId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createQuestDto)))
            .andExpect(status().isNotFound());
    }


    @Test
    void delete_ShouldReturn200_WhenQuestExists() throws Exception {
        doNothing().when(questService).delete(questId);

        mockMvc.perform(delete("/quests/{id}", questId))
            .andExpect(status().isOk());
    }

    @Test
    void delete_ShouldReturn404_WhenQuestNotFound() throws Exception {
        doThrow(new QuestNotFoundException("Quest with ID " + questId + " not found"))
            .when(questService).delete(questId);

        mockMvc.perform(delete("/quests/{id}", questId))
            .andExpect(status().isNotFound());
    }
}
