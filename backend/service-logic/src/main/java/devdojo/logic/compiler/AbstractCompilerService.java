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
import devdojo.logic.database.user.UserEntity;
import devdojo.logic.language.ILanguageService;
import devdojo.logic.language.LanguageService;
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
import devdojo.schema.task.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
public abstract class AbstractCompilerService implements ICompilerService {

  private final ObjectMapper objectMapper;
  private final ILanguageService languageService;
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
    final ILanguageService languageService,
    final ITaskService taskService,
    final ITasksProgressService tasksProgressService,
    final CompilerRepository compilerRepository,
    final CompilerHeartbeatRepository compilerHeartbeatRepository,
    final CompilerTaskRepository compilerTaskRepository,
    final CompilerTaskResultRepository compilerTaskResultRepository,
    final ISocketPublisher socketPublisher,
    final IUserService userService) {
    this.objectMapper = objectMapper;
    this.languageService = languageService;
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
      this.languageService.getLanguage(new ILanguageService.GetLanguage(updateHeartbeat.languageToken()));


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
        createCompilerTask.unitTests().stream()
          .map(iTest -> {
            try {
              return new Test(
                iTest.input().stream()
                  .map(arg -> {
                    try {
                      return new TestArgument(arg.type(), arg.variant(),
                        objectMapper.writeValueAsString(arg.value()));
                    } catch (JsonProcessingException e) {
                      throw new RuntimeException(e);
                    }
                  })
                  .toList(),
                new TestArgument(iTest.output().type(), iTest.output().variant(),
                  objectMapper.writeValueAsString(iTest.output().value()))
              );
            } catch (JsonProcessingException e) {
              throw new RuntimeException(e);
            }
          })
          .toList(),
        createCompilerTask.taskId(),
        createCompilerTask.languageId(),
        SecurityUtils.currentTokenDetails().userId()));
  }

  @Transactional
  @Override
  public CompilerTaskResult completeCompilerTask(final CompleteCompilerTask completeCompilerTask) {

    final CompilerTask compilerTask = this.compilerTaskRepository
      .findById(completeCompilerTask.id())
      .orElseThrow(() -> new NotFoundException("No compiler task found for id=" + completeCompilerTask.id()));

    final List<ITest> expectedTests = List.copyOf(compilerTask.tests());

    final List<TestRunResultChecked> checkedResults =
      completeCompilerTask.results().stream()
        .map(result -> mapToResultUnit(result, expectedTests))
        .toList();

    final boolean allPassed = checkedResults.stream().allMatch(TestRunResultChecked::pass);

    TaskProgressState state = TaskProgressState.NOT_STARTED;
    int score = 0;
    boolean finished = allPassed;

    if (compilerTask.taskId() != null) {
      final TaskEntity taskEntity =
        this.taskService.getTask(new ITaskService.GetTask(compilerTask.taskId()));

      state = computeProgressState(taskEntity, compilerTask.methodCode(), allPassed);
      score = allPassed ? taskEntity.difficulty().score() : 0;


      final TaskProgressEntity taskProgressEntity =
        this.tasksProgressService.updateTaskProgress(
          new ITasksProgressService.UpdateTaskProgress(
            compilerTask.userId(),
            taskEntity.externalId(),
            compilerTask.methodCode().trim(),
            state,
            score
          )
        );

      finished = taskProgressEntity.state() == TaskProgressState.FINISHED;
      score = taskProgressEntity.score();
    }

    final TestRun run = new TestRun(
      compilerTask.id(),
      checkedResults,
      finished,
      score
    );

    final CompilerTaskResult stored = storeResult(compilerTask, run);

    compilerTask.processed(true);
    this.compilerTaskRepository.save(compilerTask);

    sendWebsocket(compilerTask, run);

    return stored;
  }

  private Object deserialize(final Object value) {
    if (value == null) {
      return null;
    }

    if (value instanceof String s) {
      try {
        return objectMapper.readValue(s, Object.class);
      } catch (Exception e) {
        return s;
      }
    }

    return value;
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


  private CompilerTaskResult storeResult(final CompilerTask compilerTask, final TestRun run) {
    try {
      return this.compilerTaskResultRepository.save(
        new CompilerTaskResult(
          compilerTask.id(),
          compilerTask.userId(),
          this.objectMapper.writeValueAsString(run)
        )
      );
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private TaskProgressState computeProgressState(final TaskEntity taskEntity,
                                                 final String methodCode,
                                                 final boolean allPassed) {

    if (allPassed) {
      return TaskProgressState.FINISHED;
    }

    if (!methodCode.trim().equalsIgnoreCase(taskEntity.template())) {
      return TaskProgressState.RUNNING;
    }

    return TaskProgressState.NOT_STARTED;
  }


  private TestRunResultChecked mapToResultUnit(
    final TestRunResult result,
    final List<? extends ITest> expectedTests) {

    final ITest matched = expectedTests.stream()
      .filter(test -> jsonEquals(result.input(), expectedInputList(test)))
      .findFirst()
      .orElse(null);

    boolean pass = false;

    if (matched != null && matched.output() != null) {
      if (matched.output().type() == ArgumentType.OUTPUT) {
        pass = jsonEquals(
          result.output(),
          deserialize(matched.output().value())
        );
      } else if (matched.output().type() == ArgumentType.THROWABLE) {
        pass = checkThrowableMatch(result, matched);
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

  private List<Object> expectedInputList(final ITest test) {
    return test.input().stream()
      .filter(arg -> arg.type() == ArgumentType.INPUT)
      .map(arg -> deserialize(arg.value()))
      .toList();
  }

  private boolean jsonEquals(final Object a, final Object b) {
    try {
      if (a == null && b == null) {
        return true;
      }
      if (a == null || b == null) {
        return false;
      }

      final String jsonA = objectMapper.writeValueAsString(a);
      final String jsonB = objectMapper.writeValueAsString(b);

      return jsonA.equals(jsonB);
    } catch (Exception e) {
      return false;
    }
  }

  private boolean checkThrowableMatch(
    final TestRunResult result,
    final ITest expectedTest) {

    if (result == null || result.errors() == null) {
      return false;
    }

    final String expectedType =
      expectedTest.output().variant() != null
        ? String.valueOf(deserialize(expectedTest.output().variant()))
        : null;

    final String expectedMessage =
      expectedTest.output().value() != null
        ? String.valueOf(deserialize(expectedTest.output().value()))
        : null;

    return result.errors().stream().anyMatch(error -> {
      final boolean typeMatch =
        expectedType != null && expectedType.equals(error.type());

      if (!typeMatch) {
        return false;
      }

      if (expectedMessage == null) {
        return true;
      }

      final String actualMessage =
        error.message() != null ? error.message() : "";

      return expectedMessage.equalsIgnoreCase(actualMessage);
    });
  }

  private void sendWebsocket(final CompilerTask compilerTask,
                             final TestRun testRun) {
    try {
      final UserEntity userEntity = this.userService.getUser(compilerTask.userId());
      this.socketPublisher.notifyTaskUpdate(userEntity.authId(), testRun);
    } catch (Exception e) {
      log.error("No user found to send task compile response={}, ignoring. ", compilerTask.id());
    }
  }
}
