package cloudflight.integra.backend.model.dtos.quest;

import cloudflight.integra.backend.model.utils.enums.QuestProgressStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuestProgressDto {

    @NotNull(message = "Quest ID cannot be null")
    private UUID questId;

    @NotNull(message = "Status cannot be null")
    private QuestProgressStatus status;
}
