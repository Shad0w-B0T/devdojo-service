package devdojo.schema.exception;

import jakarta.validation.constraints.NotNull;

public class HttpException extends AbstractException {
  public HttpException(@NotNull(message = "Message cannot be null.") final String message) {
    super(message);
  }

  public HttpException(@NotNull(message = "Message cannot be null.") final String message,
                       @NotNull(message = "Cause cannot be null.") final Throwable cause) {
    super(message, cause);
  }
}
