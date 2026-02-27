package devdojo.schema.task;

import devdojo.schema.Constants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.*;

public record TestArgument(@NotNull(message = "Argument type must not be null")
                           ArgumentType type,

                           @Size(max = Constants.TEST_ARGUMENT_VARIANT_LENGTH,
                             message = "Variant must not exceed {max} characters")
                           String variant, // optional

                           @NotNull(message = "Argument value must not be null")
                           Object value

) implements ITestArgument, Serializable {
}
