package devdojo.schema.exception.database;

import devdojo.schema.exception.AbstractException;
import jakarta.validation.constraints.NotNull;

/**
 * Exception thrown when attempting to create or register an object that already exists in the system.
 *
 * <p>This exception is a subclass of {@link AbstractException} and is thrown when an object with the
 * same value already exists in the repository.</p>
 */
public class AlreadyExistsException extends AbstractException {
  public AlreadyExistsException(@NotNull final String value) {
    super("Object with the value: '" + value + "' already exists.");
  }
}
