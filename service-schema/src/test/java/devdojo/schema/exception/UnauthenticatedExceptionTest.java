package devdojo.schema.exception;

import devdojo.schema.exception.permissions.UnauthenticatedException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UnauthenticatedException Tests")
final class UnauthenticatedExceptionTest {

  @Test
  @DisplayName("should create UnauthenticatedException with message only")
  void shouldCreateExceptionWithMessage() {
    final String message = "User not authenticated";

    final UnauthenticatedException exception = new UnauthenticatedException(message);

    assertEquals(message, exception.getMessage(), "Exception message should match");
    assertNull(exception.getCause(), "Exception cause should be null");
  }

  @Test
  @DisplayName("should create UnauthenticatedException with message and cause")
  void shouldCreateExceptionWithMessageAndCause() {
    final String message = "User not authenticated";
    final Throwable cause = new RuntimeException("Token expired");

    final UnauthenticatedException exception = new UnauthenticatedException(message, cause);

    assertEquals(message, exception.getMessage(), "Exception message should match");
    assertEquals(cause, exception.getCause(), "Exception cause should match");
  }
}
