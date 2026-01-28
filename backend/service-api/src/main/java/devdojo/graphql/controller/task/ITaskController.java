package devdojo.graphql.controller.task;

import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.database.task.TaskProgressRepository;
import devdojo.logic.database.user.UserEntity;
import devdojo.logic.task.ITaskService;
import devdojo.logic.task.ITasksProgressService;
import devdojo.schema.task.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface ITaskController {

  // ────── Queries ──────

  @NotNull
  List<TaskEntity> tasks(final ITaskService.GetTasksByLangauge getTasks);

  @NotNull
  TaskEntity task(@NotNull final ITaskService.GetTask getTask);

  @NotNull
  double taskRating(@NotNull final ITaskService.GetTask getTask);

  @NotNull
  TaskEntity nextTask(@NotNull final ITasksProgressService.GetNextTask getNextTask);

  TaskProgressRepository.UserRankingProjection userRank(@NotNull final ITasksProgressService.GetUserRank getUserRank);

  @NotNull
  List<TaskProgressRepository.UserRankingProjection> leaderboard(
    @NotNull final ITasksProgressService.GetLeaderboard getLeaderboard);

  // ────── Mutations ──────

  @NotNull
  TaskEntity createTask(@NotNull ITaskService.CreateTask input);

  @NotNull
  TaskEntity updateTask(@NotNull ITaskService.UpdateTask input);

  @NotNull
  TaskEntity appendUnitTest(@NotNull ITaskService.AppendTest input);

  boolean updateTaskUserInput(@NotNull ITasksProgressService.UpdateTaskProgressInput input);

  boolean setTaskRating(@NotNull final ITaskService.SetTaskRating setTaskRating);

  // ────── Schema ──────

  //Task
  @NotNull
  String id(@NotNull final TaskEntity task);

  @NotNull
  LanguageEntity language(@NotNull final TaskEntity task);

  @NotNull
  String taskName(@NotNull final TaskEntity task);

  @NotNull
  String taskDescription(@NotNull final TaskEntity task);

  @NotNull
  Difficulty taskDifficulty(@NotNull final TaskEntity task);

  @NotNull
  String taskTemplate(@NotNull final TaskEntity task);

  @NotNull
  String taskComplete(@NotNull final TaskEntity task);

  @NotNull
  List<ITest> taskUnitTests(@NotNull final TaskEntity task);

  List<String> tags(@NotNull final TaskEntity task);

  boolean active(@NotNull final TaskEntity task);

  UserEntity createdBy(@NotNull final TaskEntity task);

  UserEntity updatedBy(@NotNull final TaskEntity task);

  @NotNull
  ITaskUserProgress userProgress(@NotNull final TaskEntity task,
                                 final String userId);

  double rating(@NotNull final TaskEntity task);

  //TaskUserProgress
  @NotNull
  String input(@NotNull final ITaskUserProgress taskUserProgress);

  @NotNull
  TaskProgressState state(@NotNull final ITaskUserProgress taskUserProgress);

  int score(@NotNull final ITaskUserProgress taskUserProgress);

  //Task unit tests
  @NotNull
  List<ITestArgument> input(@NotNull final ITest taskUnitTest);

  @NotNull
  ITestArgument output(@NotNull final ITest taskUnitTest);

  //Test Argument
  @NotNull
  ArgumentType type(@NotNull final ITestArgument argument);

  String variant(@NotNull final ITestArgument argument);

  @NotNull
  Object value(@NotNull final ITestArgument argument);

  //User Ranking
  @NotNull
  UserEntity user(@NotNull final TaskProgressRepository.UserRankingProjection userRankingProjection);

  long totalScore(@NotNull final TaskProgressRepository.UserRankingProjection userRankingProjection);

  long rank(@NotNull final TaskProgressRepository.UserRankingProjection userRankingProjection);

  interface ITaskUserProgress {

    String input();

    @NotNull
    TaskProgressState state();

    @NotNull
    int score();

  }
}
