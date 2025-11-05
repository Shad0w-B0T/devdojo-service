package devdojo.rest.exception;

import devdojo.logic.cache.bucket4j.exception.RateLimitExceededException;
import devdojo.logic.cache.bucket4j.exception.RateLimitTimeoutException;
import devdojo.schema.exception.HttpException;
import devdojo.schema.exception.permissions.UnauthenticatedException;
import devdojo.schema.exception.permissions.UnauthorizedException;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;

public interface IExceptionHandler {

  @NotNull ResponseEntity<ErrorResponse> handleUnauthorized(final UnauthorizedException exception);

  @NotNull ResponseEntity<ErrorResponse> handleUnauthenticated(final UnauthenticatedException exception);

  @NotNull ResponseEntity<ErrorResponse> handleRateLimitExceeded(final RateLimitExceededException exception);

  @NotNull ResponseEntity<ErrorResponse> handleRateLimitTimeout(final RateLimitTimeoutException exception);

  @NotNull ResponseEntity<ErrorResponse> handleNotFound(final Exception exception);

  @NotNull ResponseEntity<ErrorResponse> handleConflict(final Exception exception);

  @NotNull ResponseEntity<ErrorResponse> handleHttp(final HttpException exception);

  @NotNull  ResponseEntity<ErrorResponse> handleRuntimeException(@NotNull final RuntimeException exception);

  @NotNull  ResponseEntity<ErrorResponse> handleException(@NotNull final Exception exception);

  @NotNull  ResponseEntity<ErrorResponse> handleThrowable(@NotNull final Throwable throwable);
}
