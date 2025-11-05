package devdojo.schema.exception.database;

import devdojo.schema.exception.AbstractException;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Exception thrown when an object with the specified UUID cannot be found in the system.
 *
 * <p>This exception is a subclass of {@link AbstractException} and is thrown when an operation
 * attempts to access an object that does not exist in the repository or database.</p>
 */
public class NotFoundException extends AbstractException {
  public NotFoundException(@NotNull(message = "ID cannot be null.") final UUID id) {
    this("Object with UUID: '" + id + "' not found.");
  }

  public NotFoundException(@NotNull(message = "Message cannot be null.") final String message) {
    super(message);
  }
}
