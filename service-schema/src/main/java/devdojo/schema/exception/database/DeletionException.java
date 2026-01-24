package devdojo.schema.exception.database;

import devdojo.schema.exception.AbstractException;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Exception thrown when an error occurs during the deletion of a user from the system.
 *
 * <p>This exception is a subclass of {@link AbstractException} and is thrown when a user could not
 * be successfully deleted, typically because the user still exists in the repository after
 * the deletion attempt.</p>
 */
public class DeletionException extends AbstractException {
  public DeletionException(@NotNull(message = "ID cannot be null.") final String uuid) {
    super("User with UUID: '" + uuid + "' could not be deleted.");
  }
}
