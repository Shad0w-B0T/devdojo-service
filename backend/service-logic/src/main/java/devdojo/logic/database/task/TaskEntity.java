package devdojo.logic.database.task;

import devdojo.logic.database.AbstractExternalEntity;
import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.tag.TagEntity;
import devdojo.logic.database.user.UserEntity;
import devdojo.schema.Constants;
import devdojo.schema.task.Difficulty;
import devdojo.schema.task.ITask;
import devdojo.schema.task.ITest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
public final class TaskEntity extends AbstractExternalEntity implements ITask {

  @NotNull(message = "Language entity must not be null")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "language_id", nullable = false)
  private LanguageEntity languageEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_created_id")
  private UserEntity createdByUserEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_updated_id")
  private UserEntity updatedByUserEntity;

  @NotNull(message = "Task name must not be null")
  @Size(max = Constants.STRING_DEFAULT_LENGTH, message = "Task name must not exceed {max} characters")
  @Column(nullable = false)
  private String name;

  @NotNull(message = "Description must not be null")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @NotNull(message = "Difficulty must not be null")
  @Column(nullable = false)
  private Difficulty difficulty;

  @NotNull(message = "Template must not be null")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String template;

  @NotNull(message = "Complete text must not be null")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String complete;

  @OneToMany(mappedBy = "taskEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<TestEntity> testEntities = new ArrayList<>();

  @OneToMany(mappedBy = "taskEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<TagEntity> tagEntities = new ArrayList<>();

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


  public void addTagEntity(final TagEntity tagEntity) {
    if (!this.tagEntities.contains(tagEntity)) {
      this.tagEntities.add(tagEntity);
      tagEntity.setTaskEntity(this);
    }
  }

  public void setTagEntities(final List<TagEntity> tagEntities) {
    this.tagEntities.clear();
    tagEntities.forEach(this::addTagEntity);
  }

  @Override
  protected String prefix() {
    return "tsk";
  }
}
