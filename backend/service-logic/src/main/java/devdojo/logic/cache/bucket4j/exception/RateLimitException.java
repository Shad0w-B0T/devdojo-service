package devdojo.logic.cache.bucket4j.exception;


public abstract class RateLimitException extends RuntimeException {

  public RateLimitException(final String message) {
    super(message);
  }
}
