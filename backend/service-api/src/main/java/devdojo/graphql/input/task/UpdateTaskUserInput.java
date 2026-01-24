package devdojo.graphql.input.task;

import devdojo.logic.security.SecurityUtils;
import devdojo.logic.task.ITasksProgressService;
import java.util.UUID;

public record UpdateTaskUserInput(String userId,
                                  String taskId,
                                  String input) implements ITasksProgressService.UpdateTaskProgressInput {

  public UpdateTaskUserInput(final String userId,
                             final String taskId,
                             final String input) {
    this.userId = userId != null ? userId : SecurityUtils.currentTokenDetails().userId();
    this.taskId = taskId;
    this.input = input;
  }

}
