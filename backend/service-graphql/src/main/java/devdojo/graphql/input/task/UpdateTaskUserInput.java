package devdojo.graphql.input.task;

import devdojo.logic.security.SecurityUtils;
import devdojo.logic.task.ITasksProgressService;
import java.util.UUID;

public record UpdateTaskUserInput(UUID userId,
                                  UUID taskId,
                                  String input) implements ITasksProgressService.UpdateTaskProgressInput {

  public UpdateTaskUserInput(final UUID userId,
                             final UUID taskId,
                             final String input) {
    this.userId = userId != null ? userId : SecurityUtils.localThreadClient().userId();
    this.taskId = taskId;
    this.input = input;
  }

}
