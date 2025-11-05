package devdojo.logic.database.task;

import devdojo.logic.database.AbstractEntity;
import devdojo.logic.database.language.LanguageEntity;
import devdojo.schema.Constants;
import devdojo.schema.task.Difficulty;
import devdojo.schema.task.ITask;
import devdojo.schema.task.ITest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "task")
@NoArgsConstructor
@Setter
@Getter
@Accessors(fluent = true)
public final class TaskEntity extends AbstractEntity implements ITask {

  @NotNull(message = "Language entity must not be null")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "language_id", nullable = false)
  private LanguageEntity languageEntity;

  @NotNull(message = "Task name must not be null")
  @Size(max = Constants.STRING_DEFAULT_LENGTH, message = "Task name must not exceed {max} characters")
  @Column(nullable = false)
  private String name;

  @NotNull(message = "Description must not be null")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  private Difficulty difficulty;

  @NotNull(message = "Template must not be null")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String template;

  @NotNull(message = "Complete text must not be null")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String complete;

  @OneToMany(mappedBy = "taskEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<TestEntity> testEntities = new ArrayList<>();

  @Column(nullable = false)
  boolean active;

  public TaskEntity(final LanguageEntity languageEntity,
                    final String name,
                    final String description,
                    final Difficulty difficulty,
                    final String template,
                    final String complete,
                    final boolean active) {
    this.languageEntity = languageEntity;
    this.name = name;
    this.description = description;
    this.difficulty = difficulty;
    this.template = template;
    this.complete = complete;
    this.active = active;
  }

  @Override
  public @NotNull List<ITest> unitTests() {
    return this.testEntities
      .stream()
      .map(taskUnitTestEntity -> (ITest) taskUnitTestEntity)
      .toList();
  }
}
