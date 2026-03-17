package cloudflight.integra.backend.model.dtos.quest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import cloudflight.integra.backend.model.utils.enums.Enums.Difficulty;
import lombok.Data;


@Data
public class QuestDto {

    @NotNull(message = "ID cannot be null")
    private UUID id;

    @NotBlank(message = "Title cannot be null")
    private String title;

    @NotBlank(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Difficulty cannot be null")
    private Difficulty difficulty;

    @NotNull(message = "Experience points cannot be null")
    private int exp;

    @NotNull(message = "City ID cannot be null")
    private UUID cityId;

    public QuestDto(UUID id, String title, String description, Difficulty difficulty, int exp, UUID cityId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.exp = exp;
        this.cityId = cityId;
    }
}

