package devdojo.graphql.input.task;

import devdojo.logic.task.ITaskService;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AppendTest(@NotNull(message = "TaskId cannot be null.") String taskId,
                         @NotNull(message = "UnitTest cannot be null.") Test unitTest
) implements ITaskService.AppendTest {
}
