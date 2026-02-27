package devdojo.logic.database.task;

import devdojo.logic.database.AbstractEntity;
import devdojo.schema.task.ArgumentType;
import devdojo.schema.task.ITest;
import devdojo.schema.task.ITestArgument;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "test")
@NoArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
public final class TestEntity extends AbstractEntity implements ITest {

  @NotNull(message = "Task entity must not be null")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", nullable = false)
  private TaskEntity taskEntity;

  @NotNull(message = "Argument entities must not be null")
  @OneToMany(mappedBy = "testEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TestArgumentEntity> argumentEntities = new ArrayList<>();

  public TestEntity(final TaskEntity taskEntity) {
    this.taskEntity = taskEntity;
  }

  @Override
  public List<ITestArgument> input() {
    return this.argumentEntities.stream()
      .filter(arg -> arg.type() == ArgumentType.INPUT)
      .sorted(Comparator.comparingInt(TestArgumentEntity::order))
      .collect(Collectors.toList());
  }

  @Override
  public ITestArgument output() {
    return this.argumentEntities.stream()
      .filter(arg -> arg.type() == ArgumentType.OUTPUT || arg.type() == ArgumentType.THROWABLE)
      .findFirst()
      .orElse(null);
  }
}
