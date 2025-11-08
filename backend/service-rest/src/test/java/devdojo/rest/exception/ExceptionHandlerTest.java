package devdojo.rest.exception;

import devdojo.logic.cache.bucket4j.exception.RateLimitExceededException;
import devdojo.schema.exception.HttpException;
import devdojo.schema.exception.database.AlreadyExistsException;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.exception.permissions.UnauthenticatedException;
import devdojo.schema.exception.permissions.UnauthorizedException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class ExceptionHandlerTest {

  private AbstractExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new AbstractExceptionHandler() {
    };
  }

  @Test
  @DisplayName("Should return 401 for UnauthorizedException")
  void testHandleUnauthorized() {
    final ResponseEntity<ErrorResponse> response =
      handler.handleUnauthorized(new UnauthorizedException(""));

    assertEquals(401, response.getStatusCodeValue());
    assertEquals("Request Unauthorized", response.getBody().message());
  }

  @Test
  @DisplayName("Should return 403 for UnauthenticatedException")
  void testHandleUnauthenticated() {
    final ResponseEntity<ErrorResponse> response =
      handler.handleUnauthenticated(new UnauthenticatedException(""));

    assertEquals(403, response.getStatusCodeValue());
    assertEquals("Request unauthenticated", response.getBody().message());
  }

  @Test
  @DisplayName("Should return 429 for RateLimitExceededException")
  void testHandleRateLimitExceeded() {
    final ResponseEntity<ErrorResponse> response =
      handler.handleRateLimitExceeded(new RateLimitExceededException(""));

    assertEquals(429, response.getStatusCodeValue());
    assertEquals("Wait before you send any more requests.", response.getBody().message());
  }

  @Test
  @DisplayName("Should return 404 for NotFoundException")
  void testHandleNotFound() {
    final ResponseEntity<ErrorResponse> response =
      handler.handleNotFound(new NotFoundException("Not found"));

    assertEquals(404, response.getStatusCodeValue());
    assertEquals("Element not found.", response.getBody().message());
  }

  @Test
  @DisplayName("Should return 409 for AlreadyExistsException")
  void testHandleConflict() {
    final ResponseEntity<ErrorResponse> response =
      handler.handleConflict(new AlreadyExistsException("Conflict"));

    assertEquals(409, response.getStatusCodeValue());
    assertEquals("Conflict with document.", response.getBody().message());
  }

  @Test
  @DisplayName("Should return 400 for HttpException")
  void testHandleHttp() {
    final ResponseEntity<ErrorResponse> response =
      handler.handleHttp(new HttpException("Bad request"));

    assertEquals(400, response.getStatusCodeValue());
    assertEquals("An error occurred while processing the request.", response.getBody().message());
  }

  @Test
  @DisplayName("Should return 500 for RuntimeException")
  void testHandleRuntimeException() {
    final ResponseEntity<ErrorResponse> response =
      handler.handleThrowable(new RuntimeException("Boom"));

    assertEquals(500, response.getStatusCodeValue());
    assertEquals("An unexpected error occurred.", response.getBody().message());
  }

  @Test
  @DisplayName("Should return 500 for Throwable")
  void testHandleThrowable() {
    final ResponseEntity<ErrorResponse> response =
      handler.handleThrowable(new Throwable("Crash"));

    assertEquals(500, response.getStatusCodeValue());
    assertEquals("An unexpected error occurred.", response.getBody().message());
  }

  @Test
  @DisplayName("Should return 500 for generic Exception")
  void testHandleGenericException() {
    final ResponseEntity<ErrorResponse> response =
      handler.handleThrowable(new Exception("Unknown"));

    assertEquals(500, response.getStatusCodeValue());
    assertEquals("An unexpected error occurred.", response.getBody().message());
  }
}
