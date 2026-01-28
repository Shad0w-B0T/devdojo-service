package devdojo.logic.task;

import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.database.task.TaskRatingEntity;
import devdojo.schema.task.ArgumentType;
import devdojo.schema.task.Difficulty;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Task service interface for CRUD operations and unit test management.
 */
public interface ITaskService {

  int RATING_MAX = 5;

  @NotNull(message = "Returned tasks must not be null")
  List<TaskEntity> getTasks(final GetTasksByLangauge getTasksByLangauge);

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

  @NotNull(message = "Remove tag from task must not be null")
  boolean removeTagFromTask(
    @NotNull(message = "RemoveTagFromTask data must not be null")
    @Valid final RemoveTagFromTask removeTagFromTask
  );

  @NotNull(message = "Appended unit test task must not be null")
  TaskEntity appendUnitTest(
    @NotNull(message = "AppendTest data must not be null")
    @Valid final AppendTest append);

  @NotNull(message = "TaskRating must not be null")
  TaskRatingEntity setTaskRating(
    @NotNull(message = "SetTaskRating data must not be null") final SetTaskRating setTaskRating);

  @NotNull
  Double getTaskRating(@NotNull(message = "GetTask data must not be null")
                       @Valid final GetTask getTask);

  // ———— DTOs ————

  record GetTasksByLangauge(
    @NotNull(message = "Language ID must not be null")
    String languageId
  ) {
  }

  record GetTask(
    @NotNull(message = "Task ID must not be null")
    String taskId
  ) {
  }

  record CreateTask(

    @NotNull(message = "LanguageId must not be null")
    String languageId,

    @NotNull(message = "Task name must not be null")
    String name,

    @NotNull(message = "Task description must not be null")
    String description,

    @NotNull(message = "Task difficulty must not be null")
    Difficulty difficulty,

    @NotNull(message = "Task template must not be null")
    String template,

    @NotNull(message = "Task complete field must not be null")
    String complete,

    @NotNull(message = "Unit tests list must not be null")
    @Valid
    List<? extends TestData> unitTests,

    boolean active,

    @Nullable
    List<String> tags
  ) {
  }

  record UpdateTask(

    @NotNull(message = "Task ID must not be null")
    String taskId,

    @NotNull(message = "Task name must not be null")
    String name,

    @NotNull(message = "Task description must not be null")
    String description,

    @NotNull(message = "Task difficulty must not be null")
    Difficulty difficulty,

    @NotNull(message = "Task template must not be null")
    String template,

    @NotNull(message = "Task complete field must not be null")
    String complete,

    @NotNull(message = "Unit tests list must not be null")
    @Valid
    List<? extends TestData> unitTests,

    boolean active,

    @Nullable
    List<String> tags
  ) {
  }

  record DeleteTask(

    @NotNull(message = "Task ID must not be null")
    String taskId

  ) {
  }

  record AppendTest(

    @NotNull(message = "Task ID must not be null")
    String taskId,

    @NotNull(message = "Unit test must not be null")
    @Valid
    TestData unitTest

  ) {
  }

  record TestData(

    @NotNull(message = "Input arguments list must not be null")
    @Valid
    List<? extends TestArgumentData> input,

    @NotNull(message = "Output argument must not be null")
    @Valid
    TestArgumentData output

  ) {
  }

  record TestArgumentData(

    @NotNull(message = "Argument type must not be null")
    ArgumentType type,

    String variant, // optional

    @NotNull(message = "Argument value must not be null")
    Object value

  ) {
  }

  record RemoveTagFromTask(

    @NotNull(message = "Task ID must not be null")
    String taskId,

    @NotNull(message = "Tag ID must not be null")
    UUID tagId

  ) {
  }

  record SetTaskRating(

    @NotNull(message = "Task ID must not be null")
    String taskId,

    @NotNull(message = "User ID must not be null")
    String userId,


    @Min(1)
    @Max(RATING_MAX)
    int rating

  ) {
  }

}
