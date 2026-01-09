package devdojo.graphql.controller.task;

import devdojo.graphql.input.task.*;
import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.tag.TagEntity;
import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.database.task.TaskProgressEntity;
import devdojo.logic.database.task.TaskProgressRepository;
import devdojo.logic.database.user.UserEntity;
import devdojo.logic.security.SecurityUtils;
import devdojo.logic.task.ITaskService;
import devdojo.logic.task.ITasksProgressService;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.task.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.validation.annotation.Validated;

@Validated
public abstract class AbstractTaskController implements ITaskController {

  private final @NotNull ITaskService taskService;
  private final @NotNull ITasksProgressService tasksProgressService;

  protected AbstractTaskController(final ITaskService taskService,
                                   @NotNull final ITasksProgressService tasksProgressService) {
    Objects.requireNonNull(taskService, "taskService cannot be null");

    this.taskService = taskService;
    this.tasksProgressService = tasksProgressService;
  }

  @QueryMapping("tasks")
  @Override
  public @NotNull List<TaskEntity> tasks(@Argument("input") final GetTasks getTasks) {
    return this.taskService.getTasks(getTasks);
  }

  @QueryMapping("task")
  @Override
  public @NotNull TaskEntity task(@Argument("input") @NotNull final GetTask getTask) {
    return this.taskService.getTask(getTask);
  }

  @QueryMapping("taskRating")
  @Override
  public double taskRating(@Argument("input") @NotNull final GetTask getTask) {
    return this.taskService.getTaskRating(getTask);
  }

  @QueryMapping("nextTask")
  @Override
  public TaskEntity nextTask(@Argument("input") @NotNull final GetNextTask getNextTask) {
    return this.tasksProgressService.getNextTask(getNextTask);
  }

  @QueryMapping("userRank")
  @Override
  public int userRank(@Argument("input") final GetUserRank getUserRank) {
    return this.tasksProgressService.getUserRank(getUserRank);
  }

  @QueryMapping("leaderboard")
  @Override
  public List<TaskProgressRepository.UserRankingProjection> leaderboard(
    @Argument("input") final GetLeaderboard getLeaderboard) {
    return this.tasksProgressService.getLeaderboard(getLeaderboard);
  }

  @MutationMapping("createTask")
  public @NotNull TaskEntity createTask(@Argument("input") @NotNull final CreateTask input) {
    return this.taskService.createTask(input);
  }

  @MutationMapping("updateTask")
  public @NotNull TaskEntity updateTask(@Argument("input") @NotNull final UpdateTask input) {
    return this.taskService.updateTask(input);
  }

  @MutationMapping("appendUnitTest")
  public @NotNull TaskEntity appendUnitTest(@Argument("input") @NotNull final AppendTest input) {
    return this.taskService.appendUnitTest(input);
  }

  @MutationMapping("updateTaskUserInput")
  @Override
  public boolean updateTaskUserInput(@Argument("input") @NotNull final UpdateTaskUserInput input) {
    this.tasksProgressService.updateTaskProgress(input);
    return true;
  }


  @MutationMapping("removeTagFromTask")
  public boolean removeTagFromTask(@Argument("input") @NotNull final RemoveTagFromTask input) {
    return this.taskService.removeTagFromTask(input);
  }

  @MutationMapping("setTaskRating")
  @Override
  public boolean setTaskRating(@Argument("input") @NotNull final SetTaskRating setTaskRating) {
    return this.taskService.setTaskRating(setTaskRating) != null;
  }

  @SchemaMapping(typeName = "Task", value = "tags")
  @Override
  public @NotNull List<String> tags(@NotNull final TaskEntity task) {
    return task.tagEntities().stream().map(TagEntity::name).collect(Collectors.toList());
  }

  @SchemaMapping(typeName = "Task", value = "id")
  @Override
  public @NotNull UUID id(@NotNull final TaskEntity task) {
    return task.id();
  }

  @SchemaMapping(typeName = "Task", value = "language")
  @Override
  public @NotNull LanguageEntity language(@NotNull final TaskEntity task) {
    return task.languageEntity();
  }

  @SchemaMapping(typeName = "Task", value = "name")
  @Override
  public @NotNull String taskName(@NotNull final TaskEntity task) {
    return task.name();
  }

  @SchemaMapping(typeName = "Task", value = "description")
  @Override
  public @NotNull String taskDescription(@NotNull final TaskEntity task) {
    return task.description();
  }

  @SchemaMapping(typeName = "Task", value = "difficulty")
  @Override
  public @NotNull Difficulty taskDifficulty(@NotNull final TaskEntity task) {
    return task.difficulty();
  }

  @SchemaMapping(typeName = "Task", value = "template")
  @Override
  public @NotNull String taskTemplate(@NotNull final TaskEntity task) {
    return task.template();
  }

  @SchemaMapping(typeName = "Task", value = "complete")
  @Override
  public @NotNull String taskComplete(@NotNull final TaskEntity task) {
    return task.complete();
  }

  @SchemaMapping(typeName = "Task", value = "unitTests")
  @Override
  public @NotNull List<ITest> taskUnitTests(@NotNull final TaskEntity task) {
    return task.unitTests();
  }

  @SchemaMapping(typeName = "Task", value = "active")
  @Override
  public boolean active(@NotNull final TaskEntity task) {
    return task.languageEntity().active() && task.active();
  }

  @SchemaMapping(typeName = "Task", value = "userProgress")
  @Override
  public @NotNull ITaskUserProgress userProgress(@NotNull final TaskEntity task,
                                                 @Argument("userId") final UUID userId) {
    final UUID progressUserId = userId != null ? userId : SecurityUtils.currentTokenDetails().userId();

    try {
      final TaskProgressEntity taskProgressEntity =
        this.tasksProgressService.getTaskProgress(progressUserId, task.id());
      return new TaskUserProgress(taskProgressEntity.input(), taskProgressEntity.state(), taskProgressEntity.score());
    } catch (final NotFoundException exception) {
      return new TaskUserProgress("", TaskProgressState.NOT_STARTED, 0);
    }
  }

  @SchemaMapping(typeName = "Task", value = "createdBy")
  @Override
  public UserEntity createdBy(final TaskEntity task) {
    return task.createdByUserEntity();
  }

  @SchemaMapping(typeName = "Task", value = "updatedBy")
  @Override
  public UserEntity updatedBy(final TaskEntity task) {
    return task.updatedByUserEntity();
  }

  @SchemaMapping(typeName = "Task", value = "rating")
  @Override
  public double rating(@NotNull final TaskEntity task) {
    return this.taskService.getTaskRating(task::id);
  }

  @SchemaMapping(typeName = "TaskUserProgress", value = "input")
  @Override
  public @NotNull String input(@NotNull final ITaskUserProgress taskUserProgress) {
    return taskUserProgress.input();
  }

  @SchemaMapping(typeName = "TaskUserProgress", value = "state")
  @Override
  public @NotNull TaskProgressState state(@NotNull final ITaskUserProgress taskUserProgress) {
    return taskUserProgress.state();
  }

  @SchemaMapping(typeName = "TaskUserProgress", value = "score")
  @Override
  public int score(@NotNull final ITaskUserProgress taskUserProgress) {
    return taskUserProgress.score();
  }

  @SchemaMapping(typeName = "Test", value = "input")
  @Override
  public @NotNull List<ITestArgument> input(@NotNull final ITest taskUnitTest) {
    return taskUnitTest.input();
  }

  @SchemaMapping(typeName = "Test", value = "output")
  @Override
  public @NotNull ITestArgument output(@NotNull final ITest taskUnitTest) {
    return taskUnitTest.output();
  }

  @SchemaMapping(typeName = "TestArgument", value = "type")
  @Override
  public @NotNull ArgumentType type(@NotNull final ITestArgument argument) {
    return argument.type();
  }

  @SchemaMapping(typeName = "TestArgument", value = "variant")
  @Override
  public String variant(@NotNull final ITestArgument argument) {
    return argument.variant();
  }

  @SchemaMapping(typeName = "TestArgument", value = "value")
  @Override
  public @NotNull Object value(@NotNull final ITestArgument argument) {
    return argument.value();
  }

  @SchemaMapping(typeName = "UserRanking", value = "userId")
  @Override
  public UUID userId(final TaskProgressRepository.UserRankingProjection userRankingProjection) {
    return userRankingProjection.userId();
  }

  @SchemaMapping(typeName = "UserRanking", value = "totalScore")
  @Override
  public long totalScore(final TaskProgressRepository.UserRankingProjection userRankingProjection) {
    return userRankingProjection.totalScore();
  }

  @SchemaMapping(typeName = "UserRanking", value = "rank")
  @Override
  public long rank(final TaskProgressRepository.UserRankingProjection userRankingProjection) {
    return userRankingProjection.rank();
  }

  private record TaskUserProgress(String input,
                                  TaskProgressState state,
                                  int score) implements ITaskUserProgress {
  }
}
