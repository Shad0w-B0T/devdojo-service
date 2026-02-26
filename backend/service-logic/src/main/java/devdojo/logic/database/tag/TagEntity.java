package devdojo.logic.database.tag;

import devdojo.logic.database.AbstractEntity;
import devdojo.logic.database.task.TaskEntity;
import devdojo.schema.tag.ITag;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tag")
@NoArgsConstructor
@Setter
@Getter
@Accessors(fluent = true)
public final class TagEntity extends AbstractEntity implements ITag {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", nullable = false)
  private TaskEntity taskEntity;

  @NotNull(message = "Name must not be null")
  @Column(nullable = false)
  private String name;

  public TagEntity(final TaskEntity taskEntity,
                   final String name) {
    this.taskEntity = taskEntity;
    this.name = name;
  }

  public void setTaskEntity(final TaskEntity taskEntity) {
    this.taskEntity = taskEntity;
  }
}
