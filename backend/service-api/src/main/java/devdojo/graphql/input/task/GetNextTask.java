package devdojo.graphql.input.task;

import devdojo.logic.task.ITasksProgressService;
import devdojo.schema.task.TaskProgressState;
import java.util.UUID;

public record GetNextTask(UUID userId,
                          UUID languageId,
                          TaskProgressState state) implements ITasksProgressService.GetNextTask {
}
