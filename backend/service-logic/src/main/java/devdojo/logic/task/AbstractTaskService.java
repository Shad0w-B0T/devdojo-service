package devdojo.logic.task;

import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.database.task.TaskRepository;
import devdojo.logic.database.task.TestArgumentEntity;
import devdojo.logic.database.task.TestEntity;
import devdojo.logic.language.ILanguageService;
import devdojo.schema.annotation.RoleRequired;
import devdojo.schema.exception.database.DatabaseException;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.task.ArgumentType;
import devdojo.schema.user.UserRole;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.annotation.Validated;

@Validated
@Slf4j
public abstract class AbstractTaskService implements ITaskService {

  private final @NotNull ILanguageService languageService;
  private final @NotNull TaskRepository taskRepository;

  protected AbstractTaskService(
    @NotNull(message = "LanguageService must not be null") final ILanguageService languageService,
    @NotNull(message = "TaskRepository must not be null") final TaskRepository taskRepository) {
    this.languageService = languageService;
    this.taskRepository = taskRepository;
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
    final LanguageEntity languageEntity = this.languageService.getLanguage(() -> createTask.languageId());

    final TaskEntity task = new TaskEntity(
      languageEntity,
      createTask.name(),
      createTask.description(),
      createTask.difficulty(),
      createTask.template(),
      createTask.complete(),
      createTask.active()
    );

    createTask.unitTests()
      .stream().map(this::testEntity)
      .forEach(finalTest -> {
        finalTest.taskEntity(task);
        task.testEntities().add(finalTest);
        log.debug("Added unit test to task '{}'", createTask.name());
      });

    try {
      final TaskEntity savedTask = taskRepository.save(task);
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
    final TaskEntity task = taskRepository.findById(updateTask.taskId())
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

    task.testEntities().clear();
    updateTask.unitTests()
      .stream().map(this::testEntity)
      .forEach(finalTest -> {
        finalTest.taskEntity(task);
        task.testEntities().add(finalTest);
        log.debug("Added unit test during update to task with ID {}", updateTask.taskId());
      });

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
