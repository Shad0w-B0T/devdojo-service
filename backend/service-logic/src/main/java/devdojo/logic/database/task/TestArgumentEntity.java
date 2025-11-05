package devdojo.logic.database.task;

import devdojo.logic.database.AbstractEntity;
import devdojo.logic.database.task.converter.JsonStringConverter;
import devdojo.schema.Constants;
import devdojo.schema.task.ArgumentType;
import devdojo.schema.task.ITestArgument;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "test_argument")
@NoArgsConstructor
@Setter
@Getter
@Accessors(fluent = true)
public final class TestArgumentEntity extends AbstractEntity implements ITestArgument {

  @NotNull(message = "Test entity must not be null")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_unit_test_id", nullable = false, updatable = false)
  private TestEntity testEntity;

  @NotNull(message = "Argument type must not be null")
  @Column(name = "type", nullable = false)
  private ArgumentType type;

  @Size(max = Constants.STRING_DEFAULT_LENGTH, message = "Variant must not exceed {max} characters")
  @Column(name = "variant")
  private String variant;

  @Column(name = "value", nullable = false, columnDefinition = "TEXT")
  @Convert(converter = JsonStringConverter.class)
  private Object value;

  @PositiveOrZero(message = "Order must be zero or a positive integer")
  @Column(name = "order_index", nullable = false)
  private int order;

  public TestArgumentEntity(final TestEntity testEntity,
                            final ArgumentType type,
                            final String variant,
                            final Object value,
                            final int order) {
    this.testEntity = testEntity;
    this.type = type;
    this.variant = variant;
    this.value = value;
    this.order = order;
  }
}

