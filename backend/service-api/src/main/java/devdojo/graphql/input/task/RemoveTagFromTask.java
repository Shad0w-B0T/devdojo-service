package devdojo.graphql.input.task;


import devdojo.logic.task.ITaskService;
import java.util.UUID;

public record RemoveTagFromTask(String taskId, UUID tagId) implements ITaskService.RemoveTagFromTask {
}
