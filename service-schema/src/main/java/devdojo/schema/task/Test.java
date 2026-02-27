package devdojo.schema.task;

import devdojo.schema.Constants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.*;
import java.util.List;

public record Test(@NotNull(message = "Input arguments list must not be null")
                   @Size(max = Constants.TEST_ARGUMENT_LIMIT, message = "Input arguments must not exceed {max} entries")
                   @Valid
                   List<TestArgument> input,

                   @NotNull(message = "Output argument must not be null")
                   @Valid
                   TestArgument output

) implements ITest, Serializable {
}
