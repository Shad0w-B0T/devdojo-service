package devdojo.graphql.controller.task;

import devdojo.graphql.controller.AbstractGraphController;
import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.tag.TagEntity;
import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.database.task.TaskProgressEntity;
import devdojo.logic.database.task.TaskProgressRepository;
import devdojo.logic.database.user.UserEntity;
import devdojo.logic.security.SecurityUtils;
import devdojo.logic.task.ITaskService;
import devdojo.logic.task.ITasksProgressService;
import devdojo.logic.user.IUserService;
import devdojo.schema.annotation.RoleRequired;
import devdojo.schema.annotation.SelfRequired;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.task.*;
import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;

@Validated
public abstract class AbstractTaskController extends AbstractGraphController implements ITaskController {

  private final @NotNull ITaskService taskService;
  private final @NotNull IUserService userService;
  private final @NotNull ITasksProgressService tasksProgressService;

  protected AbstractTaskController(final ITaskService taskService,
                                   @NotNull final IUserService userService,
                                   @NotNull final ITasksProgressService tasksProgressService) {
    Objects.requireNonNull(taskService, "taskService cannot be null");

    this.taskService = taskService;
    this.userService = userService;
    this.tasksProgressService = tasksProgressService;
  }

  @RoleRequired(role = UserRole.USER)
  @QueryMapping("tasks")
  @Override
  public @NotNull List<TaskEntity> tasks(@Argument("input") final ITaskService.GetTasksByLanguage getTasks) {
    return this.taskService.getTasks(getTasks);
  }

  @RoleRequired(role = UserRole.USER)
  @QueryMapping("task")
  @Override
  public @NotNull TaskEntity task(@Argument("input") @NotNull final ITaskService.GetTask getTask) {
    return this.taskService.getTask(getTask);
  }

  @RoleRequired(role = UserRole.USER)
  @QueryMapping("taskRating")
  @Override
  public double taskRating(@Argument("input") @NotNull final ITaskService.GetTask getTask) {
    return this.taskService.getTaskRating(getTask);
  }

  @QueryMapping("nextTask")
  @SelfRequired(field = "#user.userId()")
  @Override
  public TaskEntity nextTask(
    @Argument("input") @P("user") @NotNull final ITasksProgressService.GetNextTask getNextTask) {
    return this.tasksProgressService.getNextTask(getNextTask);
  }

  @QueryMapping("languageProgress")
  @SelfRequired(field = "#user.userId()")
  @Override
  public @NotNull List<TaskProgressRepository.LanguageProgressProjection>
  languageProgress(@Argument("input") @P("user") final ITasksProgressService.GetLanguageProgress getLanguageProgress) {
    return this.tasksProgressService.getLanguageProgress(getLanguageProgress);
  }

  @QueryMapping("userRank")
  @SelfRequired(field = "#user.userId()")
  @Override
  public TaskProgressRepository.UserRankingProjection userRank(
    @Argument("input") @P("user") final ITasksProgressService.GetUserRank getUserRank) {
    return this.tasksProgressService.getUserRank(getUserRank);
  }

  @QueryMapping("leaderboard")
  @Override
  public List<TaskProgressRepository.UserRankingProjection> leaderboard(
    @Argument("input") final ITasksProgressService.GetLeaderboard getLeaderboard) {
    return this.tasksProgressService.getLeaderboard(getLeaderboard);
  }

  @RoleRequired(role = UserRole.PROFESSOR)
  @MutationMapping("createTask")
  public @NotNull TaskEntity createTask(@Argument("input") @NotNull final ITaskService.CreateTask input) {
    return this.taskService.createTask(input);
  }

  @RoleRequired(role = UserRole.PROFESSOR)
  @MutationMapping("updateTask")
  public @NotNull TaskEntity updateTask(@Argument("input") @NotNull final ITaskService.UpdateTask input) {
    return this.taskService.updateTask(input);
  }

  @RoleRequired(role = UserRole.PROFESSOR)
  @MutationMapping("appendUnitTest")
  public @NotNull TaskEntity appendUnitTest(@Argument("input") @NotNull final ITaskService.AppendTest input) {
    return this.taskService.appendUnitTest(input);
  }

  @MutationMapping("updateTaskUserInput")
  @SelfRequired(field = "#user.userId()")
  @Override
  public boolean updateTaskUserInput(
    @Argument("input") @P("user") @NotNull final ITasksProgressService.UpdateTaskProgressInput input) {
    this.tasksProgressService.updateTaskProgress(input);
    return true;
  }

  @RoleRequired(role = UserRole.PROFESSOR)
  @MutationMapping("removeTagFromTask")
  public boolean removeTagFromTask(@Argument("input") @NotNull final ITaskService.RemoveTagFromTask input) {
    return this.taskService.removeTagFromTask(input);
  }

  @SelfRequired(field = "#user.userId()")
  @MutationMapping("setTaskRating")
  @Override
  public boolean setTaskRating(@Argument("input") @P("user") @NotNull final ITaskService.SetTaskRating setTaskRating) {
    return this.taskService.setTaskRating(setTaskRating) != null;
  }

  @SchemaMapping(typeName = "Task", value = "tags")
  @Override
  public @NotNull List<String> tags(@NotNull final TaskEntity task) {
    return task.tagEntities().stream().map(TagEntity::name).collect(Collectors.toList());
  }

  @SchemaMapping(typeName = "Task", value = "id")
  @Override
  public @NotNull String id(@NotNull final TaskEntity task) {
    return task.externalId();
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
                                                 @Argument("userId") final String userId) {
    final String progressUserId = userId != null ? userId : SecurityUtils.currentTokenDetails().userId();

    try {
      final TaskProgressEntity taskProgressEntity =
        this.tasksProgressService.getTaskProgress(progressUserId, task.externalId());
      return new TaskUserProgress(taskProgressEntity.input(), taskProgressEntity.state(), taskProgressEntity.score());
    } catch (final NotFoundException exception) {
      return new TaskUserProgress("", TaskProgressState.NOT_STARTED, 0);
    }
  }

  @SchemaMapping(typeName = "Task", value = "createdBy")
  @Override
  public UserEntity createdBy(final TaskEntity task) {
    return this.anonymousUser(task.createdByUserEntity());
  }

  @SchemaMapping(typeName = "Task", value = "updatedBy")
  @Override
  public UserEntity updatedBy(final TaskEntity task) {
    return this.anonymousUser(task.updatedByUserEntity());
  }

  @SchemaMapping(typeName = "Task", value = "rating")
  @Override
  public double rating(@NotNull final TaskEntity task) {
    return this.taskService.getTaskRating(new ITaskService.GetTask(task.externalId()));
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
    return List.copyOf(taskUnitTest.input());
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

  @SchemaMapping(typeName = "UserRanking", value = "user")
  @Override
  public @NotNull UserEntity user(final TaskProgressRepository.UserRankingProjection userRankingProjection) {
    return this.anonymousUser(this.userService.getInternalUser(userRankingProjection.userId()));
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

  // ───── LanguageProgress ─────

  @SchemaMapping(typeName = "LanguageProgress", value = "languageId")
  public @NotNull String languageId(
    final TaskProgressRepository.LanguageProgressProjection projection) {
    return projection.languageId().toString();
  }

  @SchemaMapping(typeName = "LanguageProgress", value = "languageName")
  public @NotNull String languageName(
    final TaskProgressRepository.LanguageProgressProjection projection) {
    return projection.languageName();
  }

  @SchemaMapping(typeName = "LanguageProgress", value = "totalTasks")
  public long totalTasks(
    final TaskProgressRepository.LanguageProgressProjection projection) {
    return projection.totalTasks();
  }

  @SchemaMapping(typeName = "LanguageProgress", value = "completedTasks")
  public long completedTasks(
    final TaskProgressRepository.LanguageProgressProjection projection) {
    return projection.completedTasks();
  }

  @SchemaMapping(typeName = "LanguageProgress", value = "totalScore")
  public long totalScore(
    final TaskProgressRepository.LanguageProgressProjection projection) {
    return projection.totalScore();
  }

  @SchemaMapping(typeName = "LanguageProgress", value = "averageScore")
  public double averageScore(
    final TaskProgressRepository.LanguageProgressProjection projection) {
    return projection.averageScore() != null
      ? projection.averageScore()
      : 0.0;
  }

  private record TaskUserProgress(String input,
                                  TaskProgressState state,
                                  int score) implements ITaskUserProgress {
  }
}
