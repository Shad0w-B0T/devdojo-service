package devdojo.rest.controller;

import devdojo.logic.cache.bucket4j.annotation.RateLimited;
import devdojo.logic.code.ICodeService;
import devdojo.logic.language.ILanguageService;
import devdojo.logic.task.ITaskService;
import devdojo.schema.task.ITest;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/code")
@Slf4j
@Validated
public abstract class AbstractCodeController implements ICodeController {

  private static final long CODE_RATE_LIMIT_REFILL = 1000;

  private final ITaskService taskService;
  private final ICodeService codeService;
  private final ILanguageService languageService;

  public AbstractCodeController(
    @NotNull(message = "TaskService must not be null") final ITaskService taskService,
    @NotNull(message = "CodeService must not be null") final ICodeService codeService,
    @NotNull(message = "LanguageService must not be null") final ILanguageService languageService
  ) {
    this.taskService = taskService;
    this.codeService = codeService;
    this.languageService = languageService;
  }

  @RateLimited(
    bucket = "#{'rate-code-' + T(devdojo.logic.security.SecurityUtils).currentTokenDetails().userId()}",
    refillDurationMillis = CODE_RATE_LIMIT_REFILL,
    totalTokens = 1)
  @PostMapping("/exercises")
  @ResponseBody
  @Override
  public ResponseEntity<ProcessCodeResponse> processCode(@RequestBody final String payload,
                                                         @RequestParam(value = "taskId") final String taskId) {
    final var taskEntity = this.taskService.getTask(new ITaskService.GetTask(taskId));
    final UUID results = this.codeService.executeCode(taskEntity, payload);
    return ResponseEntity.ok(new ProcessCodeResponse(results));
  }

  @RateLimited(
    bucket = "#{'rate-code-' + T(devdojo.logic.security.SecurityUtils).currentTokenDetails().userId()}",
    refillDurationMillis = CODE_RATE_LIMIT_REFILL,
    totalTokens = 1)
  @PostMapping("/exercises/raw")
  @ResponseBody
  @Override
  public ResponseEntity<ProcessCodeResponse> processCodeWithTests(
    @RequestBody final ExecuteCodeWithTestsRequest request) {
    final var language = this.languageService.getLanguage(
      new ILanguageService.GetLanguage(request.languageId())
    );

    final UUID results = this.codeService.executeCode(
      language,
      null,
      request.payload(),
      request.unitTests().stream().map(test -> (ITest) test).toList()
    );

    return ResponseEntity.ok(new ProcessCodeResponse(results));
  }
}
