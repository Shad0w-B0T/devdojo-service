package devdojo.graphql.input.task;

import devdojo.logic.task.ITasksProgressService;
import java.util.UUID;

public record GetUserRank(String userId,
                          String languageId) implements ITasksProgressService.GetUserRank {
}
