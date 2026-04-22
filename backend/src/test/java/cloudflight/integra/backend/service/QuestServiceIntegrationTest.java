package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.QuestNotFoundException;
import cloudflight.integra.backend.model.Quest;
import cloudflight.integra.backend.model.utils.enums.Enums.Difficulty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QuestService.class)
class QuestServiceIntegrationTest {

    @Autowired
    private QuestService questService;

    private UUID testCityId;

    @BeforeEach
    void setUp() {
        testCityId = UUID.randomUUID();
    }

    private Quest buildQuest(String title, Difficulty difficulty, int exp) {
        Quest q = new Quest();
        q.setTitle(title);
        q.setDescription("A quest description.");
        q.setDifficulty(difficulty);
        q.setExp(exp);
        q.setCityId(testCityId);
        return q;
    }

    @Test
    void create_ShouldPersistQuest() {
        Quest saved = questService.create(buildQuest("Explore the Old Town", Difficulty.EASY, 100));

        assertNotNull(saved.getId());
        assertEquals("Explore the Old Town", saved.getTitle());
        assertEquals(1, questService.getAllQuests().size());
    }

    @Test
    void findById_ShouldReturnQuest_WhenQuestExists() {
        Quest saved = questService.create(buildQuest("Find the Hidden Garden", Difficulty.MEDIUM, 150));

        Quest found = questService.findById(saved.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void findById_ShouldThrowException_WhenQuestNotFound() {
        UUID randomId = UUID.randomUUID();

        assertThrows(QuestNotFoundException.class, () -> questService.findById(randomId));
    }

    @Test
    void update_ShouldPersistChanges() {
        Quest saved = questService.create(buildQuest("Original Title", Difficulty.EASY, 50));

        Quest updatePayload = new Quest(saved.getId(), "Updated Title", "Updated desc.", Difficulty.HARD, 300, testCityId);
        Quest updated = questService.update(saved.getId(), updatePayload);

        assertEquals("Updated Title", updated.getTitle());
        assertEquals(Difficulty.HARD, updated.getDifficulty());
        assertEquals(300, updated.getExp());
        assertEquals("Updated Title", questService.findById(saved.getId()).getTitle());
    }

    @Test
    void update_ShouldThrowException_WhenQuestNotFound() {
        UUID randomId = UUID.randomUUID();
        Quest payload = buildQuest("Doesn't matter", Difficulty.EASY, 0);

        assertThrows(QuestNotFoundException.class, () -> questService.update(randomId, payload));
    }

    @Test
    void delete_ShouldRemoveQuest() {
        Quest saved = questService.create(buildQuest("Quest to Delete", Difficulty.EASY, 10));

        questService.delete(saved.getId());

        assertTrue(questService.getAllQuests().isEmpty());
    }

    @Test
    void delete_ShouldThrowException_WhenQuestNotFound() {
        UUID randomId = UUID.randomUUID();

        assertThrows(QuestNotFoundException.class, () -> questService.delete(randomId));
    }

    @Test
    void getAllQuests_ShouldReturnAllPersistedQuests() {
        questService.create(buildQuest("Quest One", Difficulty.EASY, 50));
        questService.create(buildQuest("Quest Two", Difficulty.MEDIUM, 100));
        questService.create(buildQuest("Quest Three", Difficulty.HARD, 200));

        List<Quest> all = questService.getAllQuests();

        assertEquals(3, all.size());
    }
}
