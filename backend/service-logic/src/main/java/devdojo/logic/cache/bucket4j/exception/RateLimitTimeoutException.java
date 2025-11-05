package devdojo.logic.cache.bucket4j.exception;

public final class RateLimitTimeoutException extends RateLimitException {

  public RateLimitTimeoutException(final String message) {
    super(message);
  }
}
