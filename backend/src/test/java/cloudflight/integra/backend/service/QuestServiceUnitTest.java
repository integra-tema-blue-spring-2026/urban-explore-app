package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.QuestNotFoundException;
import cloudflight.integra.backend.model.Quest;
import cloudflight.integra.backend.model.utils.enums.Enums.Difficulty;
import cloudflight.integra.backend.repository.QuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestServiceUnitTest {

    @Mock
    private QuestRepository questRepo;

    @InjectMocks
    private QuestService questService;

    private Quest testQuest;
    private UUID testQuestId;
    private UUID testCityId;

    @BeforeEach
    void setUp() {
        testQuestId = UUID.randomUUID();
        testCityId = UUID.randomUUID();
        testQuest = new Quest(
            testQuestId,
            "Explore the Old Town",
            "Visit all landmarks in the old town.",
            Difficulty.EASY,
            100,
            testCityId
        );
    }

    @Test
    void getAllQuests_ShouldReturnAllQuests() {
        when(questRepo.findAll()).thenReturn(List.of(testQuest));

        List<Quest> result = questService.getAllQuests();

        assertEquals(1, result.size());
        assertEquals("Explore the Old Town", result.getFirst().getTitle());
        verify(questRepo, times(1)).findAll();
    }

    @Test
    void getAllQuests_ShouldReturnEmptyList_WhenNoQuestsExist() {
        when(questRepo.findAll()).thenReturn(Collections.emptyList());

        List<Quest> result = questService.getAllQuests();

        assertTrue(result.isEmpty());
        verify(questRepo, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnQuest_WhenQuestExists() {
        when(questRepo.findById(testQuestId)).thenReturn(Optional.of(testQuest));

        Quest result = questService.findById(testQuestId);

        assertNotNull(result);
        assertEquals(testQuestId, result.getId());
        assertEquals("Explore the Old Town", result.getTitle());
        verify(questRepo, times(1)).findById(testQuestId);
    }

    @Test
    void findById_ShouldThrowException_WhenQuestNotFound() {
        when(questRepo.findById(testQuestId)).thenReturn(Optional.empty());

        QuestNotFoundException exception = assertThrows(QuestNotFoundException.class,
            () -> questService.findById(testQuestId));

        assertEquals("Quest with ID " + testQuestId + " not found", exception.getMessage());
        verify(questRepo, times(1)).findById(testQuestId);
    }

    @Test
    void create_ShouldReturnSavedQuest() {
        when(questRepo.save(any(Quest.class))).thenReturn(testQuest);

        Quest result = questService.create(testQuest);

        assertNotNull(result);
        assertEquals("Explore the Old Town", result.getTitle());
        assertEquals(Difficulty.EASY, result.getDifficulty());
        verify(questRepo, times(1)).save(testQuest);
    }

    @Test
    void update_ShouldReturnUpdatedQuest_WhenQuestExists() {
        Quest updatedQuest = new Quest(testQuestId, "New Title", "New Desc", Difficulty.HARD, 200, testCityId);
        when(questRepo.existsById(testQuestId)).thenReturn(true);
        when(questRepo.save(any(Quest.class))).thenAnswer(inv -> inv.getArgument(0));

        Quest result = questService.update(testQuestId, updatedQuest);

        assertNotNull(result);
        assertEquals(testQuestId, result.getId());
        assertEquals("New Title", result.getTitle());
        assertEquals(Difficulty.HARD, result.getDifficulty());
        verify(questRepo, times(1)).existsById(testQuestId);
        verify(questRepo, times(1)).save(updatedQuest);
    }

    @Test
    void update_ShouldThrowException_WhenQuestNotFound() {
        when(questRepo.existsById(testQuestId)).thenReturn(false);

        QuestNotFoundException exception = assertThrows(QuestNotFoundException.class,
            () -> questService.update(testQuestId, testQuest));

        assertEquals("Quest with ID " + testQuestId + " not found", exception.getMessage());
        verify(questRepo, times(1)).existsById(testQuestId);
        verify(questRepo, never()).save(any());
    }

    @Test
    void delete_ShouldDeleteQuest_WhenQuestExists() {
        when(questRepo.existsById(testQuestId)).thenReturn(true);

        questService.delete(testQuestId);

        verify(questRepo, times(1)).existsById(testQuestId);
        verify(questRepo, times(1)).deleteById(testQuestId);
    }

    @Test
    void delete_ShouldThrowException_WhenQuestNotFound() {
        when(questRepo.existsById(testQuestId)).thenReturn(false);

        QuestNotFoundException exception = assertThrows(QuestNotFoundException.class,
            () -> questService.delete(testQuestId));

        assertEquals("Quest with ID " + testQuestId + " not found", exception.getMessage());
        verify(questRepo, times(1)).existsById(testQuestId);
        verify(questRepo, never()).deleteById(any());
    }
}
