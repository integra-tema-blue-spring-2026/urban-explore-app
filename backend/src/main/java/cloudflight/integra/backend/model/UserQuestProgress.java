package cloudflight.integra.backend.model;

import cloudflight.integra.backend.model.utils.enums.QuestProgressStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_quest_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQuestProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "quest_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Quest quest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestProgressStatus status = QuestProgressStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    private LocalDateTime completedAt;
}
