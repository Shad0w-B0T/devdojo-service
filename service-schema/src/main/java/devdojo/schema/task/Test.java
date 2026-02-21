package devdojo.schema.task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.*;
import java.util.List;

public record Test(@NotNull(message = "Input arguments list must not be null")
                   @Valid
                   List<TestArgument> input,

                   @NotNull(message = "Output argument must not be null")
                   @Valid
                   TestArgument output) implements ITest, Serializable {
}
