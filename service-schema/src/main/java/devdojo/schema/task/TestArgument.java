package devdojo.schema.task;

import jakarta.validation.constraints.NotNull;
import java.io.*;

public record TestArgument(@NotNull(message = "Argument type must not be null")
                           ArgumentType type,

                           String variant, // optional

                           @NotNull(message = "Argument value must not be null")
                           Object value

) implements ITestArgument, Serializable {
}
