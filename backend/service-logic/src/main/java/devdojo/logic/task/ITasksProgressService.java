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
  TaskProgressEntity getTaskProgress(@NotNull(message = "userId must not be null") final UUID userId,
                                     @NotNull(message = "taskId must not be null") final UUID taskId);

  @NotNull
  TaskEntity getNextTask(@NotNull(message = "GetNextTask must not be null") final GetNextTask getNextTask);

  @NotNull(message = "Returned rank must not be null")
  int getUserRank(@NotNull(message = "GetUserRank data must not be null") final GetUserRank getUserRank);

  @NotNull(message = "Returned leaderboard must not be null")
  List<TaskProgressRepository.UserRankingProjection> getLeaderboard(
    @NotNull(message = "GetLeaderboard data must not be null") final GetLeaderboard getLeaderboard);

  @NotNull
  TaskProgressEntity updateTaskProgress(@NotNull final UpdateTaskProgress updateTaskProgress);

  @NotNull
  TaskProgressEntity updateTaskProgress(@NotNull final UpdateTaskProgressInput updateTaskProgressInput);

  // DTOs
  interface GetTaskProgresses {
    @NotNull(message = "completedTasksId must not be null")
    UUID userId();
  }

  interface GetTaskProgress {
    @NotNull(message = "userId must not be null")
    UUID userId();

    @NotNull(message = "taskId must not be null")
    UUID taskId();
  }

  interface GetUserRank {
    @NotNull(message = "userId must not be null")
    UUID userId();

    @NotNull(message = "languageId must not be null")
    UUID languageId();
  }

  interface GetLeaderboard {
    @NotNull(message = "languageId must not be null")
    UUID languageId();

    @NotNull(message = "start must not be null")
    @Min(value = 0, message = "start must be greater than or equal to 0")
    int start();

    @NotNull(message = "end must not be null")
    @Min(value = 0, message = "end must be greater than or equal to 0")
    int end();
  }

  interface GetNextTask {
    @NotNull(message = "userId must not be null")
    UUID userId();

    UUID languageId();

    @NotNull(message = "state must not be null")
    TaskProgressState state();
  }

  interface UpdateTaskProgressInput extends GetTaskProgress {
    @NotNull(message = "input must not be null")
    String input();
  }

  interface UpdateTaskProgress extends UpdateTaskProgressInput {
    @NotNull(message = "state must not be null")
    TaskProgressState state();

    @NotNull(message = "score must not be null")
    @Min(value = 0, message = "score must be postive")
    int score();
  }
}
