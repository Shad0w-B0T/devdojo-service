package devdojo.rest.exception;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;

/**
 * Creates a new {@code ErrorResponse}.
 *
 * @param message    the error message describing the problem; must not be {@code null}
 * @param httpStatus the HTTP status to be returned; must not be {@code null}
 * @param timestamp  the timestamp when the error occurred in ISO-8601 format; must not be {@code null}
 */
public record ErrorResponse(@NotNull String message,
                            @NotNull HttpStatus httpStatus,
                            @NotNull String timestamp) {
}
