package devdojo.graphql.input.task;

import devdojo.logic.task.ITaskService;
import devdojo.schema.task.ArgumentType;
import jakarta.validation.constraints.NotNull;

public record TestArgument(@NotNull(message = "Type cannot be null.") ArgumentType type,
                           String variant,
                           @NotNull(message = "Value cannot be null.") Object value) implements ITaskService.TestArgumentData {
}
