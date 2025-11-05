package devdojo.schema.exception;

import devdojo.schema.exception.permissions.UnauthorizedException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UnauthorizedException Tests")
final class UnauthorizedExceptionTest {

  @Test
  @DisplayName("should create UnauthorizedException with message only")
  void shouldCreateExceptionWithMessage() {
    final String message = "User not authorized";

    final UnauthorizedException exception = new UnauthorizedException(message);

    assertEquals(message, exception.getMessage(), "Exception message should match");
    assertNull(exception.getCause(), "Exception cause should be null");
  }

  @Test
  @DisplayName("should create UnauthorizedException with message and cause")
  void shouldCreateExceptionWithMessageAndCause() {
    final String message = "User not authorized";
    final Throwable cause = new RuntimeException("Access denied");

    final UnauthorizedException exception = new UnauthorizedException(message, cause);

    assertEquals(message, exception.getMessage(), "Exception message should match");
    assertEquals(cause, exception.getCause(), "Exception cause should match");
  }
}

