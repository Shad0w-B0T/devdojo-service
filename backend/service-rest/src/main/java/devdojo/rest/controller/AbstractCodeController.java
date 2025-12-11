package devdojo.rest.controller;

import devdojo.logic.cache.bucket4j.annotation.RateLimited;
import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.database.task.TestArgumentEntity;
import devdojo.logic.code.ICodeService;
import devdojo.logic.compiler.ICompilerService;
import devdojo.logic.task.ITaskService;
import devdojo.logic.task.ITasksProgressService;
import devdojo.schema.task.ArgumentType;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/code")
@Slf4j
@Validated
public abstract class AbstractCodeController implements ICodeController {

  private final static long CODE_RATE_LIMIT_REFILL = 1000;

  private final ITaskService taskService;
  private final ICodeService codeService;
  private final ITasksProgressService tasksProgressService;
  private final ICompilerService compilerService;

  public AbstractCodeController(
    @NotNull(message = "TaskService must not be null") final ITaskService taskService,
    @NotNull(message = "CodeService must not be null") final ICodeService codeService,
    @NotNull(message = "TasksProgressService must not be null") final ITasksProgressService tasksProgressService,
    @NotNull(message = "CompilerService must not be null") final ICompilerService compilerService) {
    this.taskService = taskService;
    this.codeService = codeService;
    this.tasksProgressService = tasksProgressService;
    this.compilerService = compilerService;
  }


  @RateLimited(
    bucket = "#{'rate-code-' + T(devdojo.logic.security.SecurityUtils).localThreadClient().userId()}",
    refillDurationMillis = CODE_RATE_LIMIT_REFILL,
    totalTokens = 1)
  @PostMapping("/exercises")
  @ResponseBody
  @Override
  public ResponseEntity<IProcessCodeResponse> processCode(@RequestBody final String payload,
                                                          @RequestParam(value = "taskId") final UUID taskId) {
    final TaskEntity taskEntity = this.taskService.getTask(() -> taskId);
    final List<List<Object>> testInputs = extractTestInputs(taskEntity);

    final UUID results = this.codeService.executeCode(taskEntity, payload, testInputs);

    System.out.println("Extracted results: " + results);
/*
    final List<CodeResultUnit> resultUnits = results.stream()
      .map(result -> mapToResultUnit(result, taskEntity))
      .toList();

    System.out.println("Mapped results: " + resultUnits);

    final boolean complete = resultUnits.stream().allMatch(codeResultUnit -> codeResultUnit.pass());

    TaskProgressState state = TaskProgressState.NOT_STARTED;
    int score = 0;

    if (!complete && !payload.trim().equalsIgnoreCase(taskEntity.template())) {
      state = TaskProgressState.RUNNING;
    }

    if (complete) {
      state = TaskProgressState.FINISHED;
      score = taskEntity.difficulty().score();
    }

    System.out.println("state: " + state);
    System.out.println("Score: " + score);

    int finalScore = score;
    TaskProgressState finalState = state;

    final TaskProgressEntity taskProgressEntity =
      this.tasksProgressService.updateTaskProgress(new ITasksProgressService.UpdateTaskProgress() {
        @Override
        public String input() {
          return payload.trim();
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
          return SecurityUtils.localThreadClient().userId();
        }

        @Override
        public UUID taskId() {
          return taskId;
        }
      });

    System.out.println(taskProgressEntity);

    return ResponseEntity.ok(new ProcessCodeResponse(resultUnits,
      taskProgressEntity.state() == TaskProgressState.FINISHED,
      taskProgressEntity.score()));

 */

    return ResponseEntity.ok(new ProcessCodeResponse(results));
  }

  private List<List<Object>> extractTestInputs(final TaskEntity taskEntity) {
    return taskEntity.testEntities().stream()
      .map(testEntity -> testEntity.argumentEntities().stream()
        .filter(arg -> arg.type() == ArgumentType.INPUT)
        .map(TestArgumentEntity::value)
        .toList())
      .toList();
  }

  private record ProcessCodeResponse(UUID id) implements IProcessCodeResponse {

  }
}
