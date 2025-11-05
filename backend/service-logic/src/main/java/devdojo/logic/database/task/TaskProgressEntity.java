package devdojo.logic.database.task;

import devdojo.logic.database.AbstractEntity;
import devdojo.logic.database.user.UserEntity;
import devdojo.schema.task.TaskProgress;
import devdojo.schema.task.TaskProgressState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "task_user_progress",
  uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "task_id"}),
  indexes = {
    @Index(name = "contactEmailContactIdAndName", columnList = "user_id, task_id")})
@NoArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
public final class TaskProgressEntity extends AbstractEntity implements TaskProgress {

  @NotNull(message = "User entity must not be null")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

  @NotNull(message = "Task entity must not be null")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", nullable = false)
  private TaskEntity taskEntity;

  @NotNull(message = "Input text must not be null")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String input;

  @NotNull(message = "State text must not be null")
  @Column(nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private TaskProgressState state;

  @NotNull(message = "Score text must not be null")
  @Column(nullable = false)
  private int score;

  public TaskProgressEntity(final UserEntity userEntity,
                            final TaskEntity taskEntity,
                            final String input,
                            final TaskProgressState state,
                            final int score) {
    this.userEntity = userEntity;
    this.taskEntity = taskEntity;
    this.input = input;
    this.state = state;
    this.score = score;
  }
}


