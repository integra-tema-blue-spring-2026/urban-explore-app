package cloudflight.integra.backend.quests.model;

import cloudflight.integra.backend.quests.model.Quest.Difficulty;

public record QuestDto (Long id, String title, String description, Difficulty difficulty, int exp, Long cityId) {}
