package devdojo.graphql.input.task;

import devdojo.logic.task.ITaskService;
import devdojo.schema.task.Difficulty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CreateTask(
  @NotNull(message = "LanguageId cannot be null.") UUID languageId,
  @NotNull(message = "Name cannot be null.") String name,
  @NotNull(message = "Description cannot be null.") String description,
  @NotNull(message = "Difficulty cannot be null.") Difficulty difficulty,
  @NotNull(message = "Template cannot be null.") String template,
  @NotNull(message = "Complete cannot be null.") String complete,
  @NotNull(message = "UnitTests cannot be null.") List<Test> unitTests,
  @NotNull(message = "Active cannot be null.") boolean active
) implements ITaskService.CreateTask {
}
