package devdojo.logic.cache.bucket4j.exception;

public final class RateLimitExceededException extends RateLimitException {

  public RateLimitExceededException(final String message) {
    super(message);
  }
}
