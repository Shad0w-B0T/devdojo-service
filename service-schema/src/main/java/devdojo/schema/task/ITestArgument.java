package devdojo.schema.task;

import jakarta.validation.constraints.NotNull;

public interface ITestArgument {

  @NotNull(message = "Type cannot be null.")
  ArgumentType type();

  String variant();

  @NotNull(message = "Value cannot be null.")
  Object value();

}
