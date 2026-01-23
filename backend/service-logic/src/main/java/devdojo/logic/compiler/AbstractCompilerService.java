package devdojo.logic.compiler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import devdojo.logic.cache.compiler.CompilerTask;
import devdojo.logic.cache.compiler.CompilerTaskRepository;
import devdojo.logic.cache.compiler.CompilerTaskResult;
import devdojo.logic.cache.compiler.CompilerTaskResultRepository;
import devdojo.logic.database.compiler.CompilerEntity;
import devdojo.logic.database.compiler.CompilerHeartbeatEntity;
import devdojo.logic.database.compiler.CompilerHeartbeatRepository;
import devdojo.logic.database.compiler.CompilerRepository;
import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.language.LanguageRepository;
import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.database.task.TaskProgressEntity;
import devdojo.logic.database.task.TestEntity;
import devdojo.logic.database.user.UserEntity;
import devdojo.logic.security.SecurityUtils;
import devdojo.logic.task.ITaskService;
import devdojo.logic.task.ITasksProgressService;
import devdojo.logic.user.IUserService;
import devdojo.logic.websocket.ISocketPublisher;
import devdojo.schema.code.TestRun;
import devdojo.schema.code.TestRunResult;
import devdojo.schema.code.TestRunResultChecked;
import devdojo.schema.compiler.CompilerResourceAnalytic;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.task.ArgumentType;
import devdojo.schema.task.ITestArgument;
import devdojo.schema.task.TaskProgressState;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
public abstract class AbstractCompilerService implements ICompilerService {

  private final ObjectMapper objectMapper;
  private final LanguageRepository languageRepository;
  private final ITaskService taskService;
  private final ITasksProgressService tasksProgressService;
  private final CompilerRepository compilerRepository;
  private final CompilerHeartbeatRepository compilerHeartbeatRepository;
  private final CompilerTaskRepository compilerTaskRepository;
  private final CompilerTaskResultRepository compilerTaskResultRepository;
  private final ISocketPublisher socketPublisher;
  private final IUserService userService;

  protected AbstractCompilerService(
    final ObjectMapper objectMapper,
    @NotNull(message = "LanguageRepository must not be null") final LanguageRepository languageRepository,
    final ITaskService taskService,
    final ITasksProgressService tasksProgressService,
    final CompilerRepository compilerRepository,
    final CompilerHeartbeatRepository compilerHeartbeatRepository,
    final CompilerTaskRepository compilerTaskRepository,
    final CompilerTaskResultRepository compilerTaskResultRepository,
    final ISocketPublisher socketPublisher,
    final IUserService userService) {
    this.objectMapper = objectMapper;
    this.languageRepository = languageRepository;
    this.taskService = taskService;
    this.tasksProgressService = tasksProgressService;
    this.compilerRepository = compilerRepository;
    this.compilerHeartbeatRepository = compilerHeartbeatRepository;
    this.compilerTaskRepository = compilerTaskRepository;
    this.compilerTaskResultRepository = compilerTaskResultRepository;
    this.socketPublisher = socketPublisher;
    this.userService = userService;
  }

  @Override
  public CompilerHeartbeatEntity updateHeartbeat(final UpdateHeartbeat updateHeartbeat) {
    log.debug("Updating heartbeat for languageToken='{}'", updateHeartbeat.languageToken());

    final LanguageEntity languageEntity =
      this.languageRepository.findByToken(updateHeartbeat.languageToken()).orElseThrow();


    final CompilerEntity compilerEntity = this.compilerRepository
      .findByCompilerId(updateHeartbeat.id())
      .orElseGet(() -> this.compilerRepository.save(new CompilerEntity(
        languageEntity, updateHeartbeat.id(), updateHeartbeat.cpuCores(), updateHeartbeat.memoryTotalMb())));

    final CompilerHeartbeatEntity compilerHeartbeatEntity = new CompilerHeartbeatEntity(compilerEntity,
      updateHeartbeat.memoryUsageMb(),
      updateHeartbeat.cpuUsagePercent(),
      updateHeartbeat.totalTasksProcessed());

    return this.compilerHeartbeatRepository.save(compilerHeartbeatEntity);
  }

  @Override
  public CompilerTaskResult getCompilerTask(final GetCompilerTask getCompilerTask) {
    return this.compilerTaskResultRepository.findById(getCompilerTask.id())
      .orElseThrow(() -> new NotFoundException("No compiler task found for id=" + getCompilerTask.id()));
  }

  @Override
  public CompilerTask createCompilerTask(final CreateCompilerTask createCompilerTask) {
    return this.compilerTaskRepository.save(
      new CompilerTask(createCompilerTask.id(), false, createCompilerTask.methodCode(),
        UUID.fromString(createCompilerTask.taskId()), SecurityUtils.currentTokenDetails().userId()));
  }

  @Transactional
  @Override
  public CompilerTaskResult completeCompilerTask(final CompleteCompilerTask completeCompilerTask) {
    final CompilerTask compilerTask = this.compilerTaskRepository
      .findById(completeCompilerTask.id())
      .orElseThrow(() -> new NotFoundException("No compiler task found for id=" + completeCompilerTask.id()));

    final TaskEntity taskEntity = this.taskService.getTask(compilerTask::taskId);
    final List<TestRunResult> results = completeCompilerTask.results();
    final List<TestRunResultChecked> resultUnits = results.stream()
      .map(result -> mapToResultUnit(result, taskEntity))
      .toList();

    final boolean complete = resultUnits.stream().allMatch(TestRunResultChecked::pass);

    TaskProgressState state = TaskProgressState.NOT_STARTED;
    int score = 0;

    if (!complete && !compilerTask.methodCode().trim().equalsIgnoreCase(taskEntity.template())) {
      state = TaskProgressState.RUNNING;
    }

    if (complete) {
      state = TaskProgressState.FINISHED;
      score = taskEntity.difficulty().score();
    }

    int finalScore = score;
    TaskProgressState finalState = state;

    final TaskProgressEntity taskProgressEntity =
      this.tasksProgressService.updateTaskProgress(new ITasksProgressService.UpdateTaskProgress() {
        @Override
        public String input() {
          return compilerTask.methodCode().trim();
        }

        @Override
        public TaskProgressState state() {
          return finalState;
        }

        @Override
        public int score() {
          return finalScore;
        }

        @Override
        public UUID userId() {
          return compilerTask.userId();
        }

        @Override
        public UUID taskId() {
          return taskEntity.id();
        }
      });

    final TestRun run = new TestRun(
      compilerTask.id(),
      resultUnits,
      taskProgressEntity.state() == TaskProgressState.FINISHED,
      taskProgressEntity.score());

    final CompilerTaskResult result;
    try {
      result = this.compilerTaskResultRepository.save(
        new CompilerTaskResult(compilerTask.id(), compilerTask.userId(), this.objectMapper.writeValueAsString(run)));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    compilerTask.processed(true);
    this.compilerTaskRepository.save(compilerTask);
    this.sendWebsocket(compilerTask, run);

    return result;
  }

  private void sendWebsocket(final CompilerTask compilerTask,
                             final TestRun testRun) {
    try {
      final UserEntity userEntity = this.userService.getUser(compilerTask.userId());
      this.socketPublisher.notifyTaskUpdate(userEntity.authId(), testRun);
    } catch (final NotFoundException exception) {

    }
  }

  @Transactional
  @Override
  public void cleanup() {
    final Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);
    final int compilerDeleted = this.compilerRepository.deleteAllWithLastHeartbeatBefore(Date.from(cutoff));
    log.debug("Deleted inactive compilers: {}", compilerDeleted);

    final Instant cutoffHeartbeats = Instant.now().minus(2, ChronoUnit.DAYS);
    this.compilerHeartbeatRepository.deleteByCreatedAtBefore(Date.from(cutoffHeartbeats));
  }

  @Override
  public List<CompilerEntity> getCompilers() {
    return this.compilerRepository.findAll();
  }

  @Override
  public CompilerEntity getCompiler(final GetCompiler getCompiler) {
    return this.compilerRepository.findByCompilerId(getCompiler.id())
      .orElseThrow(() -> new NotFoundException("No compiler with id " + getCompiler.id()));
  }

  @Override
  public CompilerHeartbeatEntity getLastCompilerHeartbeat(final GetCompiler getCompiler) {
    return this.compilerHeartbeatRepository.findTopByCompilerEntity_CompilerIdOrderByCreatedAtDesc(getCompiler.id())
      .orElseThrow(() -> new NotFoundException("No compiler with id " + getCompiler.id()));
  }

  private final static int CPU_INDEX = 1;
  private final static int MEMORY_INDEX = 2;

  @Override
  public List<CompilerResourceAnalytic> getCompilerResourceAnalytics(final GetCompiler getCompiler) {
    final CompilerEntity compilerEntity = this.getCompiler(getCompiler);

    return this.compilerHeartbeatRepository.findAggregatedByCompilerId(compilerEntity.id()).stream()
      .map(row -> {
        final double percentageCpu = Math.min(100, Math.max(0, (Double) row[CPU_INDEX]));
        final double percentageMemory = Math.min(100, Math.max(0, (Double) row[MEMORY_INDEX]));

        return new CompilerResourceAnalytic(
          ((Instant) row[0]).toEpochMilli(),
          percentageCpu,
          percentageMemory
        );
      })
      .toList();
  }


  private TestRunResultChecked mapToResultUnit(final TestRunResult result,
                                               final TaskEntity taskEntity) {
    final TestEntity matchedTest = findMatchingTestEntity(result, taskEntity);

    boolean pass = false;

    if (matchedTest != null && matchedTest.output() != null) {
      if (matchedTest.output().type() == ArgumentType.OUTPUT) {
        pass = checkOutputMatch(result, matchedTest);
      } else if (matchedTest.output().type() == ArgumentType.THROWABLE) {
        pass = checkThrowableMatch(result, matchedTest);
      }
    }

    return new TestRunResultChecked(
      result.input(),
      result.output(),
      result.logs(),
      result.errors(),
      result.compileTimeMs(),
      result.runTimeMs(),
      pass
    );
  }

  private TestEntity findMatchingTestEntity(final TestRunResult result,
                                            final TaskEntity taskEntity) {
    return taskEntity.testEntities().stream()
      .filter(testEntity -> Objects.equals(
        result.input(),
        testEntity.input().stream().map(ITestArgument::value).toList()
      ))
      .findFirst()
      .orElse(null);
  }

  private boolean checkOutputMatch(final TestRunResult result,
                                   final TestEntity testEntity) {
    return Objects.equals(
      String.valueOf(result.output()),
      String.valueOf(testEntity.output().value())
    );
  }

  private boolean checkThrowableMatch(final TestRunResult result,
                                      final TestEntity testEntity) {
    if (result == null || result.errors() == null || testEntity == null || testEntity.output() == null) {
      return false;
    }

    final String expectedType = testEntity.output().variant();
    final String expectedMessage = String.valueOf(testEntity.output().value());

    return result.errors().stream()
      .anyMatch(error ->
        expectedType != null &&
          expectedType.equals(error.type()) &&
          expectedMessage != null &&
          expectedMessage.equalsIgnoreCase(
            error.message() != null ? error.message() : ""
          )
      );
  }
}
