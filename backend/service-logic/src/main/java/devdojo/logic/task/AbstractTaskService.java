package devdojo.logic.task;

import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.tag.TagEntity;
import devdojo.logic.database.task.*;
import devdojo.logic.database.user.UserEntity;
import devdojo.logic.database.user.UserRepository;
import devdojo.logic.language.ILanguageService;
import devdojo.logic.user.IUserService;
import devdojo.logic.security.SecurityUtils;
import devdojo.logic.user.IUserService;
import devdojo.schema.annotation.RoleRequired;
import devdojo.schema.exception.database.DatabaseException;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.exception.permissions.UnauthorizedException;
import devdojo.schema.task.ArgumentType;
import devdojo.schema.user.UserRole;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.annotation.Validated;

@Validated
@Slf4j
public abstract class AbstractTaskService implements ITaskService {

  private final static int DIGITS_IN_DECIMAL = 2;

  private final @NotNull ILanguageService languageService;
  private final @NotNull IUserService userService;
  private final @NotNull TaskRepository taskRepository;
  private final @NotNull TaskRatingRepository ratingRepository;

  protected AbstractTaskService(
    @NotNull(message = "LanguageService must not be null") final ILanguageService languageService,
    @NotNull(message = "UserService must not be null") final IUserService userService,
    @NotNull(message = "TaskRepository must not be null") final TaskRepository taskRepository,
    @NotNull(message = "TaskRatingRepository must not be null") final TaskRatingRepository ratingRepository) {
    this.languageService = languageService;
    this.userService = userService;
    this.taskRepository = taskRepository;
    this.ratingRepository = ratingRepository;
    log.info("AbstractTaskService initialized with TaskRepository");
  }

  @Transactional
  @Override
  public List<TaskEntity> getTasks(final GetTasks getTasks) {
    if (getTasks == null || getTasks.languageId() == null) {
      return this.taskRepository.findAll();
    }

    final LanguageEntity languageEntity = this.languageService.getLanguage(() -> getTasks.languageId());

    return this.taskRepository.findAllByLanguageEntity(languageEntity);
  }

  @Override
  @Transactional
  public TaskEntity getTask(final GetTask getTask) {
    log.debug("Fetching task with ID {}", getTask.taskId());
    return this.task(getTask.taskId());
  }

  /**
   * Retrieves a task entity by its unique identifier.
   *
   * @param taskId the UUID of the task to retrieve
   * @return the found TaskEntity
   * @throws NotFoundException if no task with the given ID exists
   */
  private @NotNull TaskEntity task(final @NotNull UUID taskId) {
    return taskRepository.findById(taskId)
      .orElseThrow(() -> {
        log.warn("Task with ID {} not found", taskId);
        return new NotFoundException("Task not found");
      });
  }

  @RoleRequired(role = UserRole.PROFESSOR)
  @Override
  @Transactional
  public TaskEntity createTask(final CreateTask createTask) {
    log.info("Creating new task with name '{}'", createTask.name());
    final LanguageEntity languageEntity = this.languageService.getLanguage(createTask::languageId);

    final TaskEntity task = new TaskEntity(
      languageEntity,
      createTask.name(),
      createTask.description(),
      createTask.difficulty(),
      createTask.template(),
      createTask.complete(),
      createTask.active()
    );

    final List<TagEntity> tags =
      Optional.ofNullable(createTask.tags())
        .orElse(List.of())
        .stream()
        .distinct()
        .map(name -> new TagEntity(task, name))
        .toList();

    task.setTagEntities(tags);

    createTask.unitTests()
      .stream().map(this::testEntity)
      .forEach(finalTest -> {
        finalTest.taskEntity(task);
        task.testEntities().add(finalTest);
        log.debug("Added unit test to task '{}'", createTask.name());
      });

    try {
      task.createdByUserEntity(Optional
        .ofNullable(SecurityUtils.currentTokenDetails().userId())
        .map(this.userService::getUser)
        .orElse(null));
    } catch (final UnauthorizedException | NotFoundException exception) {
      log.info("No created user found for task.");
    }

    try {
      final TaskEntity savedTask = this.taskRepository.save(task);
      log.info("Task '{}' created with ID {}", createTask.name(), savedTask.id());
      return savedTask;
    } catch (final DataAccessException exception) {
      log.error("Database error while creating task '{}': {}", createTask.name(), exception.getMessage(), exception);
      throw new DatabaseException("Failed to create task in database", exception);
    }
  }

  @RoleRequired(role = UserRole.PROFESSOR)
  @Override
  @Transactional
  public TaskEntity updateTask(final UpdateTask updateTask) {
    log.info("Updating task with ID {}", updateTask.taskId());
    final TaskEntity task = this.taskRepository.findById(updateTask.taskId())
      .orElseThrow(() -> {
        log.warn("Task with ID {} not found for update", updateTask.taskId());
        return new NotFoundException("Task not found");
      });

    task.name(updateTask.name());
    task.description(updateTask.description());
    task.difficulty(updateTask.difficulty());
    task.template(updateTask.template());
    task.complete(updateTask.complete());
    task.active(updateTask.active());

    final List<TagEntity> tags =
      Optional.ofNullable(updateTask.tags())
        .orElse(List.of())
        .stream()
        .distinct()
        .map(name -> new TagEntity(task, name))
        .toList();

    task.setTagEntities(tags);

    task.testEntities().clear();
    updateTask.unitTests()
      .stream().map(this::testEntity)
      .forEach(finalTest -> {
        finalTest.taskEntity(task);
        task.testEntities().add(finalTest);
        log.debug("Added unit test during update to task with ID {}", updateTask.taskId());
      });

    try {
      task.updatedByUserEntity(Optional
        .ofNullable(SecurityUtils.currentTokenDetails().userId())
        .map(this.userService::getUser)
        .orElse(null));
    } catch (final UnauthorizedException | NotFoundException exception) {
      log.info("No updated user found for task.");
    }

    try {
      final TaskEntity savedTask = taskRepository.save(task);
      log.info("Task with ID {} updated successfully", updateTask.taskId());
      return savedTask;
    } catch (final DataAccessException exception) {
      log.error("Database error while updating task with ID {}: {}", updateTask.taskId(),
        exception.getMessage(), exception);
      throw new DatabaseException("Failed to update task in database", exception);
    }
  }


  /**
   * Removes a tag from a task.
   * <p>
   * This method removes the association between a task and a tag by deleting the tag
   * from the list of tags associated with the task. The tag itself remains in the database,
   * only the relationship between the task and the tag is deleted.
   *
   * @param removeTagFromTask The input object containing the task ID and the tag ID to be removed.
   * @return true if the tag was successfully removed from the task, false if the tag was not found.
   * @throws NullPointerException If the input object or any of the IDs is null.
   */
  @RoleRequired(role = UserRole.PROFESSOR)
  @Override
  @Transactional
  public boolean removeTagFromTask(final RemoveTagFromTask removeTagFromTask) {

    log.info("Removing tag with ID {} from task with ID {}", removeTagFromTask.tagId(), removeTagFromTask.taskId());

    final TaskEntity task = this.task(removeTagFromTask.taskId());

    final TagEntity tagToRemove = task.tagEntities().stream()
      .filter(tag -> tag.id().equals(removeTagFromTask.tagId()))
      .findFirst()
      .orElse(null);

    if (tagToRemove != null) {
      task.tagEntities().remove(tagToRemove);
      log.info("Tag with ID {} removed from task with ID {}", removeTagFromTask.tagId(), removeTagFromTask.taskId());
      return true;
    } else {
      log.info("Tag with ID {} not found on task with ID {}", removeTagFromTask.tagId(), removeTagFromTask.taskId());
      return false;
    }
  }

  @RoleRequired(role = UserRole.PROFESSOR)
  @Override
  @Transactional
  public TaskEntity appendUnitTest(final AppendTest append) {
    log.info("Appending unit test to task with ID {}", append.taskId());
    final TaskEntity task = this.task(append.taskId());

    task.testEntities().add(testEntity(append.unitTest()));
    log.debug("Unit test appended to task with ID {}", append.taskId());

    try {
      final TaskEntity savedTask = this.taskRepository.save(task);
      log.info("Task with ID {} saved after appending unit test", append.taskId());
      return savedTask;
    } catch (final DataAccessException exception) {
      log.error("Database error while appending unit test to task with ID {}: {}", append.taskId(),
        exception.getMessage(), exception);
      throw new DatabaseException("Failed to append unit test in database", exception);
    }
  }

  @RoleRequired(role = UserRole.PROFESSOR)
  @Override
  @Transactional
  public TaskEntity deleteTask(final DeleteTask deleteTask) {

    log.info("Deleting task with ID {}", deleteTask.taskId());
    final TaskEntity task = this.task(deleteTask.taskId());

    try {
      this.taskRepository.delete(task);
      log.info("Task with ID {} deleted successfully", deleteTask.taskId());
    } catch (final DataAccessException exception) {
      log.error("Database error while deleting task with ID {}: {}", deleteTask.taskId(),
        exception.getMessage(), exception);
      throw new DatabaseException("Failed to delete task from database", exception);
    }

    return task;
  }

  @CacheEvict(
    cacheNames = "taskRating",
    key = "#p0.taskId()"
  )
  @Transactional
  @Override
  public TaskRatingEntity setTaskRating(final SetTaskRating setTaskRating) {
    final UUID userId = setTaskRating.userId();
    final UUID taskId = setTaskRating.taskId();
    final int rating = setTaskRating.rating();

    log.debug(
      "Set task rating requested [taskId={}, userId={}, rating={}]",
      taskId, userId, rating
    );

    final TaskRatingEntity taskRating = this.ratingRepository
      .findByTaskEntity_IdAndUserEntity_Id(taskId, userId)
      .orElse(null);

    if (taskRating == null) {
      log.debug(
        "Creating new task rating [taskId={}, userId={}, rating={}]",
        taskId, userId, rating
      );

      final UserEntity userEntity = this.userService.getUser(userId);
      final TaskEntity taskEntity = this.task(taskId);

      try {
        final TaskRatingEntity saved = this.ratingRepository.save(
          new TaskRatingEntity(taskEntity, userEntity, rating)
        );

        log.info(
          "Task rating created successfully [taskId={}, userId={}, ratingId={}]",
          taskId, userId, saved.id()
        );

        return saved;
      } catch (final DataAccessException exception) {
        log.error(
          "Database error while creating task rating [taskId={}, userId={}, rating={}]",
          taskId, userId, rating, exception
        );
        throw new DatabaseException("Failed to create task rating", exception);
      }
    }

    log.debug(
      "Updating existing task rating [taskId={}, userId={}, oldRating={}, newRating={}]",
      taskId, userId, taskRating.rating(), rating
    );

    taskRating.rating(rating);

    try {
      final TaskRatingEntity saved = this.ratingRepository.save(taskRating);

      log.info(
        "Task rating updated successfully [taskId={}, userId={}, ratingId={}]",
        taskId, userId, saved.id()
      );

      return saved;
    } catch (final DataAccessException exception) {
      log.error(
        "Database error while updating task rating [taskId={}, userId={}, ratingId={}]",
        taskId, userId, taskRating.id(), exception
      );
      throw new DatabaseException("Failed to update task rating", exception);
    }
  }

  @Cacheable(
    cacheNames = "taskRating",
    key = "#p0.taskId()",
    unless = "#result == null"
  )
  @Override
  public Double getTaskRating(final GetTask getTask) {
    final UUID taskId = getTask.taskId();

    log.debug("Fetching average rating for task [taskId={}]", taskId);

    final Double average = this.ratingRepository.findAverageRatingByTaskId(taskId);

    final double rounded = Optional.ofNullable(average)
      .map(a -> BigDecimal
        .valueOf(a)
        .setScale(DIGITS_IN_DECIMAL, RoundingMode.HALF_UP)
        .doubleValue())
      .orElse(0.0);

    if (average == null) {
      log.debug(
        "No ratings found for task [taskId={}], returning default rating 0.0",
        taskId
      );
    } else {
      log.debug(
        "Average rating calculated for task [taskId={}, raw={}, rounded={}]",
        taskId, average, rounded
      );
    }

    return rounded;
  }

  /**
   * Converts TestData DTO into a TestEntity with associated TestArgumentEntities.
   *
   * @param data the TestData to convert
   * @return the created TestEntity
   * @throws IllegalArgumentException if any input argument is not of type INPUT
   */
  private TestEntity testEntity(final @NotNull TestData data) {
    final TestEntity test = new TestEntity();

    final List<TestArgumentEntity> args = IntStream.range(0, data.input().size())
      .mapToObj(i -> {
        final TestArgumentData argumentData = data.input().get(i);

        if (argumentData.type() != ArgumentType.INPUT) {
          log.error("Invalid argument type for input test argument: expected INPUT but got {}", argumentData.type());
          throw new IllegalArgumentException("ArgumentType must be INPUT");
        }

        return new TestArgumentEntity(test, ArgumentType.INPUT, argumentData.variant(), argumentData.value(), i);
      }).collect(Collectors.toList());

    args.add(new TestArgumentEntity(test, data.output().type(),
      data.output().variant(), data.output().value(), 0));

    test.argumentEntities(args);
    log.debug("Created TestEntity with {} arguments", args.size());
    return test;
  }
}
