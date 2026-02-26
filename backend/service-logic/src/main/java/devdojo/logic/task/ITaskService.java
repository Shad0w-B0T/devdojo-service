package devdojo.logic.task;

import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.database.task.TaskRatingEntity;
import devdojo.schema.Constants;
import devdojo.schema.task.Difficulty;
import devdojo.schema.task.Test;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.UUID;

/**
 * Task service interface for CRUD operations and unit test management.
 */
public interface ITaskService {

  int RATING_MAX = 5;

  @NotNull(message = "Returned tasks must not be null")
  List<TaskEntity> getTasks(final GetTasksByLanguage getTasksByLangauge);

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


  record GetTasksByLanguage(

    @NotBlank(message = "Language ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Language ID must not exceed {max} characters")
    String languageId

  ) {
  }

  record GetTask(

    @NotBlank(message = "Task ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Task ID must not exceed {max} characters")
    String taskId

  ) {
  }

  record CreateTask(

    @NotBlank(message = "LanguageId must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "LanguageId must not exceed {max} characters")
    String languageId,

    @NotBlank(message = "Task name must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Task name must not exceed {max} characters")
    String name,

    @NotBlank(message = "Task description must not be blank")
    @Size(max = Constants.DESCRIPTION_LENGTH, message = "Task description must not exceed {max} characters")
    String description,

    @NotNull(message = "Task difficulty must not be null")
    Difficulty difficulty,

    @NotBlank(message = "Task template must not be blank")
    @Size(max = Constants.CODE_LENGTH, message = "Task template must not exceed {max} characters")
    String template,

    @NotBlank(message = "Task complete field must not be blank")
    @Size(max = Constants.CODE_LENGTH, message = "Task complete field must not exceed {max} characters")
    String complete,

    @NotNull(message = "Unit tests list must not be null")
    @Size(max = Constants.TEST_LIMIT, message = "Unit tests must not exceed {max} entries")
    @Valid
    List<Test> unitTests,

    boolean active,

    @Nullable
    @Size(max = Constants.TAG_LIMIT, message = "Tags must not exceed {max} entries")
    List<
      @NotBlank(message = "Tag must not be blank")
      @Size(max = Constants.STRING_DEFAULT_LENGTH, message = "Tag must not exceed {max} characters")
        String> tags
  ) {
  }

  record UpdateTask(

    @NotBlank(message = "Task ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Task ID must not exceed {max} characters")
    String taskId,

    @NotBlank(message = "Task name must not be blank")
    @Size(max = Constants.STRING_DEFAULT_LENGTH, message = "Task name must not exceed {max} characters")
    String name,

    @NotBlank(message = "Task description must not be blank")
    @Size(max = Constants.DESCRIPTION_LENGTH, message = "Task description must not exceed {max} characters")
    String description,

    @NotNull(message = "Task difficulty must not be null")
    Difficulty difficulty,

    @NotBlank(message = "Task template must not be blank")
    @Size(max = Constants.CODE_LENGTH, message = "Task template must not exceed {max} characters")
    String template,

    @NotBlank(message = "Task complete field must not be blank")
    @Size(max = Constants.CODE_LENGTH, message = "Task complete field must not exceed {max} characters")
    String complete,

    @NotNull(message = "Unit tests list must not be null")
    @Size(max = Constants.TEST_LIMIT, message = "Unit tests must not exceed {max} entries")
    @Valid
    List<Test> unitTests,

    boolean active,

    @Nullable
    @Size(max = Constants.TAG_LIMIT, message = "Tags must not exceed {max} entries")
    List<
      @NotBlank(message = "Tag must not be blank")
      @Size(max = Constants.STRING_DEFAULT_LENGTH, message = "Tag must not exceed {max} characters")
        String
      > tags

  ) {
  }

  record DeleteTask(

    @NotBlank(message = "Task ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Task ID must not exceed {max} characters")
    String taskId

  ) {
  }

  record AppendTest(

    @NotBlank(message = "Task ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Task ID must not exceed {max} characters")
    String taskId,

    @NotNull(message = "Unit test must not be null")
    @Valid
    Test unitTest

  ) {
  }

  record RemoveTagFromTask(

    @NotBlank(message = "Task ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Task ID must not exceed {max} characters")
    String taskId,

    @NotNull(message = "Tag ID must not be null")
    UUID tagId

  ) {
  }

  record SetTaskRating(

    @NotBlank(message = "Task ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Task ID must not exceed {max} characters")
    String taskId,

    @NotBlank(message = "User ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "User ID must not exceed {max} characters")
    String userId,

    @Min(value = 1, message = "Rating must be at least {value}")
    @Max(value = RATING_MAX, message = "Rating must not exceed {value}")
    int rating

  ) {
  }

}
