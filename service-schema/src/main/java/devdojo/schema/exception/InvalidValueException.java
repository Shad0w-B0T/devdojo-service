package devdojo.schema.exception;

import jakarta.validation.constraints.NotNull;

/**
 * Exception thrown when an object attempts to set or register an invalid value.
 *
 * <p>This exception is a subclass of {@link AbstractException} and is thrown when the provided
 * value does not meet the required criteria (e.g., invalid characters, length, etc.).</p>
 */
public class InvalidValueException extends AbstractException {
  public InvalidValueException(@NotNull(message = "Value cannot be null.") final String value) {
    super("Value '" + value + "' is invalid for this object.");
  }

  public InvalidValueException(@NotNull(message = "Message cannot be null.") final String message,
                               @NotNull(message = "Cause cannot be null.") final Throwable cause) {
    super(message, cause);
  }
}
