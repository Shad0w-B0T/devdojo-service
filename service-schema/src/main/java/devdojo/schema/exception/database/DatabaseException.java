package devdojo.schema.exception.database;

import jakarta.validation.constraints.NotNull;

public class DatabaseException extends RuntimeException {


  public DatabaseException(@NotNull(message = "Message cannot be null.") final String message) {
    super(message);
  }

  public DatabaseException(@NotNull(message = "Message cannot be null.") final String message,
                           @NotNull(message = "Cause cannot be null.") final Throwable cause) {
    super(message, cause);
  }
}
