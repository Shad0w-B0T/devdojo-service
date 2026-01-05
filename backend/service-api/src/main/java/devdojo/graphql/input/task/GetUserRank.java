package devdojo.graphql.input.task;

import devdojo.logic.task.ITasksProgressService;
import java.util.UUID;

public record GetUserRank(UUID userId,
                          UUID languageId) implements ITasksProgressService.GetUserRank {
}
