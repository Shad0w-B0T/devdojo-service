package devdojo.graphql.input.task;

import devdojo.logic.task.ITaskService;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;


public record GetTasks(@NotNull(message = "LanguageId cannot be null.") UUID languageId) implements ITaskService.GetTasks {
}
