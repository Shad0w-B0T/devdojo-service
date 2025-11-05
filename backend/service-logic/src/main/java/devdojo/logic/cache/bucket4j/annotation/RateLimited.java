package devdojo.logic.cache.bucket4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to apply rate limiting to a method.
 * <p>
 * This annotation is used to protect a method with a defined token bucket.
 * which can optionally be a SpEL expression.
 * <p>
 * Example:
 * {@code
 *
 * @RateLimited(bucket = "#user.id", bean = "apiRateLimiter", tokens = 2, waitForToken = 100)
 * public void someMethod(User user) {
 * // protected logic
 * }
 * }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
  /**
   * The name of the bucket, typically defined using a SpEL expression.
   * Can also be a plain static string like "global".
   *
   * @return the identifier of the bucket
   */
  String bucket();

  long totalTokens();

  long refillDurationMillis();

  /**
   * Number of tokens to consume per method call.
   * Default is 1.
   *
   * @return how many tokens to consume
   */
  long consumeTokens() default 1;

  /**
   * Maximum time to wait (in milliseconds) to acquire the tokens.
   * If set to 0, it will fail immediately if tokens are not available.
   *
   * @return how long to wait for tokens before failing
   */
  long waitForToken() default 0;
}
