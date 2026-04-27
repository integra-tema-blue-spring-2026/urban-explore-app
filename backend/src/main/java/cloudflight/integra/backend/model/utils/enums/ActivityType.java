package cloudflight.integra.backend.model.utils.enums;

public enum ActivityType {
    REVIEW_CREATED("left a review"),
    POI_CREATED("added a new POI"),
    QUEST_COMPLETED("completed a quest");

    private final String description;

    ActivityType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
