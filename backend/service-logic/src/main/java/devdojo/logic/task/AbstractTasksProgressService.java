package devdojo.logic.task;

import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.database.task.TaskProgressEntity;
import devdojo.logic.database.task.TaskProgressRepository;
import devdojo.logic.database.user.UserEntity;
import devdojo.logic.language.ILanguageService;
import devdojo.logic.user.IUserService;
import devdojo.schema.annotation.RoleRequired;
import devdojo.schema.annotation.SelfRequired;
import devdojo.schema.exception.database.DatabaseException;
import devdojo.schema.exception.database.IllegalInputException;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.task.TaskProgressState;
import devdojo.schema.user.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;

@Validated
@Slf4j
public abstract class AbstractTasksProgressService implements ITasksProgressService {
  private final @NotNull IUserService userService;
  private final @NotNull ITaskService taskService;
  private final @NotNull ILanguageService languageService;
  private final @NotNull TaskProgressRepository taskProgressRepository;

  protected AbstractTasksProgressService(
    @NotNull(message = "UserService must not be null") final IUserService userService,
    @NotNull(message = "TaskService must not be null") final ITaskService taskService,
    @NotNull(message = "LanguageService must not be null") final ILanguageService languageService,
    @NotNull(message = "CompletedTasksRepository must not be null")
    final TaskProgressRepository taskProgressRepository) {
    this.userService = userService;
    this.taskService = taskService;
    this.languageService = languageService;
    this.taskProgressRepository = taskProgressRepository;
    log.info("AbstractCompletedTasksService initialized with CompletedTaskRepository");
  }

  @Override
  public List<TaskProgressEntity> getTasksProgresses(final GetTaskProgresses getTaskProgresses) {
    final UserEntity userEntity = this.userService.getUser(getTaskProgresses.userId());

    return this.taskProgressRepository.findByUserEntity(userEntity);
  }

  @Override
  public TaskProgressEntity getTaskProgress(final GetTaskProgress getTaskProgress) {
    return Optional
      .ofNullable(taskProgress(getTaskProgress).taskProgressEntity())
      .orElseThrow(() -> new NotFoundException("TaskProgress not found"));
  }

  // @SelfRequired(bypassRole = UserRole.PROFESSOR, field = "#userId")
  @Override
  public TaskProgressEntity getTaskProgress(@P("#userId") final UUID userId,
                                            final UUID taskId) {
    return this.getTaskProgress(new GetTaskProgress() {
      @Override
      public UUID userId() {
        return userId;
      }

      @Override
      public UUID taskId() {
        return taskId;
      }
    });
  }

  @SelfRequired(bypassRole = UserRole.ADMIN, field = "#q.userId()")
  @Cacheable(value = "userRankCache", keyGenerator = "userRankKeyGenerator")
  @Override
  public int getUserRank(@P("q") final GetUserRank getUserRank) {
    return this.taskProgressRepository.getUserRankForLanguage(getUserRank.userId(), getUserRank.languageId());
  }

  @SelfRequired(bypassRole = UserRole.ADMIN, field = "#q.userId()")
  @Override
  public TaskEntity getNextTask(@P("q") final GetNextTask getNextTask) {
    final UUID userId = getNextTask.userId();
    final TaskProgressState state = getNextTask.state();
    final UUID languageId = getNextTask.languageId();

    List<TaskEntity> tasks;

    if (state == TaskProgressState.NOT_STARTED) {
      System.out.println("Task not started");
      tasks = (languageId == null)
        ? this.taskProgressRepository.findTasksWithoutProgress(userId)
        : this.taskProgressRepository.findTasksWithoutProgress(userId, languageId);
    } else {
      System.out.println("Task already started");
      tasks = (languageId == null)
        ? this.taskProgressRepository.findTasksWithProgressByState(userId, state)
        : this.taskProgressRepository.findTasksWithProgressByState(userId, languageId, state);
    }

    return tasks
      .stream()
      //Todo move into db query
      .filter(taskEntity -> taskEntity.active())
      .findFirst()
      .orElseThrow(() -> new NotFoundException("No next task found."));
  }

  /* @Cacheable(
      value = "leaderboardCache",
      cacheManager = "cacheManager",
      keyGenerator = "leaderboardKeyGenerator",
      unless = "#result == null || #result.isEmpty()"
    )

    */
  @RoleRequired(role = UserRole.USER)
  @Override
  public List<TaskProgressRepository.UserRankingProjection> getLeaderboard(final GetLeaderboard getLeaderboard) {
    return this.taskProgressRepository.findUsersWithRankByLanguage(getLeaderboard.languageId(), getLeaderboard.start(),
      getLeaderboard.end());
  }

  @Override
  public TaskProgressEntity updateTaskProgress(final UpdateTaskProgress updateTaskProgress) {
    final TaskProgressTrio trio = this.taskProgress(updateTaskProgress);
    TaskProgressEntity taskProgressEntity = trio.taskProgressEntity();

    if (!trio.taskEntity().active()) {
      throw new IllegalInputException("Task is not active");
    }

    //Create new
    if (taskProgressEntity == null) {
      taskProgressEntity = new TaskProgressEntity(trio.userEntity(), trio.taskEntity(),
        updateTaskProgress.input(), updateTaskProgress.state(), updateTaskProgress.score());
    } else {
      if (taskProgressEntity.state() == TaskProgressState.FINISHED) {
        log.info("Already finished");
        return taskProgressEntity;
      }

      taskProgressEntity.input(updateTaskProgress.input());
      taskProgressEntity.state(updateTaskProgress.state());
      taskProgressEntity.score(updateTaskProgress.score());
    }
    try {
      return this.taskProgressRepository.save(taskProgressEntity);
    } catch (final DataAccessException exception) {
      log.error("Database error while creating taskProgress '{}': {}", taskProgressEntity.id(),
        exception.getMessage(), exception);
      throw new DatabaseException("Failed to create taskProgress in database", exception);
    }
  }

  @Override
  public TaskProgressEntity updateTaskProgress(final UpdateTaskProgressInput updateTaskProgressInput) {
    return this.updateTaskProgress(new UpdateTaskProgress() {
      @Override
      public TaskProgressState state() {
        return TaskProgressState.RUNNING;
      }

      @Override
      public int score() {
        return 0;
      }

      @Override
      public UUID userId() {
        return updateTaskProgressInput.userId();
      }

      @Override
      public UUID taskId() {
        return updateTaskProgressInput.taskId();
      }

      @Override
      public String input() {
        return updateTaskProgressInput.input();
      }
    });
  }

  private TaskProgressTrio taskProgress(final GetTaskProgress getTaskProgress) {
    final UserEntity userEntity = this.userService.getUser(getTaskProgress.userId());
    final TaskEntity taskEntity = this.taskService.getTask(() -> getTaskProgress.taskId());

    return new TaskProgressTrio(userEntity,
      taskEntity,
      this.taskProgressRepository.findByUserEntityAndTaskEntity(userEntity, taskEntity).orElse(null));
  }

  private record TaskProgressTrio(@NotNull UserEntity userEntity,
                                  @NotNull TaskEntity taskEntity,
                                  TaskProgressEntity taskProgressEntity) {

  }

  @Bean("userRankKeyGenerator")
  public KeyGenerator userRankKeyGenerator() {
    return (target, method, params) -> {
      final GetUserRank q = (GetUserRank) params[0];
      return q.userId().toString() + "-" + q.languageId().toString();
    };
  }

  @Bean("leaderboardKeyGenerator")
  public KeyGenerator leaderboardKeyGenerator() {
    return (target, method, params) -> {
      final Object param = params[0];
      if (param instanceof GetLeaderboard) {
        final GetLeaderboard q = (GetLeaderboard) param;
        return q.languageId().toString() + "-" + q.start() + "-" + q.end();
      }

      return SimpleKey.EMPTY;
    };
  }
}
