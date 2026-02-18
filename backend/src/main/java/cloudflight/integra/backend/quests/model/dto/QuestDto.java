package cloudflight.integra.backend.quests.model.dto;

import jakarta.validation.constraints.NotNull;

import cloudflight.integra.backend.utils.enums.Difficulty;
import lombok.Data;


@Data
public class QuestDto {

    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotNull(message = "Title cannot be null")
    private String title;

    @NotNull(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Difficulty cannot be null")
    private Difficulty difficulty;

    @NotNull(message = "Experience points cannot be null")
    private int exp;

    @NotNull(message = "City ID cannot be null")
    private Long cityId;

    public QuestDto(Long id, String title, String description, Difficulty difficulty, int exp, Long cityId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.exp = exp;
        this.cityId = cityId;
    }

}