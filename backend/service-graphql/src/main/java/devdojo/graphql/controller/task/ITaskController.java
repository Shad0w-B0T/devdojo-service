package devdojo.graphql.controller.task;

import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.database.task.TaskProgressRepository;
import devdojo.graphql.input.task.AppendTest;
import devdojo.graphql.input.task.CreateTask;
import devdojo.graphql.input.task.GetLeaderboard;
import devdojo.graphql.input.task.GetNextTask;
import devdojo.graphql.input.task.GetTask;
import devdojo.graphql.input.task.GetTasks;
import devdojo.graphql.input.task.GetUserRank;
import devdojo.graphql.input.task.UpdateTask;
import devdojo.graphql.input.task.UpdateTaskUserInput;
import devdojo.schema.task.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface ITaskController {

  // ────── Queries ──────

  @NotNull
  List<TaskEntity> tasks(final GetTasks getTasks);

  @NotNull
  TaskEntity task(@NotNull final GetTask getTask);

  @NotNull
  TaskEntity nextTask(@NotNull final GetNextTask getNextTask);

  int userRank(@NotNull final GetUserRank getUserRank);

  @NotNull
  List<TaskProgressRepository.UserRankingProjection> leaderboard(@NotNull final GetLeaderboard getLeaderboard);

  // ────── Mutations ──────

  @NotNull
  TaskEntity createTask(@NotNull CreateTask input);

  @NotNull
  TaskEntity updateTask(@NotNull UpdateTask input);

  @NotNull
  TaskEntity appendUnitTest(@NotNull AppendTest input);

  boolean updateTaskUserInput(@NotNull UpdateTaskUserInput input);

  // ────── Schema ──────

  //Task
  @NotNull
  UUID id(@NotNull final TaskEntity task);

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

  boolean active(@NotNull final TaskEntity task);

  @NotNull
  ITaskUserProgress userProgress(@NotNull final TaskEntity task,
                                 final UUID userId);

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
  UUID userId(@NotNull final TaskProgressRepository.UserRankingProjection userRankingProjection);

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
