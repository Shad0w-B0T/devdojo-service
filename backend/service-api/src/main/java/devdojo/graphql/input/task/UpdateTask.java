package devdojo.graphql.input.task;

import devdojo.logic.task.ITaskService;
import devdojo.schema.task.Difficulty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record UpdateTask(
  @NotNull(message = "TaskId cannot be null.") String taskId,
  @NotNull(message = "Name cannot be null.") String name,
  @NotNull(message = "Description cannot be null.") String description,
  @NotNull(message = "Difficulty cannot be null.") Difficulty difficulty,
  @NotNull(message = "Template cannot be null.") String template,
  @NotNull(message = "Complete cannot be null.") String complete,
  @NotNull(message = "UnitTests cannot be null.") List<Test> unitTests,
  @Nullable List<String> tags,
  @NotNull(message = "Active cannot be null.") boolean active
) implements ITaskService.UpdateTask {
}
