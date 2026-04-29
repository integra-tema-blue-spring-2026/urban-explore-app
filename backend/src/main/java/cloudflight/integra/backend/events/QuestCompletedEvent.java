package cloudflight.integra.backend.events;

import cloudflight.integra.backend.model.Quest;

import java.util.UUID;

public record QuestCompletedEvent(UUID userId, Quest quest) {
}
