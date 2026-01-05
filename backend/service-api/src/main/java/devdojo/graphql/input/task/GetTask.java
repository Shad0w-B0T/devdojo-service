package devdojo.graphql.input.task;

import devdojo.logic.task.ITaskService;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GetTask(@NotNull(message = "TaskId cannot be null.") UUID taskId) implements ITaskService.GetTask {
}
