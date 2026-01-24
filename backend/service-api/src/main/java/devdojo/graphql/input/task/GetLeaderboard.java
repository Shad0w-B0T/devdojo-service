package devdojo.graphql.input.task;

import devdojo.logic.task.ITasksProgressService;
import java.util.UUID;

public record GetLeaderboard(String languageId,
                             int start,
                             int end) implements ITasksProgressService.GetLeaderboard {
}
