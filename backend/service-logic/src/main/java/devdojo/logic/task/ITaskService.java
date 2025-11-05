package devdojo.logic.task;

import devdojo.logic.database.task.TaskEntity;
import devdojo.schema.task.ArgumentType;
import devdojo.schema.task.Difficulty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Task service interface for CRUD operations and unit test management.
 */
public interface ITaskService {

  @NotNull(message = "Returned tasks must not be null")
  List<TaskEntity> getTasks(final GetTasks getTasks);

  @NotNull(message = "Returned task must not be null")
  TaskEntity getTask(
    @NotNull(message = "GetTask data must not be null")
    @Valid final GetTask getTask);

  @NotNull(message = "Created task must not be null")
  TaskEntity createTask(
    @NotNull(message = "CreateTask data must not be null")
    @Valid final CreateTask createTask);

  @NotNull(message = "Updated task must not be null")
  TaskEntity updateTask(
    @NotNull(message = "UpdateTask data must not be null")
    @Valid final UpdateTask updateTask);

  @NotNull(message = "Deleted task must not be null")
  TaskEntity deleteTask(
    @NotNull(message = "DeleteTask data must not be null")
    @Valid final DeleteTask deleteTask);

  @NotNull(message = "Appended unit test task must not be null")
  TaskEntity appendUnitTest(
    @NotNull(message = "AppendTest data must not be null")
    @Valid final AppendTest append);

  // ———— DTOs ————

  interface GetTasks {
    UUID languageId();
  }

  interface GetTask {
    @NotNull(message = "Task ID must not be null")
    UUID taskId();
  }

  interface CreateTask {
    @NotNull(message = "LanguageId must not be null")
    UUID languageId();

    @NotNull(message = "Task name must not be null")
    String name();

    @NotNull(message = "Task description must not be null")
    String description();

    @NotNull(message = "Task difficulty must not be null")
    Difficulty difficulty();

    @NotNull(message = "Task template must not be null")
    String template();

    @NotNull(message = "Task complete field must not be null")
    String complete();

    @NotNull(message = "Unit tests list must not be null")
    @Valid
    List<? extends TestData> unitTests();

    @NotNull(message = "Active must not be null")
    boolean active();
  }

  interface UpdateTask {
    @NotNull(message = "Task ID must not be null")
    UUID taskId();

    @NotNull(message = "Task name must not be null")
    String name();

    @NotNull(message = "Task description must not be null")
    String description();

    @NotNull(message = "Task description must not be null")
    Difficulty difficulty();

    @NotNull(message = "Task template must not be null")
    String template();

    @NotNull(message = "Task complete field must not be null")
    String complete();

    @NotNull(message = "Unit tests list must not be null")
    @Valid
    List<? extends TestData> unitTests();

    @NotNull(message = "Active must not be null")
    boolean active();
  }

  interface DeleteTask {
    @NotNull(message = "Task ID must not be null")
    UUID taskId();
  }

  interface AppendTest {
    @NotNull(message = "Task ID must not be null")
    UUID taskId();

    @NotNull(message = "Unit test must not be null")
    @Valid
    TestData unitTest();
  }

  interface TestData {
    @NotNull(message = "Input arguments list must not be null")
    @Valid
    List<? extends TestArgumentData> input();

    @NotNull(message = "Output argument must not be null")
    @Valid
    TestArgumentData output();
  }

  interface TestArgumentData {
    @NotNull(message = "Argument type must not be null")
    ArgumentType type();

    String variant(); // optional, no @NotNull

    @NotNull(message = "Argument value must not be null")
    Object value();
  }
}
