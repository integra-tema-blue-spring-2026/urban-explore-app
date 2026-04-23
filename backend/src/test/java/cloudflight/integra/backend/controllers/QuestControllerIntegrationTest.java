package cloudflight.integra.backend.controllers;

import cloudflight.integra.backend.exceptions.custom.QuestNotFoundException;
import cloudflight.integra.backend.model.Quest;
import cloudflight.integra.backend.model.dtos.quest.CreateQuestDto;
import cloudflight.integra.backend.model.dtos.quest.QuestDto;
import cloudflight.integra.backend.model.utils.enums.Enums.Difficulty;
import cloudflight.integra.backend.model.utils.mappers.QuestMapper;
import cloudflight.integra.backend.service.QuestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class QuestControllerIntegrationTest extends BaseControllerIntegrationTest {

    @MockitoBean
    private QuestService questService;

    @MockitoBean
    private QuestMapper mapper;

    private String token;
    private UUID questId;
    private UUID cityId;
    private Quest testQuest;
    private QuestDto testQuestDto;
    private CreateQuestDto createQuestDto;

    @BeforeEach
    void setUp() {
        questId = UUID.randomUUID();
        cityId = UUID.randomUUID();

        testQuest = new Quest(
            questId, "Explore the Old Town", "Visit all landmarks.", Difficulty.EASY, 100, cityId);

        testQuestDto = new QuestDto(
            questId, "Explore the Old Town", "Visit all landmarks.", Difficulty.EASY, 100, cityId);

        createQuestDto = new CreateQuestDto(
            "Explore the Old Town", "Visit all landmarks.", Difficulty.EASY, 100, cityId);

        token = setupAuthAndGetToken();
    }


    @Test
    void getAllQuests_ShouldReturn200WithQuestList() {
        when(questService.getAllQuests()).thenReturn(List.of(testQuest));
        when(mapper.toDto(testQuest)).thenReturn(testQuestDto);

        ResponseEntity<List> response = restTemplate.exchange(
            "/quests", HttpMethod.GET, authEntity(token), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getAllQuests_ShouldReturn200WithEmptyList_WhenNoQuestsExist() {
        when(questService.getAllQuests()).thenReturn(Collections.emptyList());

        ResponseEntity<List> response = restTemplate.exchange(
            "/quests", HttpMethod.GET, authEntity(token), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getAllQuests_ShouldReturn401_WhenNoTokenProvided() {
        ResponseEntity<String> response = restTemplate.getForEntity("/quests", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    void getQuestById_ShouldReturn200WithQuest() {
        when(questService.findById(questId)).thenReturn(testQuest);
        when(mapper.toDto(testQuest)).thenReturn(testQuestDto);

        ResponseEntity<QuestDto> response = restTemplate.exchange(
            "/quests/" + questId, HttpMethod.GET, authEntity(token), QuestDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("Explore the Old Town");
        assertThat(response.getBody().getExp()).isEqualTo(100);
    }

    @Test
    void getQuestById_ShouldReturn404_WhenQuestNotFound() {
        when(questService.findById(questId))
            .thenThrow(new QuestNotFoundException("Quest with ID " + questId + " not found"));

        ResponseEntity<String> response = restTemplate.exchange(
            "/quests/" + questId, HttpMethod.GET, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void create_ShouldReturn201AndCreatedQuest() {
        when(mapper.toEntityFromCreateQuestDto(any(CreateQuestDto.class))).thenReturn(testQuest);
        when(questService.create(any(Quest.class))).thenReturn(testQuest);
        when(mapper.toDto(testQuest)).thenReturn(testQuestDto);

        ResponseEntity<QuestDto> response = restTemplate.exchange(
            "/quests", HttpMethod.POST, authEntity(createQuestDto, token), QuestDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getTitle()).isEqualTo("Explore the Old Town");
        assertThat(response.getBody().getDifficulty()).isEqualTo(Difficulty.EASY);
    }

    @Test
    void create_ShouldReturn401_WhenNoTokenProvided() {
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/quests", createQuestDto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    void update_ShouldReturn200AndUpdatedQuest() {
        CreateQuestDto updateDto = new CreateQuestDto(
            "Updated Title", "Updated desc.", Difficulty.HARD, 300, cityId);

        QuestDto updatedDto = new QuestDto(
            questId, "Updated Title", "Updated desc.", Difficulty.HARD, 300, cityId);

        when(mapper.toEntityFromCreateQuestDto(any(CreateQuestDto.class))).thenReturn(testQuest);
        when(questService.update(eq(questId), any(Quest.class))).thenReturn(testQuest);
        when(mapper.toDto(testQuest)).thenReturn(updatedDto);

        ResponseEntity<QuestDto> response = restTemplate.exchange(
            "/quests/" + questId, HttpMethod.PUT, authEntity(updateDto, token), QuestDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("Updated Title");
        assertThat(response.getBody().getDifficulty()).isEqualTo(Difficulty.HARD);
    }

    @Test
    void update_ShouldReturn404_WhenQuestNotFound() {
        when(mapper.toEntityFromCreateQuestDto(any(CreateQuestDto.class))).thenReturn(testQuest);
        when(questService.update(eq(questId), any(Quest.class)))
            .thenThrow(new QuestNotFoundException("Quest with ID " + questId + " not found"));

        ResponseEntity<String> response = restTemplate.exchange(
            "/quests/" + questId, HttpMethod.PUT, authEntity(createQuestDto, token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void delete_ShouldReturn200_WhenQuestExists() {
        doNothing().when(questService).delete(questId);

        ResponseEntity<Void> response = restTemplate.exchange(
            "/quests/" + questId, HttpMethod.DELETE, authEntity(token), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(questService, times(1)).delete(questId);
    }

    @Test
    void delete_ShouldReturn404_WhenQuestNotFound() {
        doThrow(new QuestNotFoundException("Quest with ID " + questId + " not found"))
            .when(questService).delete(questId);

        ResponseEntity<String> response = restTemplate.exchange(
            "/quests/" + questId, HttpMethod.DELETE, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
