package devdojo.logic.compiler;

import devdojo.logic.cache.compiler.CompilerTask;
import devdojo.logic.cache.compiler.CompilerTaskResult;
import devdojo.logic.database.compiler.CompilerEntity;
import devdojo.logic.database.compiler.CompilerHeartbeatEntity;
import devdojo.schema.code.TestRunResult;
import devdojo.schema.compiler.CompilerResourceAnalytic;
import jakarta.validation.constraints.NotNull;
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

  interface GetHeartbeats {
    @NotNull(message = "Token must not be null")
    String token();
  }


  record GetHeartbeatsRecord(String token) implements GetHeartbeats {
  }

  interface UpdateHeartbeat {
    @NotNull(message = "Id must not be null")
    UUID id();

    @NotNull(message = "LanguageToken must not be null")
    String languageToken();

    double cpuUsagePercent();

    @Positive(message = "CPU cores must be greater than 0")
    int cpuCores();

    @PositiveOrZero(message = "Memory usage must be 0 or positive")
    double memoryUsageMb();

    @Positive(message = "Total memory must be greater than 0")
    double memoryTotalMb();

    @PositiveOrZero(message = "Total tasks processed must be 0 or positive")
    long totalTasksProcessed();
  }

  record GetCompilerTask(UUID id) {

  }

  record CreateCompilerTask(UUID id,
                            String methodCode,
                            String taskId) {

  }

  record CompleteCompilerTask(UUID id,
                              List<TestRunResult> results) {

  }

  record GetCompiler(UUID id) {
  }

  record UpdateHeartbeatRecord(
    UUID id,
    String languageToken,
    double cpuUsagePercent,
    int cpuCores,
    double memoryUsageMb,
    double memoryTotalMb,
    long totalTasksProcessed
  ) implements UpdateHeartbeat {
  }
}
