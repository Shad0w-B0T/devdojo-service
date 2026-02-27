package devdojo.logic.compiler;

import devdojo.logic.cache.compiler.CompilerTask;
import devdojo.logic.cache.compiler.CompilerTaskResult;
import devdojo.logic.database.compiler.CompilerEntity;
import devdojo.logic.database.compiler.CompilerHeartbeatEntity;
import devdojo.schema.code.TestRunResult;
import devdojo.schema.compiler.CompilerResourceAnalytic;
import devdojo.schema.task.ITest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.UUID;

public interface ICompilerService {

  @NotNull(message = "Returned hearbeats must not be null")
  CompilerHeartbeatEntity updateHeartbeat(
    @NotNull(message = "UpdateHeartbeat must not be null") final UpdateHeartbeat updateHeartbeat);

  @NotNull
  CompilerTaskResult getCompilerTask(@NotNull final GetCompilerTask getCompilerTask);

  @NotNull
  CompilerTask createCompilerTask(@NotNull final CreateCompilerTask createCompilerTask);

  @NotNull
  CompilerTaskResult completeCompilerTask(@NotNull final CompleteCompilerTask completeCompilerTask);

  void cleanup();

  @NotNull
  List<CompilerEntity> getCompilers();

  @NotNull
  CompilerEntity getCompiler(@NotNull final GetCompiler getCompiler);

  @NotNull
  CompilerHeartbeatEntity getLastCompilerHeartbeat(@NotNull final GetCompiler getCompiler);

  @NotNull
  List<CompilerResourceAnalytic> getCompilerResourceAnalytics(@NotNull final GetCompiler getCompiler);

  record GetHeartbeats(

    @NotNull(message = "Token must not be null")
    String token

  ) {
  }

  record UpdateHeartbeat(

    @NotNull(message = "Id must not be null")
    UUID id,

    @NotNull(message = "LanguageToken must not be null")
    String languageToken,

    double cpuUsagePercent,

    @Positive(message = "CPU cores must be greater than 0")
    int cpuCores,

    @PositiveOrZero(message = "Memory usage must be 0 or positive")
    double memoryUsageMb,

    @Positive(message = "Total memory must be greater than 0")
    double memoryTotalMb,

    @PositiveOrZero(message = "Total tasks processed must be 0 or positive")
    long totalTasksProcessed

  ) {
  }

  record GetCompilerTask(

    @NotNull(message = "Id must not be null")
    UUID id

  ) {
  }

  record CreateCompilerTask(

    @NotNull(message = "Id must not be null")
    UUID id,

    @NotNull(message = "Method code must not be null")
    String methodCode,

    @NotNull(message = "Unit tests list must not be null")
    List<ITest> unitTests,

    @Null
    String taskId,

    @NotNull(message = "LanguageId must not be null")
    String languageId
  ) {
  }

  record CompleteCompilerTask(

    @NotNull(message = "Id must not be null")
    UUID id,

    @NotNull(message = "Results must not be null")
    @Valid
    List<TestRunResult> results

  ) {
  }

  record GetCompiler(

    @NotNull(message = "Id must not be null")
    UUID id

  ) {
  }
}
