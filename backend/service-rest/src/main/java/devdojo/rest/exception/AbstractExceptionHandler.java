package devdojo.rest.exception;

import devdojo.logic.cache.bucket4j.exception.RateLimitExceededException;
import devdojo.logic.cache.bucket4j.exception.RateLimitTimeoutException;
import devdojo.schema.exception.HttpException;
import devdojo.schema.exception.InvalidValueException;
import devdojo.schema.exception.database.AlreadyExistsException;
import devdojo.schema.exception.database.DeletionException;
import devdojo.schema.exception.database.IllegalInputException;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.exception.permissions.UnauthenticatedException;
import devdojo.schema.exception.permissions.UnauthorizedException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

public abstract class AbstractExceptionHandler implements IExceptionHandler {

  @ExceptionHandler(UnauthorizedException.class)
  @Override
  public ResponseEntity<ErrorResponse> handleUnauthorized(final UnauthorizedException exception) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
      "Request Unauthorized",
      HttpStatus.UNAUTHORIZED,
      Instant.now().toString()
    ));
  }

  @ExceptionHandler(UnauthenticatedException.class)
  @Override
  public ResponseEntity<ErrorResponse> handleUnauthenticated(final UnauthenticatedException exception) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
      "Request unauthenticated",
      HttpStatus.FORBIDDEN,
      Instant.now().toString()
    ));
  }

  @ExceptionHandler(RateLimitExceededException.class)
  @Override
  public ResponseEntity<ErrorResponse> handleRateLimitExceeded(final RateLimitExceededException exception) {
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(new ErrorResponse(
      "Wait before you send any more requests.",
      HttpStatus.TOO_MANY_REQUESTS,
      Instant.now().toString()
    ));
  }

  @ExceptionHandler(RateLimitTimeoutException.class)
  @Override
  public ResponseEntity<ErrorResponse> handleRateLimitTimeout(final RateLimitTimeoutException exception) {
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(new ErrorResponse(
      "Wait before you send any more requests, the queue is also full.",
      HttpStatus.TOO_MANY_REQUESTS,
      Instant.now().toString()
    ));
  }

  @ExceptionHandler({NotFoundException.class, ResponseStatusException.class})
  @Override
  public ResponseEntity<ErrorResponse> handleNotFound(final Exception exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
      "Element not found.",
      HttpStatus.NOT_FOUND,
      Instant.now().toString()
    ));
  }

  @ExceptionHandler({IllegalInputException.class, AlreadyExistsException.class, DeletionException.class,
    InvalidValueException.class})
  @Override
  public ResponseEntity<ErrorResponse> handleConflict(final Exception exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
      "Conflict with document.",
      HttpStatus.CONFLICT,
      Instant.now().toString()
    ));
  }

  @ExceptionHandler(value = HttpException.class)
  @Override
  public ResponseEntity<ErrorResponse> handleHttp(final HttpException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
      "An error occurred while processing the request.",
      HttpStatus.BAD_REQUEST,
      Instant.now().toString()
    ));
  }

  @ExceptionHandler(value = RuntimeException.class)
  @Override
  public ResponseEntity<ErrorResponse> handleRuntimeException(final RuntimeException exception) {
    final ErrorResponse errorResponse = new ErrorResponse(
      "An unexpected error occurred.",
      HttpStatus.INTERNAL_SERVER_ERROR,
      Instant.now().toString()
    );

    return ResponseEntity
      .status(errorResponse.httpStatus())
      .body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  @Override
  public ResponseEntity<ErrorResponse> handleException(final Exception exception) {
    final ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred.",
      HttpStatus.INTERNAL_SERVER_ERROR,
      Instant.now().toString()
    );

    return ResponseEntity
      .status(errorResponse.httpStatus())
      .body(errorResponse);
  }

  @ExceptionHandler(Throwable.class)
  @Override
  public ResponseEntity<ErrorResponse> handleThrowable(final Throwable throwable) {
    final ErrorResponse errorResponse = new ErrorResponse(
      "An unexpected error occurred.",
      HttpStatus.INTERNAL_SERVER_ERROR,
      Instant.now().toString()
    );

    return ResponseEntity
      .status(errorResponse.httpStatus())
      .body(errorResponse);
  }
}
