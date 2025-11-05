package devdojo.logic.code;

import devdojo.logic.database.task.TaskEntity;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface ICodeService {
  /**
   * Executes the given Java static method code with the provided argument sets.
   *
   * @param taskEntity
   * @param methodCode   The full static Java method code as a String.
   * @param argumentSets A list of argument sets to be passed to the method.
   *                     Each argument set is a list of objects representing parameters for one invocation.
   * @return A list of {@link devdojo.schema.code.TestRunResult} objects containing output, errors, and execution timings for each invocation.
   */
  @NotNull
  UUID executeCode(@NotNull final TaskEntity taskEntity,
                   @NotNull final String methodCode,
                   @NotNull final List<List<Object>> argumentSets
  );
}
