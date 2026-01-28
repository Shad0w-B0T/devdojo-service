package devdojo.logic.task;

import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.database.task.TaskProgressEntity;
import devdojo.logic.database.task.TaskProgressRepository;
import devdojo.schema.task.TaskProgressState;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface ITasksProgressService {

  @NotNull
  List<TaskProgressEntity> getTasksProgresses(@NotNull final GetTaskProgresses getTaskProgresses);

  @NotNull
  TaskProgressEntity getTaskProgress(@NotNull final GetTaskProgress getTaskProgress);

  @NotNull
  TaskProgressEntity getTaskProgress(@NotNull(message = "userId must not be null") final String userId,
                                     @NotNull(message = "taskId must not be null") final String taskId);

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
  record GetTaskProgresses(

    @NotNull(message = "userId must not be null")
    String userId

  ) {
  }

  record GetTaskProgress(

    @NotNull(message = "userId must not be null")
    String userId,

    @NotNull(message = "taskId must not be null")
    String taskId

  ) {
  }

  record GetUserRank(

    @NotNull(message = "userId must not be null")
    String userId,

    @NotNull(message = "languageId must not be null")
    String languageId

  ) {
  }

  record GetLeaderboard(

    @NotNull(message = "languageId must not be null")
    String languageId,

    @Min(value = 0, message = "start must be greater than or equal to 0")
    int start,

    @Min(value = 0, message = "end must be greater than or equal to 0")
    int end

  ) {
  }

  record GetNextTask(

    @NotNull(message = "userId must not be null")
    String userId,

    String languageId, // optional

    @NotNull(message = "state must not be null")
    TaskProgressState state

  ) {
  }

  record UpdateTaskProgressInput(

    @NotNull(message = "userId must not be null")
    String userId,

    @NotNull(message = "taskId must not be null")
    String taskId,

    @NotNull(message = "input must not be null")
    String input

  ) {
  }

  record UpdateTaskProgress(

    @NotNull(message = "userId must not be null")
    String userId,

    @NotNull(message = "taskId must not be null")
    String taskId,

    @NotNull(message = "input must not be null")
    String input,

    @NotNull(message = "state must not be null")
    TaskProgressState state,

    @Min(value = 0, message = "score must be positive")
    int score

  ) {
  }
}
