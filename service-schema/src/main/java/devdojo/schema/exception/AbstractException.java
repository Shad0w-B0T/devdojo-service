package devdojo.schema.exception;

import devdojo.schema.exception.database.AlreadyExistsException;
import devdojo.schema.exception.database.DeletionException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract base class for all exceptions related to user operations.
 *
 * <p>This class extends {@link RuntimeException} and serves as a parent for all custom user-related
 * exceptions such as {@link AlreadyExistsException} and {@link DeletionException}.
 * It logs the error message whenever a user-related exception is thrown.</p>
 */
@Slf4j
public abstract class AbstractException extends RuntimeException {
  public AbstractException(@NotNull(message = "Message cannot be null.") final String message) {
    super(message);
    log.error(message);
  }

  public AbstractException(@NotNull(message = "Message cannot be null.") final String message,
                           @NotNull(message = "Cause cannot be null.") final Throwable cause) {
    super(message, cause);
    log.error(message, cause);
  }
}
