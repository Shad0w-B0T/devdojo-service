package devdojo.schema.exception.permissions;

import devdojo.schema.exception.AbstractException;
import jakarta.validation.constraints.NotNull;

public final class UnauthorizedException extends AbstractException {
  public UnauthorizedException(@NotNull(message = "Message cannot be null.") final String message) {
    super(message);
  }

  public UnauthorizedException(@NotNull(message = "Message cannot be null.") final String message,
                               @NotNull(message = "Cause cannot be null.") final Throwable cause) {
    super(message, cause);
  }
}
