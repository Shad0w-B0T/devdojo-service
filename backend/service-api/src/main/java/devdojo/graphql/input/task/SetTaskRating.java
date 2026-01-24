package devdojo.graphql.input.task;

import devdojo.logic.task.ITaskService;
import java.util.UUID;

public record SetTaskRating(String taskId,
                            String userId,
                            int rating) implements ITaskService.SetTaskRating {
}
