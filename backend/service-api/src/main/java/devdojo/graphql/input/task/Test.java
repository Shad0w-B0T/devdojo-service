package devdojo.graphql.input.task;

import devdojo.logic.task.ITaskService;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record Test(@NotNull(message = "Input cannot be null.") List<TestArgument> input,
                   @NotNull(message = "Output cannot be null.") TestArgument output) implements ITaskService.TestData {
}
