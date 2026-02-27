package devdojo.logic.task;

import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.database.task.TaskProgressEntity;
import devdojo.logic.database.task.TaskProgressRepository;
import devdojo.schema.Constants;
import devdojo.schema.task.TaskProgressState;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public interface ITasksProgressService {

  @NotNull
  List<TaskProgressEntity> getTasksProgresses(@NotNull final GetTaskProgresses getTaskProgresses);

  @NotNull
  TaskProgressEntity getTaskProgress(@NotNull final GetTaskProgress getTaskProgress);

  @NotNull
  TaskProgressEntity getTaskProgress(@NotNull(message = "userId must not be null") final String userId,
                                     @NotNull(message = "taskId must not be null") final String taskId);

  List<TaskProgressRepository.LanguageProgressProjection> getLanguageProgress(
    @NotNull(message = "userId must not be null") final GetLanguageProgress getLanguageProgress);

  @NotNull
  TaskEntity getNextTask(@NotNull(message = "GetNextTask must not be null") final GetNextTask getNextTask);

  @NotNull(message = "Returned rank must not be null")
  TaskProgressRepository.UserRankingProjection getUserRank(
    @NotNull(message = "GetUserRank data must not be null") final GetUserRank getUserRank);

  @NotNull(message = "Returned leaderboard must not be null")
  List<TaskProgressRepository.UserRankingProjection> getLeaderboard(
    @NotNull(message = "GetLeaderboard data must not be null") final GetLeaderboard getLeaderboard);

  @NotNull
  TaskProgressEntity updateTaskProgress(@NotNull final UpdateTaskProgress updateTaskProgress);

  @NotNull
  TaskProgressEntity updateTaskProgress(@NotNull final UpdateTaskProgressInput updateTaskProgressInput);

  // DTOs
  record GetLanguageProgress(@NotBlank(message = "User ID must not be blank")
                             @Size(max = Constants.ID_LENGTH, message = "User ID must not exceed {max} characters")
                             String userId

  ) {
  }

  record GetTaskProgresses(@NotBlank(message = "User ID must not be blank")
                           @Size(max = Constants.ID_LENGTH, message = "User ID must not exceed {max} characters")
                           String userId

  ) {
  }

  record GetTaskProgress(

    @NotBlank(message = "User ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "User ID must not exceed {max} characters")
    String userId,

    @NotBlank(message = "Task ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Task ID must not exceed {max} characters")
    String taskId

  ) {
  }

  record GetUserRank(

    @NotBlank(message = "User ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "User ID must not exceed {max} characters")
    String userId,

    @NotBlank(message = "Language ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Language ID must not exceed {max} characters")
    String languageId

  ) {
  }

  record GetLeaderboard(

    @NotBlank(message = "Language ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Language ID must not exceed {max} characters")
    String languageId,

    @Min(value = 0, message = "Start must be greater than or equal to {value}")
    int start,

    @Min(value = 0, message = "End must be greater than or equal to {value}")
    int end

  ) {
  }

  record GetNextTask(

    @NotBlank(message = "User ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "User ID must not exceed {max} characters")
    String userId,

    @Size(max = Constants.ID_LENGTH, message = "Language ID must not exceed {max} characters")
    String languageId, // optional

    @NotNull(message = "State must not be null")
    TaskProgressState state

  ) {
  }


  record UpdateTaskProgressInput(

    @NotBlank(message = "User ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "User ID must not exceed {max} characters")
    String userId,

    @NotBlank(message = "Task ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Task ID must not exceed {max} characters")
    String taskId,

    @NotBlank(message = "Input must not be blank")
    @Size(max = Constants.CODE_LENGTH, message = "Input must not exceed {max} characters")
    String input

  ) {
  }

  record UpdateTaskProgress(

    @NotBlank(message = "User ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "User ID must not exceed {max} characters")
    String userId,

    @NotBlank(message = "Task ID must not be blank")
    @Size(max = Constants.ID_LENGTH, message = "Task ID must not exceed {max} characters")
    String taskId,

    @NotBlank(message = "Input must not be blank")
    @Size(max = Constants.CODE_LENGTH, message = "Input must not exceed {max} characters")
    String input,

    @NotNull(message = "State must not be null")
    TaskProgressState state,

    @Min(value = 0, message = "Score must be greater than or equal to {value}")
    int score

  ) {
  }
}
