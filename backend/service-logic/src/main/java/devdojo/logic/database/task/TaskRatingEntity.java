package devdojo.logic.database.task;

import devdojo.logic.database.AbstractEntity;
import devdojo.logic.database.user.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(
  name = "task_rating",
  uniqueConstraints = @UniqueConstraint(columnNames = {"task_id", "user_id"}),
  indexes = {
    @Index(name = "idx_task_rating_task_user", columnList = "task_id, user_id"),
    @Index(
      name = "idx_task_rating_task",
      columnList = "task_id"
    )
  }
)
@NoArgsConstructor
@Setter
@Getter
@Accessors(fluent = true)
public final class TaskRatingEntity extends AbstractEntity {

  @NotNull(message = "Task entity must not be null")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", nullable = false, updatable = false)
  private TaskEntity taskEntity;

  @NotNull(message = "Task entity must not be null")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, updatable = false)
  private UserEntity userEntity;

  @NotNull(message = "Rating must not be null")
  @Column(nullable = false)
  private int rating;

  public TaskRatingEntity(final TaskEntity taskEntity,
                          final UserEntity userEntity,
                          final int rating) {
    this.taskEntity = taskEntity;
    this.userEntity = userEntity;
    this.rating = rating;
  }
}
