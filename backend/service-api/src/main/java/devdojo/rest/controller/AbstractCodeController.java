package devdojo.rest.controller;

import devdojo.logic.cache.bucket4j.annotation.RateLimited;
import devdojo.logic.code.ICodeService;
import devdojo.logic.compiler.ICompilerService;
import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.database.task.TestArgumentEntity;
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
    bucket = "#{'rate-code-' + T(devdojo.logic.security.SecurityUtils).currentTokenDetails().userId()}",
    refillDurationMillis = CODE_RATE_LIMIT_REFILL,
    totalTokens = 1)
  @PostMapping("/exercises")
  @ResponseBody
  @Override
  public ResponseEntity<IProcessCodeResponse> processCode(@RequestBody final String payload,
                                                          @RequestParam(value = "taskId") final String taskId) {
    final TaskEntity taskEntity = this.taskService.getTask(new ITaskService.GetTask(taskId));
    final List<List<Object>> testInputs = extractTestInputs(taskEntity);

    final UUID results = this.codeService.executeCode(taskEntity, payload, testInputs);
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
